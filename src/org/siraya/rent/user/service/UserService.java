package org.siraya.rent.user.service;

import junit.framework.Assert;

import org.siraya.rent.pojo.User;
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.utils.IApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.GET;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.user.dao.IDeviceDao;

@Service("userService")
public class UserService implements IUserService {
    @Autowired
    private IUserDAO userDao;
    @Autowired
    private IApplicationConfig applicationConfig;
    @Autowired
    private IDeviceDao deviceDao;
	@Autowired
	private IMobileAuthService mobileAuthService;	
	
    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    
	/**
	 * new user mobile number
	 * 
	 * @param cc country code
	 * @param moblie phone number
	 * @exception DuplicateKeyException duplicate mobile number
	 */    
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public User newUserByMobileNumber(int cc,String mobilePhone) throws Exception {
		//
		// verify format
		//
		logger.debug("check cc code");
		mobilePhone = mobilePhone.trim();
		Map<String, Object> mobileCountryCode = applicationConfig.get("mobile_country_code");
		Assert.assertTrue("cc not exist in mobile country code " + cc,
				mobileCountryCode.containsKey(cc));
		Assert.assertTrue("cc code not start with +"+cc,
				mobilePhone.startsWith(Integer.toString(cc)));

		//
		// setup user object
		//
		Map<String, Object> map = (Map<String, Object>) mobileCountryCode
				.get(cc);
		User user = new User();
		String id = java.util.UUID.randomUUID().toString();
		user.setId(id);


		user.setMobilePhone(mobilePhone);
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
			return userDao.getUserByMobilePhone(mobilePhone);
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
	public void newDevice(Device device) throws Exception {
		//
		// check device count.
		//
		int count=deviceDao.getDeviceCountByUserId(device.getUserId());
		int maxDevice = ((Integer)applicationConfig.get("general").get("max_device_count")).intValue();
		logger.debug("user id is "+device.getUserId()+" device count is "+count+" max device is "+maxDevice);
		if (count > maxDevice) {
			throw new Exception("current user have too many device");
		}
		//
		// check user status
		//
		if (device.getUser().getStatus() == UserStatus.Remove.getStatus()) {
			throw new Exception("user status is removed");
		}
		
		String id = java.util.UUID.randomUUID().toString();
		device.setId(id);
		device.setStatus(DeviceStatus.Init.getStatus());
		Random r = new Random();
		device.setToken(String.valueOf(r.nextInt(9999)));
		deviceDao.newDevice(device);
		logger.debug("insert device");
		
	}


    /**
     * set up email. only when email still not exist.
     * 
     */
    @Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class) 
	public void setupEmail(User user) {
    	String userId = user.getId();
    	String newEmail = user.getEmail();
    	user = userDao.getUserByUserId(user.getId());
    }
	
	public void updateLoginIdAndPassowrd(User user){
		
	}

	@Override
	public void verifyEmail(String userId, String authCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUserInfo(User user) {
		// TODO Auto-generated method stub

	}

}
