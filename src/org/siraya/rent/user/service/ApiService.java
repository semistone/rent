package org.siraya.rent.user.service;
import java.util.*;
import org.siraya.rent.pojo.*;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public class ApiService implements IApiService{
	@Autowired
    private IDeviceDao deviceDao;
	@Autowired
	private ISessionService sessionService;
    @Autowired
    private IApplicationConfig applicationConfig;


	/**
	 * 
	 * @param userId
	 * @param name
	 */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public Device apply(String userId,String name){
    	Device device = this.deviceDao.getUserAppDevice(userId, name);
    	if (device != null) {
    		return device;
    	}
    	device = new Device();
		device.setUserId(userId);
		device.setName(name);
		device.setId(Device.genId());
		device.setStatus(DeviceStatus.ApiKeyOnly.getStatus());
		device.setModified(0);
		device.genToken();
		this.deviceDao.newDevice(device);
		return device;
	}
 
    
    /**    	
     *	 auth data is sha1(secure token + " " + timestamp) 
     *
     * @param token
     * @param timestamp
     * @return
     */
    static public String genAuthData(String token, long timestamp) {
    	return EncodeUtility.sha1(token+" "+timestamp);
    }
    
    private void checkAuthData(Device device,String authData, long timestamp){
    	if (!ApiService.genAuthData(device.getToken(), timestamp).equals(authData)) {
    		throw new RentException(RentException.RentErrorCode.ErrorPermissionDeny, "check auth data fail");
    	}
		long now = Calendar.getInstance().getTime().getTime() / 1000;
		if (timestamp > now + 30) {
			throw new RentException(RentException.RentErrorCode.ErrorGeneral,
					"timestamp is too old");
		}
    }
    
    /**
     * update session.
     * @param session
     * @param authData
     * @param timestamp
     */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
    public void updateSession(Session session, String authData, long timestamp) {
    	String deviceId= session.getDeviceId();
    	
    	//
    	// get device from database.
    	//
    	Device device = this.deviceDao.getAppDeviceByDeviceId(deviceId);
    	//
    	// check auth data
    	//
    	this.checkAuthData(device, authData, timestamp);

    	if (!device.getUserId().equals(session.getUserId())) {
    		//
    		// compare old session's user id and device's user id match ?
    		//    
    		throw new RentException(
    				RentException.RentErrorCode.ErrorPermissionDeny,
    				"user not match");
    	}

       	java.util.List<Session> oldSession=sessionService.getSessions(device, 1, 0);
       	if (oldSession.size() != 1) {
			throw new RentException(RentException.RentErrorCode.ErrorGeneral,
					"session is not exist");

       	}
       	
       	if (!session.getId().equals(oldSession.get(0).getId())) {
       		throw new RentException(
       				RentException.RentErrorCode.ErrorGeneral,
       				"session id not match");				
    	}
		//
		// update into database.
		//
		sessionService.updateApiSession(session);
    	//
    	// set or extend session timeout
    	//
    	long timeout = java.util.Calendar.getInstance().getTime().getTime();
    	timeout += (Integer)applicationConfig.get("general").get("api_timeout");
    	session.setTimeout(timeout);    	    	
    }
    
    /**
     * request new session.
     * 
     */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
    public void requestSession(Session session, String authData, long timestamp, List<Integer> roles){
    	String deviceId= session.getDeviceId();    	
    	//
    	// get device from database.
    	//
    	Device device = this.deviceDao.getAppDeviceByDeviceId(deviceId);
    	//
    	// check auth data
    	//
    	this.checkAuthData(device, authData, timestamp);

    	//
    	// set session data
    	//
    	session.setUserId(device.getUserId());    		


    	java.util.List<Session> oldSession=sessionService.getSessions(device, 1, 0);
		if (oldSession != null && oldSession.size() != 0) {
			throw new RentException(RentException.RentErrorCode.ErrorGeneral,
					"session already exist");
		} 
		//
		// insert into database.
		//
		sessionService.newApiSession(session,roles);
    	//
    	// set or extend session timeout
    	//
    	long timeout = java.util.Calendar.getInstance().getTime().getTime();
    	timeout += (Integer)applicationConfig.get("general").get("api_timeout");
    	session.setTimeout(timeout);    	
    }


	public IDeviceDao getDeviceDao() {
		return deviceDao;
	}


	public void setDeviceDao(IDeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}


	public ISessionService getSessionService() {
		return sessionService;
	}


	public void setSessionService(ISessionService sessionService) {
		this.sessionService = sessionService;
	}


	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}


	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}
    
}
