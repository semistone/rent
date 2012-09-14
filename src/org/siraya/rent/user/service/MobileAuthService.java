package org.siraya.rent.user.service;
import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.MessageFormat;
import java.util.ResourceBundle;


import junit.framework.Assert;
import org.siraya.rent.user.dao.IUserDAO;
@Service("mobileAuthService")
public class MobileAuthService implements IMobileAuthService {
    @Autowired
    private IMobileGatewayService mobileGatewayService;
    @Autowired
    private IApplicationConfig applicationConfig;
    @Autowired
    private IDeviceDao deviceDao;    
    @Autowired
    private IUserDAO userDao;
    private static Logger logger = LoggerFactory.getLogger(MobileAuthService.class);
	/**
	 * 1. check device status to prevent duplicate auth.
	 * 2. set auth code by random from 0 - 9999
	 * 3. send message.
	 */
	public void sendAuthMessage(Device device) throws Exception{
		//
		// check status and limit
		//
		Assert.assertNotNull(device.getToken());
		
		if (device.getStatus() != DeviceStatus.Init.getStatus()) {
			throw new Exception("device status isn't init");
		}
		//
		// check retry count
		//
		int retryLimit = (Integer)applicationConfig.get("general").get("auth_retry_limit");
		Assert.assertTrue("auth retry too many time try "+device.getAuthRetry(), retryLimit > device.getAuthRetry()  );
		
		User user = device.getUser();
		Assert.assertNotNull("user can't be null",user);
		
		//
		// prepare message.
		//
		String phone=user.getMobilePhone();
	    ResourceBundle resource = ResourceBundle.getBundle("user",user.getLocale());
		String message = MessageFormat.format(resource.getString("mobile_auth_message"),
				device.getId(), device.getToken());
		logger.debug("message is "+message);
		//
		// update status
		//
		device.setModified(0);
		int ret =deviceDao.updateStatusAndRetryCount(device.getId(), DeviceStatus.Authing.getStatus(),
				DeviceStatus.Init.getStatus(), device.getModified());
		Assert.assertEquals("update device status count is not 1",1, ret);

		//
		// send message through gateway.
		//
		mobileGatewayService.sendSMS(phone, message);
		device.setAuthRetry(device.getAuthRetry()+1);
		device.setStatus(DeviceStatus.Authing.getStatus());
	}

	/**
	 * set user dao for testing
	 * @param deviceDao
	 */
	void setDeviceDao(IDeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}

	void setUserDao(IUserDAO userDao){
		this.userDao = userDao;
	}
	/**
	 * 
	 */
	public void verifyAuthCode(String deviceId, String authCode) throws Exception{
		//
		// check status.
		//
		Device device = deviceDao.getDeviceByDeviceId(deviceId);
		device.setModified(0); // reset modified to get current time.
		if (device.getStatus() != DeviceStatus.Authing.getStatus()) {
			throw new Exception("auth status not match");
		}
		//
		// check retry count
		//
		int retryLimit = (Integer)applicationConfig.get("general").get("auth_retry_limit");
		Assert.assertTrue("verify auth retry too many time try:"+device.getAuthRetry()+" > "+retryLimit, device.getAuthRetry() <= retryLimit);

		//
		// check auth code
		//
		if (authCode != device.getToken()) {
			//
			// if fail update retry status
			//
			int ret =deviceDao.updateStatusAndRetryCount(device.getId(), DeviceStatus.Authing.getStatus(),
					DeviceStatus.Init.getStatus(), device.getModified());
			Assert.assertEquals("update device status count is not 1",1, ret);
			throw new Exception("auth code not match");
		}
		
		//
		// update device and user in database.
		//
		deviceDao.updateDeviceStatus(device.getId(),DeviceStatus.Authed.getStatus(),
				DeviceStatus.Authing.getStatus(),
				device.getModified());
		User user = device.getUser();
		if (user.getStatus() == UserStatus.Init.getStatus()){
			user.setModified((long)0);
			userDao.updateUserStatus(user.getId(), UserStatus.Authed.getStatus()
					,  UserStatus.Init.getStatus(), user.getModified());
		}
		
	}

}
