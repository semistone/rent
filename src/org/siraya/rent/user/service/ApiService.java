package org.siraya.rent.user.service;
import org.siraya.rent.pojo.*;
import org.siraya.rent.rest.CookieUtils;
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
    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
    public Session requestSession(String deviceId, String authData, long timestamp){
    	Session session = new Session();
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
    	//
    	// set session data
    	//
    	session.setDeviceId(deviceId);
    	session.setUserId(device.getUserId());
    	session.setDeviceVerified(true);
    	//
    	// add roles from user
    	//
    	sessionService.newApiSession(session);
    	//
    	// set session timeout
    	//
    	long timeout = java.util.Calendar.getInstance().getTime().getTime();
    	timeout += (Integer)applicationConfig.get("general").get("api_timeout");
    	session.setTimeout(timeout);    	
    	return session;
    }
    
}
