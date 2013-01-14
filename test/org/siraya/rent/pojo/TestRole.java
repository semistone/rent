package org.siraya.rent.pojo;



import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;


public class TestRole {
	private Role role;
	@Before
	public void setUp(){
		role = new Role();
	}
	@Test
	public void testRoleId(){
		role.setRoleId(1);
		Assert.assertEquals(1, role.getRoleId());
	}
}
