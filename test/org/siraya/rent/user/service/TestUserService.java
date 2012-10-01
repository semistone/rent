package org.siraya.rent.user.service;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.pojo.User;
import org.siraya.rent.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.user.dao.IDeviceDao;
@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"} )

public class TestUserService   extends AbstractJUnit4SpringContextTests{
	User user =new User();
	Device device = new Device();
	@Autowired
	private IUserService userService;
   	long time=java.util.Calendar.getInstance().getTimeInMillis();
	private Mockery context;
	private boolean isMock = true;
	private IUserDAO userDao;
	private IDeviceDao deviceDao;
	private String deviceId ="d123";
	private String userId = "0b2150d3-b437-4731-91d6-70db69660dc2";
	@Before
	public void setUp(){
		user = new User();
		user.setEmail(time+"@gmail.com");
		user.setId(userId);	
		user.setLoginId("id"+time);
		user.setPassword("test");
		user.setStatus(0);

		device.setUser(user);
		device.setId(deviceId);
		if (isMock){
			context = new JUnit4Mockery();
			userDao = context.mock(IUserDAO.class);
			deviceDao = context.mock(IDeviceDao.class);
			userService.setUserDao(userDao);
			userService.setDeviceDao(deviceDao);
		}
	}
	@Test   
	public void testNewUser()throws Exception{
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(userDao).newUser(with(any(User.class)));
				}
			});
		}
		user =userService.newUserByMobileNumber(886,"88653936072283");
		Assert.assertNotNull(user.getId());
	}
	
	@Test
	public void testNewDevice()throws Exception{
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(userDao).newUser(with(any(User.class)));
					one(deviceDao).getDeviceByDeviceIdAndUserId(deviceId, userId);
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
		user.setId("u"+time/1000);

		userService.newDevice(device);
		Assert.assertNotNull(device.getId());
	}
	
	@Test
	public void testSetupEmail() throws Exception{
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
	public void testUpdateLoginIdAndPassword()throws Exception{
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
}
