package org.siraya.rent.rest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.pojo.*;
import org.siraya.rent.user.service.*;
import org.siraya.rent.utils.EncodeUtility;

import java.util.*;
public class TestOpenApi {
	OpenApi openApi = new OpenApi();
	IApiService apiService;
	Map<String,Object> request = new HashMap<String,Object>();
	private UserAuthorizeData userAuthorizeData;
	private Mockery context;
	private EncodeUtility encodeUtility = new EncodeUtility();
	private String authData = "xxx";
	private Long timestamp;
	private String deviceId="device";
	private Session session;
	@Before
	public void setUp() throws Exception{
		context = new JUnit4Mockery();
		apiService = context.mock(IApiService.class);
		timestamp = new Long(Calendar.getInstance().getTime()
				.getTime() / 1000);
		request.put("timestamp", timestamp);
		request.put("authData", authData);
		userAuthorizeData = new UserAuthorizeData();
		openApi.setUserAuthorizeData(userAuthorizeData);
		session = new Session();
		session.setDeviceId(deviceId);
		userAuthorizeData.setSession(session);
		
		userAuthorizeData.setBrower(false);
		openApi.setApiService(apiService);
		openApi.setCookieUtils(new org.siraya.rent.rest.CookieUtils());
		openApi.getCookieUtils().setEncodeUtility(encodeUtility);
		encodeUtility.setApplicationConfig(new org.siraya.rent.utils.ApplicationConfig());
	}
	
	@Test
	public void testRequestSession(){
		context.checking(new Expectations() {
			{
				one(apiService).requestSession(userAuthorizeData.getSession(), authData, timestamp, null);
			}
		});
		openApi.requestSession(request,deviceId);

	}
	@Test
	public void testUpdateSession() {
		final String sessionKey = encodeUtility.encrypt(userAuthorizeData.getSession().toString(),"cookie");
		request.put("sessionKey", sessionKey);
		context.checking(new Expectations() {
			{
				one(apiService).updateSession(with(any(Session.class)), with(any(String.class)), with(any(Long.class)));
			}
		});
		openApi.updateSession(request);
	}
}
