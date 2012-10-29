package org.siraya.rent.user.service;
import org.siraya.rent.user.dao.IRoleDao;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.pojo.Session;
import org.siraya.rent.user.dao.ISessionDao;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.siraya.rent.pojo.Role;
import org.siraya.rent.pojo.Device;
@Service("sessionService")
public class SessionService implements ISessionService {

	@Autowired
    private ISessionDao sessionDao;


	@Autowired
    private IDeviceDao deviceDao;
	@Autowired
    private IRoleDao roleDao;

	private static Logger logger = LoggerFactory.getLogger(SessionService.class);

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public void newSession(Session session) {
		logger.debug("new session");
		this.sessionDao.newSession(session);
		String deviceId = session.getDeviceId();
		String userId = session.getUserId();
		Device device = this.deviceDao.getDeviceByDeviceIdAndUserId(deviceId,
				userId);
		if (device == null) {
			throw new RentException(RentException.RentErrorCode.ErrorDeviceNotFound,
					"device data not found");
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
	public List<Session> getSessions(Device device,int limit ,int offset){
		List<Session> sessions = this.sessionDao.getSessions(
				device.getUserId(), device.getId(), limit, offset);
		if (sessions.size() == 0) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"no session found");
		}
		return sessions;
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Role> getRoles(String userId){
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

}
