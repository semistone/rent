package org.siraya.rent.pojo;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.filter.UserRole;

public class TestSession {
	Validator validator;
	private Session session;
	@Before
	public void setUp(){
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		session = new Session();
		session.setUserId("user");
		session.setDeviceId("device");
		session.setLastLoginIp("127.0.0.1");
		session.setId("123");
	}
	@Test
	public void testToString(){
		Assert.assertEquals("123:device:user:127.0.0.1:", session.toString());
		boolean role = session.isUserInRole(UserRole.UserRoleId.DEVICE_CONFIRMED.getRoleId());
		Assert.assertFalse(role);
	}
	
	@Test
	public void testSetDeviceVerified(){
		session.setDeviceVerified(true);
		Assert.assertEquals(1, session.getRoles().size());
		boolean role = session.isUserInRole(UserRole.UserRoleId.DEVICE_CONFIRMED.getRoleId());
		Assert.assertTrue(role);
	}
}
