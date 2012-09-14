package org.siraya.rent.user.service;

import junit.framework.Assert;

import org.junit.Test;
import org.siraya.rent.pojo.User;
import org.siraya.rent.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.Device;
@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"})

public class TestUserService  extends AbstractJUnit4SpringContextTests{
	@Autowired
	private IUserService userService;
	@Test   
	public void testNewUser()throws Exception{
		User user =userService.newUserByMobileNumber(886,"+88653936072283");
		Assert.assertNotNull(user.getId());
	}
	
	@Test
	public void testNewDevice()throws Exception{
		User user = new User();
		user.setStatus(0);
	   	long time=java.util.Calendar.getInstance().getTimeInMillis();
		user.setId("u"+time/1000);
		Device device = new Device();
		device.setUser(user);
		userService.newDevice(device);
		Assert.assertNotNull(device.getId());
	}
	
}
