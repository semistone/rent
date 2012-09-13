package org.siraya.rent.user.service;

import junit.framework.Assert;

import org.junit.Test;
import org.siraya.rent.pojo.User;
import org.siraya.rent.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"})

public class TestUserService  extends AbstractJUnit4SpringContextTests{
	@Autowired
	private IUserService userService;
	@Test   
	public void testNewUser()throws Exception{
		User user =userService.newUserByMobileNumber(886,"+88653936072283");
		Assert.assertNotNull(user.getId());
	}
}
