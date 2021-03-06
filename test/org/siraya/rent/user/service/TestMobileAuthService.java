<<<<<<< HEAD
package org.siraya.rent.user.service;
import org.siraya.rent.user.dao.IMobileAuthRequestDao;
import java.util.HashMap;
import java.util.Map;
import org.siraya.rent.donttry.service.IDontTryService;

import org.siraya.rent.user.dao.IMobileAuthResponseDao;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.siraya.rent.pojo.MobileAuthRequest;
import org.siraya.rent.pojo.MobileAuthResponse;
import org.siraya.rent.pojo.User;
import org.siraya.rent.user.service.IMobileAuthService;
import org.siraya.rent.pojo.Device;
import org.junit.Before;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.utils.EncodeUtility;
//@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"})
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.mobile.service.IMobileGatewayService;
public class TestMobileAuthService {

	private MobileAuthService mobileAuthService;
	private Mockery context;
	String deviceId= "123";
	String userId= "456";
	String mobilePhone="87E45511C317F40D40509943AD6AF951";
	Device device =null;
	private IApplicationConfig config;
	User user = new User();
	private String authCode = "809FFECF2869157CA16B50F1A3E6B75C";	
	private IDeviceDao deviceDao;
	private EncodeUtility encodeUtility;
	private IUserDAO userDao;
	private IDontTryService dontTryService;
	private Map<String, Object> setting;
	private IMobileGatewayService mobileGatewayService;
	private boolean isMock = true;
	private IMobileAuthRequestDao mobileAuthRequestDao;
	private IMobileAuthResponseDao mobileAuthResponseDao;
	private MobileAuthRequest mobileAuthRequest = new MobileAuthRequest();
	private MobileAuthResponse mobileAuthResponse = new MobileAuthResponse();
	@Before
	public void setUp(){
		if (isMock){
			context = new JUnit4Mockery();
			mobileAuthService = new MobileAuthService();
		}
		user.setId(userId);
		user.setCc("TW");
		user.setLang("zh");
		user.setStatus(0);
		user.setMobilePhone(mobilePhone);
		device = new Device();
		device.setId("test id");
		device.setUser(user);
		device.setStatus(0);
		device.setToken(authCode);
		if (isMock){
			deviceDao = context.mock(IDeviceDao.class);	
			this.mobileGatewayService = context.mock(IMobileGatewayService.class);
			this.mobileAuthRequestDao = context.mock(IMobileAuthRequestDao.class);
			this.mobileAuthResponseDao = context.mock(IMobileAuthResponseDao.class);
			this.dontTryService = context.mock(IDontTryService.class);	
			userDao = context.mock(IUserDAO.class);	
			mobileAuthService.setDeviceDao(deviceDao);
			mobileAuthService.setUserDao(userDao);
			config = context.mock(IApplicationConfig.class);
			mobileAuthService.setApplicationConfig(config);
			setting = new HashMap<String, Object>();
			setting.put("auth_retry_limit", 3);
			setting.put("general", "thebestsecretkey");
			setting.put("auth_verify_timeout", 1800);
			
			encodeUtility = new EncodeUtility();
			encodeUtility.setApplicationConfig(config);
			mobileAuthService.setEncodeUtility(encodeUtility);
			mobileAuthService.setMobileGatewayService(mobileGatewayService);
			mobileAuthService.setDontTryService(dontTryService);
			mobileAuthService.setMobileAuthRequestDao(mobileAuthRequestDao);
			mobileAuthService.setMobileAuthResponseDao(mobileAuthResponseDao);
		}
		mobileAuthRequest.setRequestId("request id ");
		mobileAuthRequest.setMobilePhone("FE4DC1C73AE63F2BDE5C80E1E3B93BFC");
		mobileAuthRequest.setToken("FE4DC1C73AE63F2BDE5C80E1E3B93BFC");
		mobileAuthRequest.setAuthUserId(device.getUserId());
		mobileAuthRequest.setDevice(device);
		mobileAuthRequest.setRequestTime(0);
		mobileAuthRequest.setAuthCode("886911826844");
		mobileAuthRequest.setRequestFrom("test user");
		mobileAuthRequest.setAuthUserId(user.getId());
		mobileAuthRequest.setUser(user);
		mobileAuthResponse.setDevice(device);
	}
	
	@Test   
	public void testSendAuthMessage()throws Exception{
		//expectation
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("general");
					will(returnValue(setting));
					one(config).get("keydb");
					will(returnValue(setting));
					one(deviceDao).getDeviceByDeviceIdAndUserId(deviceId,userId);								
					will(returnValue(device));

					one(userDao).getUserByUserId(device.getUserId());
					will(returnValue(device.getUser()));
					
					one(deviceDao)
							.updateStatusAndRetryCount(
									with(any(String.class)),
									with(any(String.class)),
									with(any(int.class)), 
									with(any(int.class)),
									with(any(long.class)));
					will(returnValue(1));
					one(deviceDao).getDeviceByDeviceIdAndUserId(device.getId(),device.getUserId());
					will(returnValue(device));
					
					one(mobileGatewayService).sendSMS(with(any(String.class)),with(any(String.class)));
				}
			});	
		}
		
		
		mobileAuthService.sendAuthMessage(deviceId,userId);
	}
	
	@Test   
	public void testVerifyAuthCode() throws Exception{
		//expectation
		if (isMock) {
			device.setStatus(1);
			context.checking(new Expectations() {
				{
					one(config).get("general");
					will(returnValue(setting));
					one(config).get("keydb");
					will(returnValue(setting));
					one(deviceDao).getDeviceByDeviceIdAndUserId(deviceId,userId);								
					will(returnValue(device));

					one(userDao).getUserByUserId(device.getUserId());
					will(returnValue(device.getUser()));
					
					one(deviceDao)
							.updateStatusAndRetryCount(
									with(any(String.class)),
									with(any(String.class)),
									with(any(int.class)), 
									with(any(int.class)),
									with(any(long.class)));
					will(returnValue(1));
					one(deviceDao).getDeviceByDeviceIdAndUserId(device.getId(),device.getUserId());
					will(returnValue(device));
					

					
					one(userDao).updateUserStatus(
							with(any(String.class)),
							with(any(int.class)),
							with(any(int.class)),
							with(any(long.class)));
					will(returnValue(1));
				}
			});	
		}
		

		
		mobileAuthService.verifyAuthCode(device.getId(),device.getUserId(), "12313131");		
	}
	
	@Test 
	public void testVerifyAuthCodeByMobilePhone(){
		//expectation
		if (isMock) {
			device.setStatus(1);
			context.checking(new Expectations() {
				{
					one(config).get("general");
					will(returnValue(setting));
					one(config).get("keydb");
					will(returnValue(setting));
					one(userDao).getUserByMobilePhone(mobilePhone);
					will(returnValue(user));	
				
					one(deviceDao).getDeviceByUserIdAndStatusAuthing(userId);								
					will(returnValue(device));

					one(userDao).getUserByUserId(device.getUserId());
					will(returnValue(device.getUser()));
					
					one(deviceDao)
							.updateStatusAndRetryCount(
									with(any(String.class)),
									with(any(String.class)),
									with(any(int.class)), 
									with(any(int.class)),
									with(any(long.class)));
					will(returnValue(1));
					one(deviceDao).getDeviceByDeviceIdAndUserId(device.getId(),device.getUserId());
					will(returnValue(device));
					

					
					one(userDao).updateUserStatus(
							with(any(String.class)),
							with(any(int.class)),
							with(any(int.class)),
							with(any(long.class)));
					will(returnValue(1));
			
				}
			});	
		}
				
		mobileAuthService.verifyAuthCodeByMobilePhone("55555555", "12313131");		
	}
	
	
	@Test(expected=org.siraya.rent.utils.RentException.class)   
	public void testRetryMax()throws Exception{
		try {
			if (isMock) {
				context.checking(new Expectations() {
					{
						one(config).get("general");
						will(returnValue(setting));
						one(deviceDao).getDeviceByDeviceIdAndUserId(deviceId,userId);								
						device.setAuthRetry(5);
						will(returnValue(device));

						one(userDao).getUserByUserId(device.getUserId());
						will(returnValue(device.getUser()));
						one(deviceDao).updateStatusAndRetryCount(with(any(String.class)),
								with(any(String.class)),
								with(any(int.class)), 
								with(any(int.class)),
								with(any(long.class)));
					}
				});	
			}
			mobileAuthService.sendAuthMessage(deviceId,userId);
		}catch(Exception e){
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=org.siraya.rent.utils.RentException.class)   
	public void testRetryMaxInVerify()throws Exception{
		//expectation
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("general");
					will(returnValue(setting));
					one(deviceDao).getDeviceByDeviceIdAndUserId(device.getId(),device.getUserId());
					will(returnValue(device));
					one(userDao).getUserByUserId(device.getUserId());
					will(returnValue(device.getUser()));
				}
			});	
		}
		device.setStatus(DeviceStatus.Authing.getStatus());
		device.setAuthRetry(5);
		mobileAuthService.verifyAuthCode(device.getId(), device.getUserId(), authCode);
	}
	
	@Test 
	public void testSendAuthMessageByMobileAuthRequest(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("general");
					will(returnValue(setting));
					one(dontTryService).doTry(mobileAuthRequest.getRequestId(),
							IDontTryService.DontTryType.Life, 3);
					one(config).get("keydb");
					will(returnValue(setting));
					one(mobileGatewayService).sendSMS(with(any(String.class)), with(any(String.class)));
					one(deviceDao).updateDeviceStatus(with(any(String.class)),with(any(String.class)), 
							with(any(int.class)), with(any(int.class)), with(any(long.class)));
					will(returnValue(1));
					one(mobileAuthResponseDao).updateResponseStatus(with(any(MobileAuthResponse.class)));
					will(returnValue(1));
				}
			});	
		}
		mobileAuthService.sendAuthMessage(mobileAuthRequest, mobileAuthResponse);
	}
	
	@Test
	public void testVerifyMobileAuthRequestCode(){
		mobileAuthRequest.setStatus(1);
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(mobileAuthRequestDao).get(mobileAuthRequest.getRequestId());
					will(returnValue(mobileAuthRequest));
					one(config).get("general");
					will(returnValue(setting));
					one(dontTryService).doTry(mobileAuthRequest.getRequestId(),
							IDontTryService.DontTryType.Life, 3);
					one(config).get("keydb");
					will(returnValue(setting));
					one(userDao).getUserByUserId(mobileAuthRequest.getAuthUserId());
					will(returnValue(user));
					one(userDao).updateUserStatus(with(any(String.class)), with(any(Integer.class)), 
							with(any(Integer.class)), with(any(long.class)));
					will(returnValue(1));
					one(mobileAuthResponseDao).updateResponse(with(any(MobileAuthResponse.class)));
					will(returnValue(1));
					one(userDao).getUserByUserId(mobileAuthRequest.getUserId());
					will(returnValue(user));
					one(deviceDao).getDeviceByDeviceIdAndUserId("SSO", mobileAuthRequest.getRequestFrom());
					will(returnValue(device));

				}
			});	
		}
		mobileAuthService.verifyMobileAuthRequestCode(mobileAuthRequest);
		
	}
}
=======
package org.siraya.rent.user.service;
import org.siraya.rent.user.dao.IMobileAuthRequestDao;
import java.util.HashMap;
import java.util.Map;
import org.siraya.rent.donttry.service.IDontTryService;

import org.siraya.rent.user.dao.IMobileAuthResponseDao;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.siraya.rent.pojo.MobileAuthRequest;
import org.siraya.rent.pojo.MobileAuthResponse;
import org.siraya.rent.pojo.User;
import org.siraya.rent.user.service.IMobileAuthService;
import org.siraya.rent.pojo.Device;
import org.junit.Before;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.utils.EncodeUtility;
//@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"})
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.springframework.web.client.RestOperations;
public class TestMobileAuthService {

	private MobileAuthService mobileAuthService;
	private Mockery context;
	String deviceId= "123";
	String userId= "456";
	String mobilePhone="29BFD9986CFB4E66CEDB3574267CDE30";
	Device device =null;
	private IApplicationConfig config;
	User user = new User();
	private String authCode = "8800312F4B86C306E1496CEAA2FEE2FC";	
	private IDeviceDao deviceDao;
	private EncodeUtility encodeUtility;
    private RestOperations restTemplate;
	private IUserDAO userDao;
	private IDontTryService dontTryService;
	private Map<String, Object> setting;
	private IMobileGatewayService mobileGatewayService;
	private boolean isMock = true;
	private IMobileAuthRequestDao mobileAuthRequestDao;
	private IMobileAuthResponseDao mobileAuthResponseDao;
	private MobileAuthRequest mobileAuthRequest = new MobileAuthRequest();
	private MobileAuthResponse mobileAuthResponse = new MobileAuthResponse();
	@Before
	public void setUp(){
		if (isMock){
			context = new JUnit4Mockery();
			restTemplate = context.mock(RestOperations.class);	
			mobileAuthService = new MobileAuthService(restTemplate);
		}
		user.setId(userId);
		user.setCc("TW");
		user.setLang("zh");
		user.setStatus(0);
		user.setMobilePhone(mobilePhone);
		device = new Device();
		device.setId("test id");
		device.setUser(user);
		device.setStatus(0);
		device.setToken(authCode);
		if (isMock){
			deviceDao = context.mock(IDeviceDao.class);	
			this.mobileGatewayService = context.mock(IMobileGatewayService.class);
			this.mobileAuthRequestDao = context.mock(IMobileAuthRequestDao.class);
			this.mobileAuthResponseDao = context.mock(IMobileAuthResponseDao.class);
			this.dontTryService = context.mock(IDontTryService.class);	
			userDao = context.mock(IUserDAO.class);	
			mobileAuthService.setDeviceDao(deviceDao);
			mobileAuthService.setUserDao(userDao);
			config = context.mock(IApplicationConfig.class);
			mobileAuthService.setApplicationConfig(config);
			setting = new HashMap<String, Object>();
			setting.put("auth_retry_limit", 3);
			setting.put("general", "thebestsecretkey");
			setting.put("auth_verify_timeout", 1800);
			setting.put("callback_max_retry", 10);
			encodeUtility = new EncodeUtility();
			encodeUtility.setApplicationConfig(config);
			mobileAuthService.setEncodeUtility(encodeUtility);
			mobileAuthService.setMobileGatewayService(mobileGatewayService);
			mobileAuthService.setDontTryService(dontTryService);
			mobileAuthService.setMobileAuthRequestDao(mobileAuthRequestDao);
			mobileAuthService.setMobileAuthResponseDao(mobileAuthResponseDao);
		}
		mobileAuthRequest.setRequestId("request id ");
		mobileAuthRequest.setMobilePhone("29BFD9986CFB4E66CEDB3574267CDE30");
		mobileAuthRequest.setToken("E81FC6F81B71D6447CC9E1A1B6376375");
		mobileAuthRequest.setAuthUserId(device.getUserId());
		mobileAuthRequest.setDevice(device);
		mobileAuthRequest.setRequestTime(0);
		mobileAuthRequest.setAuthCode("615202");
		mobileAuthRequest.setCallback("http://www.yahoo.com");
		mobileAuthRequest.setRequestFrom("test user");
		mobileAuthRequest.setAuthUserId(user.getId());
		mobileAuthRequest.setUser(user);
		mobileAuthResponse.setDevice(device);
	}
	
	@Test   
	public void testSendAuthMessage()throws Exception{
		//expectation
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("general");
					will(returnValue(setting));
					one(config).get("keydb");
					will(returnValue(setting));
					one(deviceDao).getDeviceByDeviceIdAndUserId(deviceId,userId);								
					will(returnValue(device));

					one(userDao).getUserByUserId(device.getUserId());
					will(returnValue(device.getUser()));
					
					one(deviceDao)
							.updateStatusAndRetryCount(
									with(any(String.class)),
									with(any(String.class)),
									with(any(int.class)), 
									with(any(int.class)),
									with(any(long.class)));
					will(returnValue(1));
					one(deviceDao).getDeviceByDeviceIdAndUserId(device.getId(),device.getUserId());
					will(returnValue(device));
					
					one(mobileGatewayService).sendSMS(with(any(String.class)), with(any(String.class)),with(any(String.class)));
				}
			});	
		}
		
		
		mobileAuthService.sendAuthMessage(deviceId,userId);
	}
	
	@Test   
	public void testVerifyAuthCode() throws Exception{
		//expectation
		if (isMock) {
			device.setStatus(1);
			context.checking(new Expectations() {
				{
					one(config).get("general");
					will(returnValue(setting));
					one(config).get("keydb");
					will(returnValue(setting));
					one(deviceDao).getDeviceByDeviceIdAndUserId(deviceId,userId);								
					will(returnValue(device));

					one(userDao).getUserByUserId(device.getUserId());
					will(returnValue(device.getUser()));
					
					one(deviceDao)
							.updateStatusAndRetryCount(
									with(any(String.class)),
									with(any(String.class)),
									with(any(int.class)), 
									with(any(int.class)),
									with(any(long.class)));
					will(returnValue(1));
					one(deviceDao).getDeviceByDeviceIdAndUserId(device.getId(),device.getUserId());
					will(returnValue(device));
					

					
					one(userDao).updateUserStatus(
							with(any(String.class)),
							with(any(int.class)),
							with(any(int.class)),
							with(any(long.class)));
					will(returnValue(1));
				}
			});	
		}
		

		
		mobileAuthService.verifyAuthCode(device.getId(),device.getUserId(), "794917");		
	}
	
	@Test 
	public void testVerifyAuthCodeByMobilePhone(){
		//expectation
		if (isMock) {
			device.setStatus(1);
			context.checking(new Expectations() {
				{
					one(config).get("general");
					will(returnValue(setting));
					one(config).get("keydb");
					will(returnValue(setting));
					one(userDao).getUserByMobilePhone(mobilePhone);
					will(returnValue(user));	
				
					one(deviceDao).getDeviceByUserIdAndStatusAuthing(userId);								
					will(returnValue(device));

					one(userDao).getUserByUserId(device.getUserId());
					will(returnValue(device.getUser()));
					
					one(deviceDao)
							.updateStatusAndRetryCount(
									with(any(String.class)),
									with(any(String.class)),
									with(any(int.class)), 
									with(any(int.class)),
									with(any(long.class)));
					will(returnValue(1));
					one(deviceDao).getDeviceByDeviceIdAndUserId(device.getId(),device.getUserId());
					will(returnValue(device));
					

					
					one(userDao).updateUserStatus(
							with(any(String.class)),
							with(any(int.class)),
							with(any(int.class)),
							with(any(long.class)));
					will(returnValue(1));
			
				}
			});	
		}
				
		mobileAuthService.verifyAuthCodeByMobilePhone("+886936072283", "794917");		
	}
	
	
	@Test(expected=org.siraya.rent.utils.RentException.class)   
	public void testRetryMax()throws Exception{
		try {
			if (isMock) {
				context.checking(new Expectations() {
					{
						one(config).get("general");
						will(returnValue(setting));
						one(deviceDao).getDeviceByDeviceIdAndUserId(deviceId,userId);								
						device.setAuthRetry(5);
						will(returnValue(device));

						one(userDao).getUserByUserId(device.getUserId());
						will(returnValue(device.getUser()));
						one(deviceDao).updateStatusAndRetryCount(with(any(String.class)),
								with(any(String.class)),
								with(any(int.class)), 
								with(any(int.class)),
								with(any(long.class)));
					}
				});	
			}
			mobileAuthService.sendAuthMessage(deviceId,userId);
		}catch(Exception e){
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	@Test(expected=org.siraya.rent.utils.RentException.class)   
	public void testRetryMaxInVerify()throws Exception{
		//expectation
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("general");
					will(returnValue(setting));
					one(deviceDao).getDeviceByDeviceIdAndUserId(device.getId(),device.getUserId());
					will(returnValue(device));
					one(userDao).getUserByUserId(device.getUserId());
					will(returnValue(device.getUser()));
				}
			});	
		}
		device.setStatus(DeviceStatus.Authing.getStatus());
		device.setAuthRetry(5);
		mobileAuthService.verifyAuthCode(device.getId(), device.getUserId(), authCode);
	}
	
	@Test 
	public void testSendAuthMessageByMobileAuthRequest(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("general");
					will(returnValue(setting));
					one(dontTryService).doTry(mobileAuthRequest.getRequestId(),
							IDontTryService.DontTryType.Life, 3);
					one(config).get("keydb");
					will(returnValue(setting));
					one(mobileGatewayService).sendSMS(with(any(String.class)), with(any(String.class)), with(any(String.class)));
					one(deviceDao).updateDeviceStatus(with(any(String.class)),with(any(String.class)), 
							with(any(int.class)), with(any(int.class)), with(any(long.class)));
					will(returnValue(1));
					one(mobileAuthResponseDao).updateResponseStatus(with(any(MobileAuthResponse.class)));
					will(returnValue(1));
				}
			});	
		}
		mobileAuthService.sendAuthMessage(mobileAuthRequest, mobileAuthResponse);
	}
	
	@Test
	public void testVerifyMobileAuthRequestCode(){
		mobileAuthRequest.setStatus(1);
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(mobileAuthRequestDao).get(mobileAuthRequest.getRequestId());
					will(returnValue(mobileAuthRequest));
					one(config).get("general");
					will(returnValue(setting));
					one(dontTryService).doTry(mobileAuthRequest.getRequestId(),
							IDontTryService.DontTryType.Life, 3);
					one(config).get("keydb");
					will(returnValue(setting));
					one(userDao).getUserByUserId(mobileAuthRequest.getAuthUserId());
					will(returnValue(user));
					one(userDao).updateUserStatus(with(any(String.class)), with(any(Integer.class)), 
							with(any(Integer.class)), with(any(long.class)));
					will(returnValue(1));
					one(mobileAuthResponseDao).updateResponse(with(any(MobileAuthResponse.class)));
					will(returnValue(1));
					one(userDao).getUserByUserId(mobileAuthRequest.getUserId());
					will(returnValue(user));
					one(deviceDao).getDeviceByDeviceIdAndUserId("SSO", mobileAuthRequest.getRequestFrom());
					will(returnValue(device));
					one(restTemplate).getForObject(with(any(String.class)), with(any(Class.class)), with(any(java.util.Map.class)));

				}
			});	
		}
		mobileAuthService.verifyMobileAuthRequestCode(mobileAuthRequest);
		
	}
}
>>>>>>> master
