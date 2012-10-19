package org.siraya.rent.user.service;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.pojo.MobileAuthRequest;
import org.siraya.rent.pojo.User;
import org.siraya.rent.user.dao.IMobileAuthRequestDao;
import org.siraya.rent.user.service.IUserService;
import org.siraya.rent.user.service.UserService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.pojo.VerifyEvent;
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.utils.EncodeUtility;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.user.dao.IVerifyEventDao;
import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(locations = { "classpath*:/applicationContext*.xml" })
public class TestUserService {
	User user = new User();
	Device device = new Device();
	private UserService userService;
	long time = java.util.Calendar.getInstance().getTimeInMillis();
	private Mockery context;
	private boolean isMock = true;
	private IUserDAO userDao;
	private IDeviceDao deviceDao;
	private String deviceId = "d123";
	private String userId = "0b2150d3-b437-4731-91d6-70db69660dc2";
	private MobileAuthRequest request;
	private IApplicationConfig config;
	private IMobileAuthRequestDao mobileAuthRequestDao;
	private Map<String, Object> setting;
	private EncodeUtility encodeUtility;
	private IVerifyEventDao verifyEventDao;

	@Before
	public void setUp() {
		user = new User();
		user.setEmail(time + "@gmail.com");
		user.setId(userId);
		user.setLoginId("id" + time);
		user.setPassword("test");
		user.setStatus(0);
		userService = new UserService();
	
		device.setUser(user);
		device.setId(deviceId);
		if (isMock) {
			context = new JUnit4Mockery();
			userDao = context.mock(IUserDAO.class);
			deviceDao = context.mock(IDeviceDao.class);
			mobileAuthRequestDao = context.mock(IMobileAuthRequestDao.class);
			verifyEventDao = context.mock(IVerifyEventDao.class);

			config = context.mock(IApplicationConfig.class);
			userService.setUserDao(userDao);
			userService.setDeviceDao(deviceDao);
			userService.setApplicationConfig(config);
			userService.setMobileAuthRequestDao(mobileAuthRequestDao);
			userService.setVerifyEventDao(verifyEventDao);
			request = new MobileAuthRequest();
			request.setRequestId("r" + time / 1000);
			request.setForceReauth(false);
			request.setRequestFrom(this.userId);
			request.setRequestTime(time/1000);
			request.setCountryCode("886");
			request.setAuthUserId("test id");
			request.setMobilePhone("8862332131313");
			request.setDone("http://www.yahoo.com");
			
			setting = new HashMap<String, Object>();
			Map<String, Object> setting2;
			setting2 = new HashMap<String, Object>();
			setting.put("debug", Boolean.TRUE);
			setting.put("max_device_count", 3);
			setting.put("max_user_per_device", 4);
			setting.put("886", setting2);
			setting.put("general", "thebestsecretkey");
			setting.put("cookie", "thebestsecretkey");
			setting2.put("country", "TW");
			setting2.put("lang", "zh");

			encodeUtility = new EncodeUtility();
			userService.setEncodeUtility(encodeUtility);
			this.encodeUtility.setApplicationConfig(config);
		}
	}

	@Test
	public void testNewUser() throws Exception {
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(userDao).newUser(with(any(User.class)));
					one(config).get("mobile_country_code");
					will(returnValue(setting));
					one(config).get("keydb");
					will(returnValue(setting));
				}
			});
		}
		user = userService.newUserByMobileNumber(886, "88653936072283");
		Assert.assertNotNull(user.getId());
	}

	@Test
	public void testNewDevice() throws Exception {
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(userDao).newUser(with(any(User.class)));
					one(deviceDao).getDeviceByDeviceIdAndUserId(deviceId,
							userId);
					will(returnValue(device));

					one(deviceDao).getDeviceCountByUserId(userId);
					will(returnValue(1));
					one(deviceDao).getDeviceCountByDeviceId(deviceId);
					will(returnValue(1));
					one(config).get("general");
					will(returnValue(setting));

				}
			});
		}
		User user = new User();
		user.setStatus(0);
		user.setId("u" + time / 1000);

		userService.newDevice(device);
		Assert.assertNotNull(device.getId());
	}

	@Test
	public void testSetupEmail() throws Exception {

		if (isMock) {
			context.checking(new Expectations() {
				{
					String email = "new@email.com";
					one(userDao).getUserByUserId(user.getId());
					User user2 = new User();
					user2.setEmail(email);
					will(returnValue(user2));

					one(userDao).updateUserEmail(user2);
					one(verifyEventDao).getEventByVerifyDetailAndType(0, email);
					one(verifyEventDao).newVerifyEvent(
							with(any(VerifyEvent.class)));

				}
			});
		}
		userService.setupEmail(user);

	}

	@Test
	public void testUpdateLoginIdAndPassword() throws Exception {
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(userDao).getUserByUserId(user.getId());
					User user2 = new User();
					will(returnValue(user2));

					one(userDao).updateUserLoginIdAndPassword(user);
					will(returnValue(1));
				}
			});
		}
		userService.updateLoginIdAndPassowrd(user);
	}

	@Test
	public void testMobileAuthRequest() {
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(deviceDao).getDeviceByDeviceIdAndUserId(
							IUserService.SSO_DEVICE_ID, userId);
					device.setStatus(DeviceStatus.ApiKeyOnly.getStatus());
					will(returnValue(device));
					one(userDao).getUserByMobilePhone(with(any(String.class)));
					// check mobile phone and return null
					one(mobileAuthRequestDao).newRequest(request);
					one(config).get("general");
					will(returnValue(setting));
	

					//
					// copy from new device by mobile phone
					//
					one(userDao).newUser(with(any(User.class)));
					one(config).get("mobile_country_code");
					will(returnValue(setting));
					one(config).get("keydb");
					will(returnValue(setting));
					
					one(deviceDao).getDeviceByDeviceIdAndUserId(
							with(any(String.class)),with(any(String.class)));
					will(returnValue(device));
				}
			});

		}

		userService.mobileAuthRequest(device, request);
	}
}
