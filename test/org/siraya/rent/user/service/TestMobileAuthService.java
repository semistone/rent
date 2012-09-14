package org.siraya.rent.user.service;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.User;
import org.siraya.rent.user.service.IMobileAuthService;
import org.siraya.rent.pojo.Device;
import org.junit.Before;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.user.dao.IUserDAO;
@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"})

public class TestMobileAuthService  extends AbstractJUnit4SpringContextTests{
	@Autowired
	private IMobileAuthService mobileAuthService;
	private Mockery context;
	
	Device device =null;
	private String authCode = "1234";	
	private IDeviceDao deviceDao;
	private IUserDAO userDao;
	private boolean isMock = true;
	@Before
	public void setUp(){
		if (isMock){
			context = new JUnit4Mockery();
		}
		User user = new User();
		user.setCc("TW");
		user.setLang("zh");
		user.setStatus(0);
		user.setMobilePhone("+886936072283");
		device = new Device();
		device.setId("test id");
		device.setUser(user);
		device.setStatus(0);
		device.setToken(authCode);
		if (isMock){
			deviceDao = context.mock(IDeviceDao.class);	
			userDao = context.mock(IUserDAO.class);	
			((MobileAuthService)mobileAuthService).setDeviceDao(deviceDao);
			((MobileAuthService)mobileAuthService).setUserDao(userDao);
		}
	}
	
	@Test   
	public void testSendAuthMessage()throws Exception{
		//expectation
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(deviceDao)
							.updateStatusAndRetryCount(
									with(any(String.class)),
									with(any(int.class)), 
									with(any(int.class)),
									with(any(long.class)));
					will(returnValue(1));
					one(deviceDao).getDeviceByDeviceId(device.getId());
					will(returnValue(device));
					
					one(deviceDao).updateDeviceStatus(with(any(String.class)),
							with(any(int.class)),
							with(any(int.class)),
							with(any(long.class)));
					will(returnValue(1));
					
					one(userDao).updateUserStatus(
							with(any(String.class)),
							with(any(int.class)),
							with(any(int.class)),
							with(any(long.class)));
					will(returnValue(1));
				}
			});	
		}
		
		
		mobileAuthService.sendAuthMessage(device);
		mobileAuthService.verifyAuthCode(device.getId(), authCode);
	}
	
	@Test(expected=junit.framework.AssertionFailedError.class)   
	public void testRetryMax()throws Exception{
		device.setAuthRetry(5);
		mobileAuthService.sendAuthMessage(device);
	}
	
	@Test(expected=junit.framework.AssertionFailedError.class)   
	public void testRetryMaxInVerify()throws Exception{
		//expectation
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(deviceDao).getDeviceByDeviceId(device.getId());
					will(returnValue(device));
					
				}
			});	
		}
		device.setStatus(DeviceStatus.Authing.getStatus());
		device.setAuthRetry(5);
		mobileAuthService.verifyAuthCode(device.getId(), authCode);
	}
	
	
}
