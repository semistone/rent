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
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.utils.EncodeUtility;
import org.siraya.rent.utils.IApplicationConfig;

import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(locations = { "classpath*:/applicationContext*.xml" })
public class TestUserService extends AbstractJUnit4SpringContextTests {
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
			config = context.mock(IApplicationConfig.class);
			userService.setUserDao(userDao);
			userService.setDeviceDao(deviceDao);
			userService.setApplicationConfig(config);
			userService.setMobileAuthRequestDao(mobileAuthRequestDao);
			request = new MobileAuthRequest();
			request.setRequestId("r" + time / 1000);
			request.setForceReauth(false);
			request.setRequestFrom(this.userId);
			request.setRequestTime(time / 1000);
			request.setAuthUserId("test id");
			request.setMobilePhone("771232131313");
			request.setDone("http://www.yahoo.com");
			request.setSign("96EDD356D2C863B5483CA811061BD639CB9FF29C");
			setting = new HashMap<String, Object>();
			setting.put("debug", Boolean.TRUE);
		}
	}

	@Test
	public void testNewUser() throws Exception {
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(userDao).newUser(with(any(User.class)));
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
					one(userDao).getUserByUserId(user.getId());
					User user2 = new User();
					user2.setEmail("new@email.com");
					will(returnValue(user2));

					one(userDao).updateUserEmail(user2);
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
					one(userDao).getUserByMobilePhone(request.getMobilePhone());
					one(mobileAuthRequestDao).newRequest(request);
					one(config).get("general");
					will(returnValue(setting));

				}
			});

		}

		userService.mobileAuthRequest(device, request);
	}
}
