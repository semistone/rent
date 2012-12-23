package org.siraya.rent.user.service;

import org.siraya.rent.user.dao.IRoleDao;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.pojo.Session;
import org.siraya.rent.pojo.User;
import org.siraya.rent.user.dao.IUserOnlineStatusDao;
import org.siraya.rent.user.dao.ISessionDao;
import org.siraya.rent.user.dao.IUserOnlineStatusDao;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.siraya.rent.pojo.Role;
import org.siraya.rent.pojo.UserOnlineStatus;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

@Service("sessionService")
public class SessionService implements ISessionService {

	@Autowired
	private ISessionDao sessionDao;

	@Autowired
	private IApplicationConfig applicationConfig;

	@Autowired
	private IDeviceDao deviceDao;
	@Autowired
	private IRoleDao roleDao;
	@Autowired
	private IUserOnlineStatusDao userOnlineStatusDao;



	private static Logger logger = LoggerFactory
			.getLogger(SessionService.class);

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public void newSession(Session session) {
		logger.debug("new session");

		try {
			String data = (String) applicationConfig.get("general").get(
					"geoip_data");
			LookupService cl = new LookupService(data,
					LookupService.GEOIP_MEMORY_CACHE);
			Location l1 = cl.getLocation(session.getLastLoginIp());
			session.setCity(l1.city);
			session.setCountry(l1.countryName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.sessionDao.newSession(session);
		String deviceId = session.getDeviceId();
		String userId = session.getUserId();
		Device device = this.deviceDao.getDeviceByDeviceIdAndUserId(deviceId,
				userId);
		if (device == null) {
			throw new RentException(
					RentException.RentErrorCode.ErrorDeviceNotFound,
					"device data not found");
		} else {
			int ret = this.deviceDao.updateLastLoginIp(session);
			if (ret != 1) {
				throw new RentException(
						RentException.RentErrorCode.ErrorNotFound,
						"update last login ip in device fail");
			}
		}
		if (device.getStatus() == DeviceStatus.Authed.getStatus()) {
			logger.debug("add role device authed");
			session.setDeviceVerified(true);
			//
			// only authed device can have roles.
			//
			logger.debug("add roles from db");
			List<Role> roles = this.roleDao.getRoleByUserId(userId);
			int size = roles.size();
			for (int i = 0; i < size; i++) {
				int role = roles.get(i).getRoleId();
				session.addRole(role);
				logger.debug("add role " + role + " into user" + userId);
			}
		}
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Session> getSessions(Device device, int limit, int offset) {
		List<Session> sessions = this.sessionDao.getSessions(
				device.getUserId(), device.getId(), limit, offset);
		if (sessions.size() == 0) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"no session found");
		}
		return sessions;
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getRoles(String userId) {
		return this.roleDao.getRoleByUserId(userId);
	}

	public void setSessionDao(ISessionDao sessionDao) {
		this.sessionDao = sessionDao;
	}

	public void setDeviceDao(IDeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}

	public IRoleDao getRoleDao() {
		return roleDao;
	}

	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
	}

	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false)
	public void connect(Session session) {
		if (session.getCallback() == null) {
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"callback is null");
		}
		session.setOnlineStatus(1);
		this.sessionDao.updateOnlineStatus(session);
		UserOnlineStatus user = new UserOnlineStatus();
		user.setId(session.getUserId());
		user.setOnlineStatus(1);
		if (userOnlineStatusDao.updateOnlineStatus(user) == 0){
			userOnlineStatusDao.insert(user);	
		}
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false)
	public void disconnect(Session session) {
		session.setOnlineStatus(0);
		session.setCallback(null);
		this.sessionDao.updateOnlineStatus(session);
		int count = this.sessionDao.getUserOnlineStatusFromSessions(session
				.getUserId());
		if (count == 0) {
			UserOnlineStatus user = new UserOnlineStatus();
			user.setId(session.getUserId());
			user.setOnlineStatus(0);
			userOnlineStatusDao.updateOnlineStatus(user);
		}
	}
	
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
    public List<String> callbacks(String userId){
		return this.sessionDao.callbacks(userId);
    }

    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
    public List<UserOnlineStatus> list(List<String> ids){
    	java.lang.StringBuffer buf=new java.lang.StringBuffer();
    	for(String tmp:ids){
    		buf.append(tmp);
    		buf.append(",");
    	}
    	String tmpids = buf.substring(0, buf.length()-1);
    	return this.userOnlineStatusDao.list(tmpids);
    }

    public IUserOnlineStatusDao getUserOnlineStatusDao() {
        return userOnlineStatusDao;
    }

    public void setUserOnlineStatusDao(IUserOnlineStatusDao userOnlineStatusDao) {
        this.userOnlineStatusDao = userOnlineStatusDao;
    }
}
