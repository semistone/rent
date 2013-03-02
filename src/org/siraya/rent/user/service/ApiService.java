package org.siraya.rent.user.service;
import org.siraya.rent.pojo.*;
import org.siraya.rent.rest.CookieUtils;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.siraya.rent.user.dao.*;
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
    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
    public void requestSession(Session session, String authData, long timestamp){
    	String deviceId= session.getDeviceId();
    	
    	//
    	// get device from database.
    	//
    	Device device = this.deviceDao.getAppDeviceByDeviceId(deviceId);
    	//
    	// check auth data
    	//
    	if (!ApiService.genAuthData(device.getToken(), timestamp).equals(authData)) {
    		throw new RentException(RentException.RentErrorCode.ErrorPermissionDeny, "check auth data fail");
    	}

    	if (session.getUserId() == null) {
        	//
        	// set session data
        	//
        	session.setUserId(device.getUserId());    		
    	} else if ( !device.getUserId().equals(session.getUserId())) {
        	//
        	// compare old session's user id and device's user id match ?
        	//    
    		throw new RentException(
					RentException.RentErrorCode.ErrorPermissionDeny,
					"user not match");
    	}

    	java.util.List<Session> oldSession=sessionService.getSessions(device, 1, 0);
    	if (oldSession != null && oldSession.size() == 1) {
        	//
        	// add roles from user
        	//
        	sessionService.newApiSession(session);    		
    	} else {
			if (!session.getId().equals(oldSession.get(0).getId())) {
				throw new RentException(
						RentException.RentErrorCode.ErrorGeneral,
						"session id not match");
			}
    	}
    	//
    	// set or extend session timeout
    	//
    	long timeout = java.util.Calendar.getInstance().getTime().getTime();
    	timeout += (Integer)applicationConfig.get("general").get("api_timeout");
    	session.setTimeout(timeout);    	
    }
    
}
