package org.siraya.rent.user.dao;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.pojo.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.Role;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})

public class TestRoleDao extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IRoleDao roleDao;
    private Role role;
    long time=java.util.Calendar.getInstance().getTimeInMillis();
	@Before
    public void setUp(){
    	role = new Role();
    	role.setUserId("u"+time);
    	role.setRoleId(2);
	}
	
    @Test
	public void testCRUD(){
    	roleDao.newRole(role);
    	List<Role> roles = roleDao.getRoleByUserId(role.getUserId());
    	int size = roles.size();
    	Assert.assertEquals(1, size);
    	
    }
}
