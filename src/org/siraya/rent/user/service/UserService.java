<<<<<<< HEAD
package org.siraya.rent.user.service;

import org.siraya.rent.filter.UserRole;
import org.siraya.rent.pojo.MobileAuthRequest;
import org.siraya.rent.pojo.Role;
import org.siraya.rent.pojo.User;
import org.siraya.rent.pojo.VerifyEvent;
import org.siraya.rent.pojo.MobileAuthResponse;
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.user.dao.IVerifyEventDao;
import org.siraya.rent.user.dao.IMobileAuthRequestDao;
import org.siraya.rent.user.dao.IMobileAuthResponseDao;
import org.siraya.rent.user.dao.IRoleDao;
import org.siraya.rent.utils.EncodeUtility;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import org.siraya.rent.utils.RentException.RentErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Map;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.user.dao.IDeviceDao;

import com.sun.research.ws.wadl.Response;

import java.util.List;
@Service("userService")
public class UserService implements IUserService {

	@Autowired
    private IUserDAO userDao;
    @Autowired
    private IApplicationConfig applicationConfig;

	@Autowired
    private IDeviceDao deviceDao;	
    @Autowired
    private IMobileAuthRequestDao mobileAuthRequestDao;	
    @Autowired
    private IMobileAuthResponseDao mobileAuthResponsetDao;	
    @Autowired
    private IVerifyEventDao verifyEventDao;
	@Autowired
    private EncodeUtility encodeUtility;    

	@Autowired
	private IRoleDao roleDao;
	private static Logger logger = LoggerFactory.getLogger(UserService.class);

    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
    public void removeDevice (Device device) throws Exception{
    	logger.debug("remove device ");
    	int ret =deviceDao.updateRemovedDeviceStatus(device.getId(),device.getUserId(),
    			device.getModified());
    	if (ret != 1) {
    		throw new RentException(RentErrorCode.ErrorStatusViolate, "update count is not 1");
    	}
    }
    
    /**
     * get device info, only can get information with correct device id and user id
     * @param device
     */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
    public Device getDevice(Device device){
    	String userId = device.getUserId();
    	logger.debug("get device from database user id "+userId+" device id "+device.getId());
    	Device tmp = deviceDao.getDeviceByDeviceIdAndUserId(device.getId(), userId);
    	if (tmp == null) {
    		throw new RentException(RentErrorCode.ErrorNotFound, "device id not exist in database");
    	} else {
    		device = tmp;
    	}
    	if (device.getStatus() == DeviceStatus.Removed.getStatus()) {
    		throw new RentException(RentErrorCode.ErrorRemoved, "device status is removed");
    	}
    	logger.debug("device status is "+device.getStatus());
    	if (device.getStatus() == DeviceStatus.Authed.getStatus()) {
    		//
    		// only authed device can get user info
    		//
    		logger.debug("get user from database");
        	User user = userDao.getUserByUserId(userId);
        	if (user == null ) {
        		throw new RentException(RentException.RentErrorCode.ErrorNotFound, "user "+userId+" not found");
        	}
        	logger.debug("mobile phone is "+user.getMobilePhone());
        	device.setUser(user);    		
    	}
    	return device;
    }
    
	/**
	 * new user mobile number
	 * 
	 * @param cc country code
	 * @param moblie phone number
	 * @exception DuplicateKeyException duplicate mobile number
	 */    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public User newUserByMobileNumber(int cc,String mobilePhone) {
		//
		// verify format
		//
    	String ccString = Integer.toString(cc);
		logger.debug("check cc code");
		mobilePhone = mobilePhone.trim();
		Map<String, Object> mobileCountryCode = applicationConfig.get("mobile_country_code");

		if (!mobileCountryCode.containsKey(ccString)) {
    		throw new RentException(RentErrorCode.ErrorCountryNotSupport, "cc not exist in mobile country code " + cc);

		}
		
		if (!mobilePhone.startsWith(ccString)) {
    		throw new RentException(RentErrorCode.ErrorInvalidParameter,"cc code not start with "+cc);			
		}


		//
		// setup user object
		//
		Map<String, Object> map = (Map<String, Object>) mobileCountryCode
				.get(ccString);
		User user = new User();
		String id = java.util.UUID.randomUUID().toString();
		user.setId(id);
		user.setMobilePhone(encodeUtility.encrypt(mobilePhone, User.ENCRYPT_KEY));
		user.setCc((String) map.get("country"));
		user.setLang((String) map.get("lang"));
		user.setTimezone((String) map.get("timezone"));
		user.setStatus(UserStatus.Init.getStatus());
		try{

			//
			// insert into database.
			//
			userDao.newUser(user);
			logger.info("create new user id is " + id);
			return user;
		}catch(org.springframework.dao.DuplicateKeyException e){
			logger.debug("phone number "+mobilePhone+" have been exist in database.");
			return userDao.getUserByMobilePhone(this.encodeUtility.encrypt(mobilePhone,User.ENCRYPT_KEY));
		}
	}


	/**
	 * create new device
	 * 
	 *    1.check max device number.
	 *    2.create new record in database.
	 *    3.send auth code through mobile number.
	 */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public Device newDevice(Device device) throws Exception {
    	User user = device.getUser();
    	Map<String,Object> generalSetting = applicationConfig.get("general");
    	//
		// check device count.
		//
		int count=deviceDao.getDeviceCountByUserId(device.getUserId());
		int maxDevice = ((Integer)generalSetting.get("max_device_count")).intValue();
		logger.debug("user id is "+device.getUserId()+" device count is "+count+" max device is "+maxDevice);
		if (count > maxDevice) {
    		throw new RentException(RentErrorCode.ErrorExceedLimit, "current user have too many device");
		}
		//
		// check how many user in this device
		//
		count = deviceDao.getDeviceCountByDeviceId(device.getId());
		maxDevice = ((Integer)generalSetting.get("max_user_per_device")).intValue();
		logger.debug("device id is "+device.getId()+" user count is "+count+" max user is "+maxDevice);
		if (count > maxDevice) {
    		throw new RentException(RentErrorCode.ErrorExceedLimit, "current device have too many users");
		}
		//
		// check user status
		//
		if (user.getStatus() == UserStatus.Remove.getStatus()) {
    		throw new RentException(RentErrorCode.ErrorRemoved, "user status is removed");
		}
		//
		// generate new device id
		//
		if (device.getId() == null) {
			String id = Device.genId();			
			device.setId(id);
		}
		Device oldDevice = deviceDao.getDeviceByDeviceIdAndUserId(
				device.getId(), user.getId());
		if (oldDevice != null) {
			logger.debug("old device exist");
			device = oldDevice;
			device.setUser(user);
		} else {
			// old device not exist
			device.setStatus(DeviceStatus.Init.getStatus());
			device.setToken(this.encodeUtility.encrypt(device.genToken(), Device.ENCRYPT_KEY));
			deviceDao.newDevice(device);
			logger.debug("insert device");

		}
		return device;
			
	}


    /**
     * set up email. only when email still not exist.
     * 
     */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class) 
	public void setupEmail(User user) throws Exception {
    	String userId = user.getId();
    	String newEmail = user.getEmail();
    	logger.info("update email for user "+userId+ " new email "+newEmail);
    	VerifyEvent verifyEvent = null;
    	user = userDao.getUserByUserId(user.getId());
    	String oldEmail = user.getEmail();
    	
    	if (oldEmail != null && !oldEmail.equals("")) {
    		logger.debug("old email exist");
    		
    		if (oldEmail.equals(newEmail)) {
    			//
    			// same email.
    			//
        		throw new RentException(RentErrorCode.ErrorUpdateSameItem, "same email have been setted");
    		}
    		
    		//
    		// if old email exist and already verified, then throw exception
    		//  to prevent overwrite primary email 
    		//  change email must call by different process.
    		//
    		verifyEvent = verifyEventDao.getEventByVerifyDetailAndType(VerifyEvent.VerifyType.Email.getType(),
    				oldEmail);
			
    		if (verifyEvent != null
					&& verifyEvent.getStatus() == VerifyEvent.VerifyStatus.Authed
							.getStatus()) {
        		throw new RentException(RentErrorCode.ErrorStatusViolate, "old email have been verified. can't reset email");
			}
    	}
    	
    	user.setModified(new Long(0));
    	user.setEmail(newEmail);

    	logger.debug("insert verify event");
    	verifyEvent = new VerifyEvent();
    	verifyEvent.setUserId(userId);
    	verifyEvent.setStatus(VerifyEvent.VerifyStatus.Init.getStatus());
    	verifyEvent.setVerifyDetail(newEmail);
    	verifyEvent.setVerifyType(VerifyEvent.VerifyType.Email.getType());    	
    	verifyEventDao.newVerifyEvent(verifyEvent);

    	
    	logger.debug("update email in database");
    	userDao.updateUserEmail(user);
    	
    }

    /**
     * update login id and password
     * 
     */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class) 
	public void updateLoginIdAndPassowrd(User user){
    	String userId = user.getId();
    	User user2 = userDao.getUserByUserId(userId);
    	//
    	// check original login id must be null or empty
    	//
		if (user2.getLoginId() != null && user2.getLoginId() != "") {
			throw new RentException(
					RentException.RentErrorCode.ErrorStatusViolate,
					"can't reset login id");
		}
    	user.setId(user2.getId());
    	user.setModified(new Long(0));
    	//
    	// sha1
    	//
    	user.setPassword(EncodeUtility.sha1(user.getPassword()));
    	logger.debug("update login id and password in database");
    	int ret =userDao.updateUserLoginIdAndPassword(user);	
    	if (ret == 0 ) {
    		throw new RentException(RentErrorCode.ErrorCanNotOverwrite, "update cnt =0, only empty login id can be update");
    	}
	}
    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class) 
    public void initLoginIdAndType(User user){
    	user.setModified(new Long(0));
    	int ret =this.userDao.initLoginIdAndType(user);
		if (ret != 1) {
			throw new RentException(
					RentException.RentErrorCode.ErrorStatusViolate,
					"update login id and type fail, maybe already initialized");
		}
    }
    public void nameDevice(Device device) {
    	int ret = this.deviceDao.nameDevice(device);
    	if (ret == 0 ) {
    		throw new RentException(RentErrorCode.ErrorCanNotOverwrite, "update cnt =0, only empty login id can be update");
    	}
    }
    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class) 
    public void applySSOApplication(Device device){
    	device.setId(SSO_DEVICE_ID);
		device.setStatus(DeviceStatus.ApiKeyOnly.getStatus());
		device.setModified(0);
		device.genToken();
    	try{
    		this.deviceDao.newDevice(device);
    		//
    		// add new role sso app
    		//
    		Role role = new Role();
    		role.setUserId(device.getUserId());
    		role.setRole(UserRole.UserRoleId.SSO_APP);
    		this.roleDao.newRole(role);
		}catch(org.springframework.dao.DuplicateKeyException e) {
			throw new RentException(RentException.RentErrorCode.ErrorDuplicate,"apply sso application but duplicate error");
		}
    }
        	
    /**
     * get all user devies
     * @param userId
     * @return
     */
    public List<Device> getUserDevices(String userId, int limit, int offset){
    	List<Device> ret =  this.deviceDao.getUserDevices(userId, limit ,offset);
    	if (ret.size() == 0){
    		throw new RentException(RentErrorCode.ErrorNotFound,"no device found");
    	}
    	return ret;
    }

    public List<User> getDeviceUsers(String deviceId, int limit, int offset){
    	List<User> ret =  this.deviceDao.getDeviceUsers(deviceId, limit ,offset);
    	if (ret.size() == 0){
    		throw new RentException(RentErrorCode.ErrorNotFound,"no device found");
    	}
    	return ret;
    }

    public String getSignatureOfMobileAuthRequest(MobileAuthRequest request){
		String userId = request.getRequestFrom();
		Device requestFrom = this.deviceDao.getDeviceByDeviceIdAndUserId(
				SSO_DEVICE_ID, userId);
		return this._getSignatureOfMobileAuthRequest(request, requestFrom);
    }
    
	private String _getSignatureOfMobileAuthRequest(MobileAuthRequest request,Device requestFrom) {
		if (requestFrom == null) {
			String userId = requestFrom.getUserId();
			throw new RentException(RentErrorCode.ErrorUserExist,"request sso device user id:"+userId+" not exist");
		}
		if (requestFrom.getStatus() != DeviceStatus.ApiKeyOnly.getStatus()) {
			throw new RentException(RentErrorCode.ErrorPermissionDeny,"request user not for apikey only");			
		}		
		String requestString = request.toString(requestFrom.getToken());
		logger.debug("verify request sign "+requestString);	
		return EncodeUtility.sha1(requestString);
	}
	
    /**
     * step1: check requestFrom 
     * step2: check sign
     * step3: if auth user exist, fetch user id.
     *        if user not exist, create new user
     * step4: check expire time.
     * step4: authUser and mobilePhone can't have together.
     * step5: save request into database.
     */
	public MobileAuthResponse mobileAuthRequest( MobileAuthRequest request){
		Device currentDevice = request.getDevice();
		String userId = request.getRequestFrom();
	  	Map<String,Object> generalSetting = applicationConfig.get("general");
		Device requestFrom = this.deviceDao.getDeviceByDeviceIdAndUserId(
				SSO_DEVICE_ID, userId);
		boolean debug = (Boolean)generalSetting.get("debug");
		String sign = this._getSignatureOfMobileAuthRequest(request, requestFrom);
		if (!debug && !sign.equals(request.getSign())) {
			throw new RentException(RentErrorCode.ErrorPermissionDeny,
					"sign verify failed "+sign);
		}

		logger.debug("check verify expired");
		long expire = 300;
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		if (request.getRequestTime() > now) {
			throw new RentException(RentErrorCode.ErrorAuthExpired,
					"request time is too after now " + request.getRequestTime()
							+ " compare to " + now);
		}
		if (request.getRequestTime() < now - expire) {
			throw new RentException(RentErrorCode.ErrorAuthExpired,
					"request time is expired time is "
							+ request.getRequestTime() + " compare to " + now);
		}
		
		//
		// get autu user from friend
		//
		
		//
		// get auth user from mobile phone
		//
		String mobilePhone = request.getMobilePhone();
		User user = null;
		if (mobilePhone != null) {
			user = this.userDao.getUserByMobilePhone(this.encodeUtility.encrypt(mobilePhone,User.ENCRYPT_KEY));
			if (user != null) {
				logger.debug("current user exist");
				request.setUser(user);
			} else {
				logger.debug("this mobile phone's user not exist yet");
				int cc = Integer.parseInt(request.getCountryCode());
				user = this.newUserByMobileNumber(cc, mobilePhone);
			}
			request.setMobilePhone(this.encodeUtility.encrypt(mobilePhone,User.ENCRYPT_KEY)); // encrypt mobile phone
			currentDevice.setUserId(user.getId());
			//
			// get device from deviceDao
			//
			try{
				currentDevice = this.getDevice(currentDevice);
			}catch(RentException e){
				//
				// skip device not found error and set as staus to removed.
				//
				if (e.getErrorCode() != RentException.RentErrorCode.ErrorNotFound) {
					throw e;
				}
				currentDevice.setStatus(DeviceStatus.Authing.getStatus());
			}

		} else {
			logger.debug("requset mobile phone is empty");
		}
		
		//
		// save into database.
		//
		MobileAuthResponse response = null;
		boolean isDuplicate = false;
		try {
			logger.debug("save request into database");
			request.genToken();
			request.setStatus(DeviceStatus.Init.getStatus());
			request.setToken(encodeUtility.encrypt(request.getToken(), Device.ENCRYPT_KEY));
			mobileAuthRequestDao.newRequest(request);
			response = new MobileAuthResponse();
			response.setRequestId(request.getRequestId());
			response.setResponseTime(java.util.Calendar.getInstance().getTimeInMillis()/1000);

		}catch(org.springframework.dao.DuplicateKeyException e) {
			isDuplicate = true;
			logger.debug("duplication request "+request.getRequestId());
			//
			// get original response obj and set original token from original request.
			//
			response = this.mobileAuthRequestDao.get(request.getRequestId());
			request.setToken(((MobileAuthRequest)response).getToken());
		}catch(Exception e){
			logger.error("insert request into dao error",e);
			throw new RentException(RentException.RentErrorCode.ErrorGeneral,"insert request into dao error");
		}
		//
		// prepare response
		//
		logger.debug("prepare response");
		// only force reauth = false need to return current status
		response.setDevice(currentDevice);
		response.setUser(user);
		String responseSign = EncodeUtility.sha1(response.toString(requestFrom.getToken()));
		if (request.isForceReauth()) {
			response.setStatus(request.getStatus());
		} else {  // not force reauth
			if (isDuplicate) { // and duplicate
				int originStatus = response.getStatus();
				response.setStatus(currentDevice.getStatus());
				if ((originStatus == DeviceStatus.Authing.getStatus() || originStatus == DeviceStatus.Init.getStatus()) &&
					currentDevice.getStatus() == DeviceStatus.Authed.getStatus()) {
					this.mobileAuthResponsetDao.updateResponse(response);	
				}
			} else {
				response.setStatus(currentDevice.getStatus());				
			}
		}
		response.setSign(responseSign);	
		return response;
	}
	
	public List<Device> getSsoDevices(){
		return this.deviceDao.getSsoDevices();
	}
	
	public MobileAuthRequest getMobileAuthRequest(String requestId) {
		MobileAuthRequest request = this.mobileAuthRequestDao.get(requestId);
		if (request == null) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"request id "+requestId+" not found");
		}
		//
		// sign response.
		//
		if (request.getStatus() == DeviceStatus.Authed.getStatus()) {
			String sign = getSignatureOfMobileAuthRequest(request);
			request.setSign(sign);
		}
		return request;
	}
	
	public IMobileAuthRequestDao getMobileAuthRequestDao() {
		return mobileAuthRequestDao;
	}

	public void setMobileAuthRequestDao(IMobileAuthRequestDao mobileAuthRequestDao) {
		this.mobileAuthRequestDao = mobileAuthRequestDao;
	}

	@Override
	public void verifyEmail(String userId, String authCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUserInfo(User user) {
		// TODO Auto-generated method stub

	}
	
	public void setUserDao(IUserDAO userDao){
		this.userDao = userDao;
	}
	
	public void setDeviceDao(IDeviceDao deviceDao){
		this.deviceDao = deviceDao;
	}
	
    public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}
    public IVerifyEventDao getVerifyEventDao() {
		return verifyEventDao;
	}

	public void setVerifyEventDao(IVerifyEventDao verifyEventDao) {
		this.verifyEventDao = verifyEventDao;
	}
    public EncodeUtility getEncodeUtility() {
		return encodeUtility;
	}

	public void setEncodeUtility(EncodeUtility encodeUtility) {
		this.encodeUtility = encodeUtility;
	}

}
=======
package org.siraya.rent.user.service;
import org.siraya.rent.user.dao.IMemberDao;
import org.siraya.rent.filter.UserRole;
import org.siraya.rent.pojo.MobileAuthRequest;
import org.siraya.rent.pojo.Role;
import org.siraya.rent.pojo.User;
import org.siraya.rent.pojo.VerifyEvent;
import org.siraya.rent.pojo.Member;
import org.siraya.rent.pojo.MobileAuthResponse;
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.user.dao.IUserOnlineStatusDao;
import org.siraya.rent.user.dao.IVerifyEventDao;
import org.siraya.rent.user.dao.IMobileAuthRequestDao;
import org.siraya.rent.user.dao.IMobileAuthResponseDao;
import org.siraya.rent.user.dao.IRoleDao;
import org.siraya.rent.utils.EncodeUtility;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import org.siraya.rent.utils.RentException.RentErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Map;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.user.dao.IDeviceDao;



import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
@Service("userService")
public class UserService implements IUserService {

	@Autowired
    private IUserDAO userDao;
    @Autowired
    private IApplicationConfig applicationConfig;
	@Autowired
    private IMemberDao memberDao;	
	@Autowired
    private IDeviceDao deviceDao;	
    @Autowired
    private IMobileAuthRequestDao mobileAuthRequestDao;	
    @Autowired
    private IMobileAuthResponseDao mobileAuthResponsetDao;	
    @Autowired
    private IVerifyEventDao verifyEventDao;
	@Autowired
    private EncodeUtility encodeUtility;    

	@Autowired
	private IRoleDao roleDao;

	@Autowired
	private IUserOnlineStatusDao userOnlineStatusDao;

	private static Logger logger = LoggerFactory.getLogger(UserService.class);

    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
    public void removeDevice (Device device) throws Exception{
    	logger.debug("remove device ");
    	int ret =deviceDao.updateRemovedDeviceStatus(device.getId(),device.getUserId(),
    			device.getModified());
    	if (ret != 1) {
    		throw new RentException(RentErrorCode.ErrorStatusViolate, "update count is not 1");
    	}
    }
    
    /**
     * get device info, only can get information with correct device id and user id
     * @param device
     */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
    public Device getDevice(Device device){
    	String userId = device.getUserId();
    	logger.debug("get device from database user id "+userId+" device id "+device.getId());
    	Device tmp = deviceDao.getDeviceByDeviceIdAndUserId(device.getId(), userId);
    	if (tmp == null) {
    		throw new RentException(RentErrorCode.ErrorNotFound, "device id not exist in database");
    	} else {
    		device = tmp;
    	}
    	if (device.getStatus() == DeviceStatus.Removed.getStatus()) {
    		throw new RentException(RentErrorCode.ErrorRemoved, "device status is removed");
    	}
    	logger.debug("device status is "+device.getStatus());
    	if (device.getStatus() == DeviceStatus.Authed.getStatus()) {
    		//
    		// only authed device can get user info
    		//
    		logger.debug("get user from database");
        	User user = userDao.getUserByUserId(userId);
        	if (user == null ) {
        		throw new RentException(RentException.RentErrorCode.ErrorNotFound, "user "+userId+" not found");
        	}
        	logger.debug("mobile phone is "+user.getMobilePhone());
        	device.setUser(user);    		
    	}
    	return device;
    }
    
	/**
	 * new user mobile number
	 * 
	 * @param cc country code
	 * @param moblie phone number
	 * @exception DuplicateKeyException duplicate mobile number
	 */    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public User newUserByMobileNumber(int cc,String mobilePhone) {
		//
		// verify format
		//
    	String ccString = Integer.toString(cc);
		logger.debug("check cc code");
		mobilePhone = mobilePhone.trim();
		Map<String, Object> mobileCountryCode = applicationConfig.get("mobile_country_code");

		if (!mobileCountryCode.containsKey(ccString)) {
    		throw new RentException(RentErrorCode.ErrorCountryNotSupport, "cc not exist in mobile country code " + cc);

		}
		
		if (!mobilePhone.startsWith("+"+ccString)) {
    		throw new RentException(RentErrorCode.ErrorInvalidParameter,"cc code not start with "+cc);			
		}


		//
		// setup user object
		//
		Map<String, Object> map = (Map<String, Object>) mobileCountryCode
				.get(ccString);
		User user = new User();
		String id = java.util.UUID.randomUUID().toString();
		user.setId(id);
		user.setMobilePhone(encodeUtility.encrypt(mobilePhone, User.ENCRYPT_KEY));
		user.setCc((String) map.get("country"));
		user.setLang((String) map.get("lang"));
		user.setTimezone((String) map.get("timezone"));
		user.setStatus(UserStatus.Init.getStatus());
		try{

			//
			// insert into database.
			//
			userDao.newUser(user);
			logger.info("create new user id is " + id);
			return user;
		}catch(org.springframework.dao.DuplicateKeyException e){
			logger.debug("phone number "+mobilePhone+" have been exist in database.");
			return userDao.getUserByMobilePhone(this.encodeUtility.encrypt(mobilePhone,User.ENCRYPT_KEY));
		}
	}


	/**
	 * create new device
	 * 
	 *    1.check max device number.
	 *    2.create new record in database.
	 *    3.send auth code through mobile number.
	 */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public Device newDevice(Device device) throws Exception {
    	User user = device.getUser();
    	Map<String,Object> generalSetting = applicationConfig.get("general");
    	//
		// check device count.
		//
		int count=deviceDao.getDeviceCountByUserId(device.getUserId());
		int maxDevice = ((Integer)generalSetting.get("max_device_count")).intValue();
		logger.debug("user id is "+device.getUserId()+" device count is "+count+" max device is "+maxDevice);
		if (count > maxDevice) {
    		throw new RentException(RentErrorCode.ErrorExceedLimit, "current user have too many device");
		}
		//
		// check how many user in this device
		//
		count = deviceDao.getDeviceCountByDeviceId(device.getId());
		maxDevice = ((Integer)generalSetting.get("max_user_per_device")).intValue();
		logger.debug("device id is "+device.getId()+" user count is "+count+" max user is "+maxDevice);
		if (count > maxDevice) {
    		throw new RentException(RentErrorCode.ErrorExceedLimit, "current device have too many users");
		}
		//
		// check user status
		//
		if (user.getStatus() == UserStatus.Remove.getStatus()) {
    		throw new RentException(RentErrorCode.ErrorRemoved, "user status is removed");
		}
		//
		// generate new device id
		//
		if (device.getId() == null) {
			String id = Device.genId();			
			device.setId(id);
		}
		Device oldDevice = deviceDao.getDeviceByDeviceIdAndUserId(
				device.getId(), user.getId());
		if (oldDevice != null) {
			logger.debug("old device exist");
			device = oldDevice;
			device.setUser(user);
		} else {
			// old device not exist
			device.setStatus(DeviceStatus.Init.getStatus());
			device.setToken(this.encodeUtility.encrypt(device.genToken(), Device.ENCRYPT_KEY));
			deviceDao.newDevice(device);
			logger.debug("insert device");

		}
		return device;
			
	}


    /**
     * set up email. only when email still not exist.
     * 
     */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class) 
	public void setupEmail(User user) throws Exception {
    	String userId = user.getId();
    	String newEmail = user.getEmail();
    	logger.info("update email for user "+userId+ " new email "+newEmail);
    	VerifyEvent verifyEvent = null;
    	user = userDao.getUserByUserId(user.getId());
    	String oldEmail = user.getEmail();
    	
    	if (oldEmail != null && !oldEmail.equals("")) {
    		logger.debug("old email exist");
    		
    		if (oldEmail.equals(newEmail)) {
    			//
    			// same email.
    			//
        		throw new RentException(RentErrorCode.ErrorUpdateSameItem, "same email have been setted");
    		}
    		
    		//
    		// if old email exist and already verified, then throw exception
    		//  to prevent overwrite primary email 
    		//  change email must call by different process.
    		//
    		verifyEvent = verifyEventDao.getEventByVerifyDetailAndType(VerifyEvent.VerifyType.Email.getType(),
    				oldEmail);
			
    		if (verifyEvent != null
					&& verifyEvent.getStatus() == VerifyEvent.VerifyStatus.Authed
							.getStatus()) {
        		throw new RentException(RentErrorCode.ErrorStatusViolate, "old email have been verified. can't reset email");
			}
    	}
    	
    	user.setModified(new Long(0));
    	user.setEmail(newEmail);

    	logger.debug("insert verify event");
    	verifyEvent = new VerifyEvent();
    	verifyEvent.setUserId(userId);
    	verifyEvent.setStatus(VerifyEvent.VerifyStatus.Init.getStatus());
    	verifyEvent.setVerifyDetail(newEmail);
    	verifyEvent.setVerifyType(VerifyEvent.VerifyType.Email.getType());    	
    	verifyEventDao.newVerifyEvent(verifyEvent);

    	
    	logger.debug("update email in database");
    	userDao.updateUserEmail(user);
    	
    }

    /**
     * update login id and password
     * 
     */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class) 
	public void updateLoginIdAndPassowrd(User user){
    	String userId = user.getId();
    	User user2 = userDao.getUserByUserId(userId);
    	//
    	// check original login id must be null or empty
    	//
		if (user2.getLoginId() != null && user2.getLoginId() != "") {
			throw new RentException(
					RentException.RentErrorCode.ErrorStatusViolate,
					"can't reset login id");
		}
    	user.setId(user2.getId());
    	user.setModified(new Long(0));
    	//
    	// sha1
    	//
    	user.setPassword(EncodeUtility.sha1(user.getPassword()));
    	logger.debug("update login id and password in database");
    	int ret =userDao.updateUserLoginIdAndPassword(user);	
    	if (ret == 0 ) {
    		throw new RentException(RentErrorCode.ErrorCanNotOverwrite, "update cnt =0, only empty login id can be update");
    	}
	}
    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class) 
    public void initLoginIdAndType(User user){
    	user.setModified(new Long(0));
    	int ret;
    	try{
    		ret =this.userDao.initLoginIdAndType(user);
    	}catch(Exception e){
			//
			// assume exception happen when duplicate key happen.
			//
			throw new RentException(RentException.RentErrorCode.ErrorDuplicate,
					"fb account has been used, " + e.getMessage());
    	}
		if (ret != 1) {
			throw new RentException(
					RentException.RentErrorCode.ErrorStatusViolate,
					"update login id and type fail, maybe already initialized ret is "+ret);
		}
    }
    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class) 
    public void nameDevice(Device device) {
    	int ret = this.deviceDao.nameDevice(device);
    	if (ret == 0 ) {
    		throw new RentException(RentErrorCode.ErrorCanNotOverwrite, "update cnt =0, only empty login id can be update");
    	}
    }
    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class) 
    public void applySSOApplication(Device device){
    	device.setId(SSO_DEVICE_ID);
		device.setStatus(DeviceStatus.ApiKeyOnly.getStatus());
		device.setModified(0);
		device.genToken();
    	try{
    		this.deviceDao.newDevice(device);
    		//
    		// add new role sso app
    		//
    		Role role = new Role();
    		role.setUserId(device.getUserId());
    		role.setRole(UserRole.UserRoleId.SSO_APP);
    		this.roleDao.newRole(role);
		}catch(org.springframework.dao.DuplicateKeyException e) {
			throw new RentException(RentException.RentErrorCode.ErrorDuplicate,"apply sso application but duplicate error");
		}
    }
        	
    /**
     * get all user devies
     * @param userId
     * @return
     */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true) 
    public List<Device> getUserDevices(String userId, int limit, int offset){
    	List<Device> ret =  this.deviceDao.getUserDevices(userId, limit ,offset);
    	if (ret.size() == 0){
    		throw new RentException(RentErrorCode.ErrorNotFound,"no device found");
    	}
    	return ret;
    }
    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true) 
    public List<User> getDeviceUsers(String deviceId, int limit, int offset){
    	List<User> ret =  this.deviceDao.getDeviceUsers(deviceId, limit ,offset);
    	if (ret.size() == 0){
    		throw new RentException(RentErrorCode.ErrorNotFound,"no device found");
    	}
    	return ret;
    }
    
    
 
    public String getSignatureOfMobileAuthRequest(MobileAuthRequest request){
		String userId = request.getRequestFrom();
		Device requestFrom = this.deviceDao.getDeviceByDeviceIdAndUserId(
				SSO_DEVICE_ID, userId);
		return this._getSignatureOfMobileAuthRequest(request, requestFrom);
    }
    
	private String _getSignatureOfMobileAuthRequest(MobileAuthRequest request,Device requestFrom) {
		if (requestFrom == null) {
			String userId = requestFrom.getUserId();
			throw new RentException(RentErrorCode.ErrorUserExist,"request sso device user id:"+userId+" not exist");
		}
		if (requestFrom.getStatus() != DeviceStatus.ApiKeyOnly.getStatus()) {
			throw new RentException(RentErrorCode.ErrorPermissionDeny,"request user not for apikey only");			
		}		
		String requestString = request.toString(requestFrom.getToken());
		logger.debug("verify request sign "+requestString);	
		return EncodeUtility.sha1(requestString);
	}
	
    /**
     * step1: check requestFrom 
     * step2: check sign
     * step3: if auth user exist, fetch user id.
     *        if user not exist, create new user
     * step4: check expire time.
     * step4: authUser and mobilePhone can't have together.
     * step5: save request into database.
     */
	public MobileAuthResponse mobileAuthRequest( MobileAuthRequest request){
		Device currentDevice = request.getDevice();
		String userId = request.getRequestFrom();
		MobileAuthResponse response = null;

	  	Map<String,Object> generalSetting = applicationConfig.get("general");
		Device requestFrom = this.deviceDao.getDeviceByDeviceIdAndUserId(
				SSO_DEVICE_ID, userId);
		boolean debug = (Boolean)generalSetting.get("debug");
		String sign = this._getSignatureOfMobileAuthRequest(request, requestFrom);
		if (!debug && !sign.equals(request.getSign())) {
			throw new RentException(RentErrorCode.ErrorPermissionDeny,
					"sign verify failed ");
		}

		logger.debug("check verify expired");
		long expire = 300;
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		if (request.getRequestTime() > now) {
			throw new RentException(RentErrorCode.ErrorAuthExpired,
					"request time is too after now " + request.getRequestTime()
							+ " compare to " + now);
		}
		if (request.getRequestTime() < now - expire) {
			throw new RentException(RentErrorCode.ErrorAuthExpired,
					"request time is expired time is "
							+ request.getRequestTime() + " compare to " + now);
		}
		
		User user = null;

		//
		// get autu user from friend
		//
		if (request.getAuthUserId() != null && !"".equals(request.getAuthUserId())) {
			Member member = this.memberDao.getByMemberUserId(request.getRequestFrom(),
					request.getAuthUserId());
			if (member != null) {
				if (member.getMemberUserId() != null) {
					user = userDao.getUserByUserId(member.getMemberUserId());
					request.setMobilePhone(user.getMobilePhone());
					request.setUser(user);
					if (request.getMobilePhone() != null) { // check if user match.
						initUserByMobilePhone(request);
					}
				} else {
					throw new RentException(
							RentException.RentErrorCode.ErrorGeneral,
							"member user id can't be null");
				}
			} else {
				logger.debug("member is null");
				member = this.createMemberFromRequest(request);
			}
		} else if (request.getMobilePhone() != null) {
			//
			// get auth user from mobile phone
			//
			this.initUserByMobilePhone(request);
		} else {
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"both auth user id and mobile phone is null");
		}
				
		user = request.getUser();
		currentDevice.setUserId(user.getId());
		//
		// get device from deviceDao
		//
		try {
			currentDevice = this.getDevice(currentDevice);
		} catch (RentException e) {
			//
			// skip device not found error and set as staus to removed.
			//
			if (e.getErrorCode() != RentException.RentErrorCode.ErrorNotFound) {
				throw e;
			}
			currentDevice.setStatus(DeviceStatus.Authing.getStatus());
		}
		
		//
		// save into database.
		//
		boolean isDuplicate = false;
		try {
			logger.debug("save request into database");
			request.genToken();
			request.setStatus(DeviceStatus.Init.getStatus());
			request.setToken(encodeUtility.encrypt(request.getToken(), Device.ENCRYPT_KEY));
			mobileAuthRequestDao.newRequest(request);
			response = new MobileAuthResponse();
			response.setRequestId(request.getRequestId());
			response.setResponseTime(java.util.Calendar.getInstance().getTimeInMillis()/1000);

		}catch(org.springframework.dao.DuplicateKeyException e) {
			isDuplicate = true;
			logger.debug("duplication request "+request.getRequestId());
			//
			// get original response obj and set original token from original request.
			//
			response = this.mobileAuthRequestDao.get(request.getRequestId());
			request.setToken(((MobileAuthRequest)response).getToken());
		}catch(Exception e){
			logger.error("insert request into dao error",e);
			throw new RentException(RentException.RentErrorCode.ErrorGeneral,"insert request into dao error");
		}
		//
		// prepare response
		//
		logger.debug("prepare response");
		// only force reauth = false need to return current status
		response.setDevice(currentDevice);
		response.setUser(user);
		String responseSign = EncodeUtility.sha1(response.toString(requestFrom.getToken()));
		if (request.isForceReauth()) {
			response.setStatus(request.getStatus());
		} else {  // not force reauth
			if (isDuplicate) { // and duplicate
				int originStatus = response.getStatus();
				response.setStatus(currentDevice.getStatus());
				if ((originStatus == DeviceStatus.Authing.getStatus() || originStatus == DeviceStatus.Init.getStatus()) &&
					currentDevice.getStatus() == DeviceStatus.Authed.getStatus()) {
					this.mobileAuthResponsetDao.updateResponse(response);	
				}
			} else {
				response.setStatus(currentDevice.getStatus());				
			}
		}
		response.setSign(responseSign);	
		return response;
	}
	
	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = true)
	public List<User> getOnineUser(int limit, int offset){
		List<String> idlist = this.userOnlineStatusDao.online(limit, offset);
		java.lang.StringBuffer sb = new java.lang.StringBuffer();
		for(String id : idlist){
			sb.append(id);
			sb.append(",");
		}
		return this.userDao.list(sb.substring(0, sb.length()-1));
	}
	
	private Member createMemberFromRequest(MobileAuthRequest request){
		Member member = new Member();
		if (request.getMobilePhone() != null) {
			this.initUserByMobilePhone(request);					
		} else {
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"mobile phone can't null");
		}
		member.setMemberId(request.getAuthUserId());
		member.setUserId(request.getRequestFrom());
		member.genId();
		member.setMemberUserId(request.getUserId());
		memberDao.newMember(member);
		return member;
	}
	/**
	 * 
	 * @param mobilePhone
	 * @param request
	 */
	private void initUserByMobilePhone(MobileAuthRequest request){
		String mobilePhone = request.getMobilePhone();
		User userFromAuthUserId = request.getUser();
		User user = this.userDao.getUserByMobilePhone(this.encodeUtility.encrypt(mobilePhone,User.ENCRYPT_KEY));
		//
		// match auth user and mobile phone if all exist.
		//
		if (userFromAuthUserId != null && user != null
				&& user.getId() != userFromAuthUserId.getId()) {
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"auth user id and mobile phone not match ");
		}
		logger.debug("this mobile phone's user not exist yet");
		if (user == null){
			int cc = Integer.parseInt(request.getCountryCode());
			user = this.newUserByMobileNumber(cc, mobilePhone);				
		}
		request.setMobilePhone(this.encodeUtility.encrypt(mobilePhone,
				User.ENCRYPT_KEY)); // encrypt mobile phone
		request.setUser(user);
	}

	public List<Device> getSsoDevices(){
		return this.deviceDao.getSsoDevices();
	}
	

	public MobileAuthRequest getMobileAuthRequest(String requestId) {
		MobileAuthRequest request = this.mobileAuthRequestDao.get(requestId);
		if (request == null) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"request id "+requestId+" not found");
		}
		//
		// sign response.
		//
		if (request.getStatus() == DeviceStatus.Authed.getStatus()) {
			String sign = getSignatureOfMobileAuthRequest(request);
			request.setSign(sign);
		}
		return request;
	}
	
	public void updateProfile(User user){
		this.userDao.updateProfile(user);
	}
	
	public IMobileAuthRequestDao getMobileAuthRequestDao() {
		return mobileAuthRequestDao;
	}

	public void setMobileAuthRequestDao(IMobileAuthRequestDao mobileAuthRequestDao) {
		this.mobileAuthRequestDao = mobileAuthRequestDao;
	}

	@Override
	public void verifyEmail(String userId, String authCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUserInfo(User user) {
		// TODO Auto-generated method stub

	}
	
	public void setUserDao(IUserDAO userDao){
		this.userDao = userDao;
	}
	
	public void setDeviceDao(IDeviceDao deviceDao){
		this.deviceDao = deviceDao;
	}
	
    public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}
    public IVerifyEventDao getVerifyEventDao() {
		return verifyEventDao;
	}

	public void setVerifyEventDao(IVerifyEventDao verifyEventDao) {
		this.verifyEventDao = verifyEventDao;
	}
    public EncodeUtility getEncodeUtility() {
		return encodeUtility;
	}

	public void setEncodeUtility(EncodeUtility encodeUtility) {
		this.encodeUtility = encodeUtility;
	}
	public IMemberDao getMemberDao() {
		return memberDao;
	}

	public void setMemberDao(IMemberDao memberDao) {
		this.memberDao = memberDao;
	}
	public IRoleDao getRoleDao() {
		return roleDao;
	}

	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
	}
}
>>>>>>> master
