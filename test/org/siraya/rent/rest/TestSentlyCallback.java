package org.siraya.rent.rest;

import javax.ws.rs.QueryParam;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.user.service.IMobileAuthService;
import org.siraya.rent.utils.IApplicationConfig;
import java.util.HashMap;
public class TestSentlyCallback {
	SentlyCallback test;

	private boolean isMock = true;
	private IMobileAuthService mobileAuthService;
	private IApplicationConfig applicationConfig;
	private Mockery context;
	private HashMap<String,String> setting;
	@Before
	public void setUp(){
		test = new SentlyCallback();

		if (isMock){
			context = new JUnit4Mockery();
			mobileAuthService = context.mock(IMobileAuthService.class);
			applicationConfig = context.mock(IApplicationConfig.class);
			test.setApplicationConfig(applicationConfig);
			test.setMobileAuthService(mobileAuthService);
			setting = new HashMap<String,String>();
			setting.put("callback_pass", "123456789");

		}
	}
	
	@Test   
	public void testReceiveMessage()throws Exception{
		String from = "+3123123131";
		String text = "xx R:2332 xx";
		String password="123456789";
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(mobileAuthService).verifyAuthCodeByMobilePhone("3123123131", "2332");
					one(applicationConfig).get("sently");
					will(returnValue(setting));
				}
			});
		}
		test.receiveMessage(password,from, text);
	}
}
