package org.siraya.rent.rest;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.siraya.rent.filter.UserAuthorizeData;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import javax.ws.rs.core.NewCookie;
import org.siraya.rent.pojo.Session;
import junit.framework.Assert;

import org.siraya.rent.pojo.Device;
import org.siraya.rent.utils.EncodeUtility;
import org.siraya.rent.utils.IApplicationConfig;

public class TestCookieUtils  {
	@Autowired
	CookieUtils cookieUtils;
	private String deviceId ="test new device";
	private EncodeUtility encodeUtility;
	private IApplicationConfig config;
	private boolean isMock = true;
	private Mockery context;
	private Map<String, Object> setting;
	long time = java.util.Calendar.getInstance().getTimeInMillis();
	@Before
	public void setUp(){
		if (isMock){
			context = new JUnit4Mockery();
			cookieUtils = new CookieUtils();
			config = context.mock(IApplicationConfig.class);
			encodeUtility = new EncodeUtility();
			encodeUtility.setApplicationConfig(config);
			cookieUtils.setEncodeUtility(encodeUtility);
			setting = new HashMap<String, Object>();
			setting.put("cookie", "thebestsecretkey");
		}


	}
	@Test 
	public void testNewCookie(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("keydb");
					will(returnValue(setting));
				}
			});	
		}
		NewCookie cookie = cookieUtils.newDeviceCookie("test");
	}
	
	@Test 	
	public void testNewCookie2()throws Exception{
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("keydb");
					will(returnValue(setting));
				}
			});	
		}
		Device device = new Device();
		device.setId(deviceId);
		device.setUserId("test list devices");
		NewCookie cookie = cookieUtils.createDeviceCookie(device);
		String deviceCookie = cookie.getValue();
		//
		//test extract
		UserAuthorizeData userAuthorizeData=new UserAuthorizeData();
		cookieUtils.extractDeviceCookie(deviceCookie, userAuthorizeData);
		Assert.assertEquals(deviceId, userAuthorizeData.getDeviceId());
	}
	
	@Test
	public void testRemoveCookie(){
		NewCookie cookie =cookieUtils.removeDeviceCookie();
		Assert.assertEquals(-1, cookie.getMaxAge());
	}
	
	@Test
	public void testNewNullSessionCookie(){
		Session session = new Session();
		NewCookie cookie = cookieUtils.newSessionCookie(session);
		Assert.assertNull(cookie);
	}
	
	@Test
	public void testNewSessionCookie(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("keydb");
					will(returnValue(setting));
				}
			});	
		}
		Session session = new Session();
		session.setUserId("u"+time/1000);
		session.setDeviceId("d"+time/1000);
		NewCookie cookie = cookieUtils.newSessionCookie(session);
		Assert.assertNotNull(cookie);
	}
	
	@Test
	public void testExtractNullSessionCookie(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("keydb");
					will(returnValue(setting));
				}
			});	
		}
		Session session = cookieUtils.extractSessionCookie("123", new UserAuthorizeData());
		Assert.assertNull(session);
	}
	@Test
	public void testExtractSessionCookieUserNotMatch(){
		String cookie = "7D15507A37D565CF11DFF514ECC51D6A96823B5607660DE289839EEDCC9BA9D53FFA4A6D27F8D36E1D92DFAD66A435ABA726D396E7D3048C35A44E6772E850150891724A3E9A2BD0437DF864FFCE67317E27222C6D66F94EE692C9BCB2513A25";
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("keydb");
					will(returnValue(setting));
				}
			});	
		}
		UserAuthorizeData userAuthorizeData  = new UserAuthorizeData();

		Session session = cookieUtils.extractSessionCookie(cookie,userAuthorizeData );
		Assert.assertNull(session);
	}

	@Test
	public void testExtractSessionCookieUser(){
		String cookie = "107FF6C8752C23E782EBAF9447D9ED5659CBF357C42E1263079F7EADCA8022E11127DF9CF615EA81BCEA1E166FE3E9042F520137F4B2A94C165CF2B7D9D338E99AAF2290DA3E3FD1D1E283F0868C87718E5C7060522AE04B1A720AEAE5BD3398";
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("keydb");
					will(returnValue(setting));
				}
			});	
		}
		UserAuthorizeData userAuthorizeData  = new UserAuthorizeData();
		userAuthorizeData.setDeviceId("d1350194731");
		userAuthorizeData.setUserId("u1350194731");
		Session session = cookieUtils.extractSessionCookie(cookie,userAuthorizeData);
		Assert.assertNotNull(session);
	}

}
