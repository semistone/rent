package org.siraya.rent.user.service;

import junit.framework.Assert;

import org.siraya.rent.pojo.User;
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.utils.IApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;
import java.util.Map;
import javax.servlet.jsp.jstl.core.Config;
@Service("userService")
public class UserService implements IUserService {
    @Autowired
    private IUserDAO userDao;
    @Autowired
    private IApplicationConfig applicationConfig;
    
    private static Logger logger = LoggerFactory.getLogger(UserService.class);
    private static  Pattern mobilePhonePattern = Pattern.compile("^\\+\\d*$");
    
	/**
	 * new user mobile number
	 * 
	 * @param cc country code
	 * @param moblie phone number
	 * @exception DuplicateKeyException duplicate mobile number
	 */
	public String newUserByMobileNumber(int cc,String mobilePhone) throws Exception {
		//
		// verify format
		//
		Assert.assertTrue("phone number format error", mobilePhonePattern
				.matcher(mobilePhone).find());
		Map<String,Object> mobileCountryCode = (Map<String,Object>) applicationConfig.get("mobile_country_code");
		Assert.assertTrue("cc not exist in mobile country code "+cc, 
				mobileCountryCode.containsKey(cc)
		);
		Assert.assertTrue("cc code not match",mobilePhone.startsWith("+"+cc));
		
		//
		// setup user object
		//
		Map<String,Object> map=(Map<String,Object>)mobileCountryCode.get(cc);
		User user = new User();
		String id = java.util.UUID.randomUUID().toString();
		user.setId(id);
		// second is enough
		long time = java.util.Calendar.getInstance().getTime().getTime() / 1000;
		user.setCreated(time);
		user.setModified(time);
		user.setMobilePhone(mobilePhone);
		user.setCc((String)map.get("country"));
		user.setLang((String)map.get("lang"));
		user.setTimezone((String)map.get("timezone"));
		user.setStatus(UserStatus.Init.getStatus());
		//
		// insert into database.
		//
		userDao.newUser(user);
		logger.info("create new user id is " + id);
		return id;
	
	}

	@Override
	public String newDevice(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void verifyMobile(String deviceId, String authCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEmail(String userId, String email) {
		// TODO Auto-generated method stub

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
