package org.siraya.rent.user.service;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.pojo.User;
import org.siraya.rent.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.Device;

@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"})

public class TestUserService  extends AbstractJUnit4SpringContextTests{
	User user =new User();
	@Autowired
	private IUserService userService;
   	long time=java.util.Calendar.getInstance().getTimeInMillis();

	@Before
	public void setUp(){
		user = new User();
		user.setEmail(time+"@gmail.com");
		user.setId("0b2150d3-b437-4731-91d6-70db69660dc2");	
	}
	@Test   
	public void testNewUser()throws Exception{
		user =userService.newUserByMobileNumber(886,"88653936072283");
		Assert.assertNotNull(user.getId());
	}
	
	@Test
	public void testNewDevice()throws Exception{
		User user = new User();
		user.setStatus(0);
		user.setId("u"+time/1000);
		Device device = new Device();
		device.setUser(user);
		userService.newDevice(device);
		Assert.assertNotNull(device.getId());
	}
	
	@Test
	public void testSetupEmail() throws Exception{
		userService.setupEmail(user);
	}
}
