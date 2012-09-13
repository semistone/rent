package org.siraya.rent.user.service;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.User;
import org.siraya.rent.user.service.IMobileAuthService;
import org.siraya.rent.pojo.Device;
import org.junit.Before;
@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"})

public class TestMobileAuthService  extends AbstractJUnit4SpringContextTests{
	@Autowired
	private IMobileAuthService mobileAuthService;
	Device device =null;
	@Before
	public void setUp(){
		User user = new User();
		user.setCc("TW");
		user.setLang("zh");
		user.setMobilePhone("+886936072283");
		device = new Device();
		device.setId("test id");
		device.setUser(user);
		device.setStatus(0);
	}
	
	@Test   
	public void testSendAuthMessage()throws Exception{
		mobileAuthService.sendAuthMessage(device);
	}
}
