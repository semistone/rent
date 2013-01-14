package org.siraya.rent.user.service;
<<<<<<< HEAD
import org.siraya.rent.user.dao.IRoleDao;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.pojo.Session;
import org.siraya.rent.user.dao.ISessionDao;
import org.siraya.rent.user.dao.IDeviceDao;
=======

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
>>>>>>> master
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.siraya.rent.pojo.Role;
<<<<<<< HEAD
import org.siraya.rent.pojo.Device;
=======
import org.siraya.rent.pojo.UserOnlineStatus;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

>>>>>>> master
@Service("sessionService")
public class SessionService implements ISessionService {

	@Autowired
<<<<<<< HEAD
    private ISessionDao sessionDao;


	@Autowired
    private IDeviceDao deviceDao;
	@Autowired
    private IRoleDao roleDao;

	private static Logger logger = LoggerFactory.getLogger(SessionService.class);
=======
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
>>>>>>> master

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public void newSession(Session session) {
		logger.debug("new session");
<<<<<<< HEAD
=======

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
>>>>>>> master
		this.sessionDao.newSession(session);
		String deviceId = session.getDeviceId();
		String userId = session.getUserId();
		Device device = this.deviceDao.getDeviceByDeviceIdAndUserId(deviceId,
				userId);
		if (device == null) {
<<<<<<< HEAD
			throw new RentException(RentException.RentErrorCode.ErrorDeviceNotFound,
=======
			throw new RentException(
					RentException.RentErrorCode.ErrorDeviceNotFound,
>>>>>>> master
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
<<<<<<< HEAD
	
	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Session> getSessions(Device device,int limit ,int offset){
=======

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Session> getSessions(Device device, int limit, int offset) {
>>>>>>> master
		List<Session> sessions = this.sessionDao.getSessions(
				device.getUserId(), device.getId(), limit, offset);
		if (sessions.size() == 0) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"no session found");
		}
		return sessions;
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
<<<<<<< HEAD
	public List<Role> getRoles(String userId){
		return this.roleDao.getRoleByUserId(userId);
	}
	
=======
	public List<Role> getRoles(String userId) {
		return this.roleDao.getRoleByUserId(userId);
	}

>>>>>>> master
	public void setSessionDao(ISessionDao sessionDao) {
		this.sessionDao = sessionDao;
	}

	public void setDeviceDao(IDeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}
<<<<<<< HEAD
	public IRoleDao getRoleDao() {
		return roleDao;
	}
=======

	public IRoleDao getRoleDao() {
		return roleDao;
	}

>>>>>>> master
	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
	}

<<<<<<< HEAD
=======
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
	
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false)
    public void callbackReset(String callback){
    	List<String> users = this.sessionDao.getOnlineUserFilterByCallback(callback);
    	this.sessionDao.resetOnlineStatus(callback);
    	for(String user: users){
    		int count = this.sessionDao.getUserOnlineStatusFromSessions(user);
    		if (count == 0) {
    			UserOnlineStatus userStatus = new UserOnlineStatus();
    			userStatus.setId(user);
    			userStatus.setOnlineStatus(0);
    			userOnlineStatusDao.updateOnlineStatus(userStatus);
    		}
    	}
    }
	
    public IUserOnlineStatusDao getUserOnlineStatusDao() {
        return userOnlineStatusDao;
    }

    public void setUserOnlineStatusDao(IUserOnlineStatusDao userOnlineStatusDao) {
        this.userOnlineStatusDao = userOnlineStatusDao;
    }
>>>>>>> master
}
