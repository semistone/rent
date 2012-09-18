package org.siraya.rent.rest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.rest.UserRestApi;
import org.siraya.rent.user.dao.IDeviceDao;

import javax.ws.rs.core.Response;

import junit.framework.Assert;
import org.siraya.rent.user.service.IMobileAuthService;
@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"})
public class TestUserRestApi extends AbstractJUnit4SpringContextTests{
	@Autowired
	UserRestApi userRestApi;
	private Mockery context;
	private IMobileAuthService mobileAuthService;
	private boolean isMock = true;
	private String deviceId= "23131";
	private String authCode="123";
	
	@Before
	public void setUp(){
		if (isMock){
			context = new JUnit4Mockery();
			mobileAuthService = context.mock(IMobileAuthService.class);	
			userRestApi.setMobileAuthService(mobileAuthService);
		}
	}
	@Test   
	public void testNewDevice(){		
    	long time=java.util.Calendar.getInstance().getTimeInMillis();
    	java.util.Map<String, String >request = new java.util.HashMap<String,String>();
    	request.put("country_code", "886");
    	request.put("mobile_phone", "886"+time/1000);
		Response response = userRestApi.newDevice(request);
		Assert.assertEquals(200, response.getStatus());
	}

	@Test   
	public void testSendMobileAuthMessage() throws Exception{
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(mobileAuthService).sendAuthMessage(deviceId);
				}
			});
		};
		Response response = userRestApi.sendMobileAuthMessage(deviceId);
		Assert.assertEquals(200, response.getStatus());
	}
	
	@Test 
	public void testMobileAuthCode()throws Exception{
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(mobileAuthService).verifyAuthCode(deviceId, authCode);
				}
			});
		};
		Response response = userRestApi.verifyMobileAuthCode(deviceId, authCode);
		Assert.assertEquals(200, response.getStatus());
	}
}
