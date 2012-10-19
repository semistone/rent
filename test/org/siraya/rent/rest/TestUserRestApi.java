package org.siraya.rent.rest;

import org.jmock.Expectations;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.rest.UserRestApi;
import org.siraya.rent.user.service.IUserService;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.pojo.MobileAuthRequest;
import org.siraya.rent.pojo.MobileAuthResponse;
import org.siraya.rent.pojo.User;
import org.siraya.rent.filter.UserAuthorizeData;
import javax.ws.rs.core.Response;
import org.siraya.rent.utils.EncodeUtility;
import org.siraya.rent.utils.IApplicationConfig;

import junit.framework.Assert;
import org.siraya.rent.user.service.IMobileAuthService;

public class TestUserRestApi{

	UserRestApi userRestApi;
	private Mockery context;
	private IMobileAuthService mobileAuthService;
	private IUserService userService;
	private boolean isMock = true;
	private String deviceId= "23131";
	private String userId = "12313";
	private String authCode="123";
	private int  cc=886;
	private String mobilePhone= "886234242342";
	private Device device = new Device();
	private User  user = new User();
	private CookieUtils cookieUtils = new CookieUtils();
	private java.util.Map<String, Object> request;
	private MobileAuthRequest mobileAuthRequest;
	private MobileAuthResponse mobileAuthResponse;
	private EncodeUtility encodeUtility = new EncodeUtility();
	private IApplicationConfig config;
	private Map<String, Object> setting;
	@Before
	public void setUp(){
		request = new java.util.HashMap<String,Object>();
		if (isMock){
			userRestApi = new UserRestApi();
			context = new JUnit4Mockery();
			config = context.mock(IApplicationConfig.class);
			mobileAuthService = context.mock(IMobileAuthService.class);	
			userRestApi.setMobileAuthService(mobileAuthService);
			userService = context.mock(IUserService.class);	
			userRestApi.setUserService(userService);
			UserAuthorizeData userAuthorizeData = new UserAuthorizeData();
			userAuthorizeData.setUserId(userId);
			userAuthorizeData.setDeviceId(deviceId);
			userRestApi.setUserAuthorizeData(userAuthorizeData);
			mobileAuthResponse = new MobileAuthResponse();
			mobileAuthRequest = new MobileAuthRequest();
			mobileAuthRequest.setRequestId("123565623422");
			mobileAuthRequest.setCountryCode("886");
			mobileAuthRequest.setSign("test");
			mobileAuthRequest.setMobilePhone("8869234242");
			mobileAuthRequest.setRequestFrom("2343242");
			mobileAuthRequest.setRequestTime(12413131);
			userRestApi.setCookieUtils(cookieUtils);
			cookieUtils.setEncodeUtility(encodeUtility);
			encodeUtility.setApplicationConfig(config);
			setting = new HashMap<String, Object>();
			setting.put("cookie", "thebestsecretkey");
		}
	}
	@Test   
	public void testNewDevice()throws Exception{	
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(userService).newDevice(with(any(Device.class)));
					will(returnValue(device));
					one(userService).newUserByMobileNumber(cc, mobilePhone);
					will(returnValue(user));
					one(config).get("keydb");
					will(returnValue(setting));					
				}
			});
		}
    	long time=java.util.Calendar.getInstance().getTimeInMillis();    	
    	request.put("countryCode", Integer.toString(cc));
    	request.put("mobilePhone",mobilePhone);
		Response response = userRestApi.newDevice(request);
		Assert.assertEquals(200, response.getStatus());
	}

	@Test   
	public void testSendMobileAuthMessage() throws Exception{
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(mobileAuthService).sendAuthMessage(deviceId,userId);
				}
			});
		};
		request.put("device_id", this.deviceId);
		Response response = userRestApi.sendMobileAuthMessage();
		Assert.assertEquals(200, response.getStatus());
	}
	
	@Test 
	public void testMobileAuthCode()throws Exception{
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(mobileAuthService).verifyAuthCode(deviceId, userId, authCode);
				}
			});
		};
		request.put("device_id", this.deviceId);
		request.put("authCode", this.authCode);

		Response response = userRestApi.verifyMobileAuthCode(request);
		Assert.assertEquals(200, response.getStatus());
	}
	 
	@Test 
	public void mobileAuthRequest(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(userService).mobileAuthRequest(with(any(Device.class)), with(any(MobileAuthRequest.class)));
					will(returnValue(mobileAuthResponse));
					one(mobileAuthService).sendAuthMessage(mobileAuthRequest, mobileAuthResponse);
				}
			});
		};

		this.userRestApi.mobileAuthRequest(mobileAuthRequest);
	}
}
