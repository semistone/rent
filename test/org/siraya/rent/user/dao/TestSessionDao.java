package org.siraya.rent.user.dao;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.Session;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})

public class TestSessionDao  extends AbstractJUnit4SpringContextTests{
    @Autowired
    private ISessionDao sessionDao;
    private Session session;
	@Before
    public void setUp(){
    	long time=java.util.Calendar.getInstance().getTimeInMillis();
    	session = new Session();
    	session.setId("s"+time);
    	session.setDeviceId("d"+time);
    	session.setUserId("u"+time);
    	session.setLastLoginIp("192.168.0.1");
    	session.setCreated(time/1000);
	}
	
    @Test   
	public void testCRUD() {
		sessionDao.newSession(session);

		List<Session> list = sessionDao.getSessions(session.getUserId(), 10, 0);
		Assert.assertNotSame(0, list.size());
		
		Session session2=sessionDao.getSession(session.getId());
		Assert.assertEquals(session.getDeviceId(), session2.getDeviceId());
    }
}
