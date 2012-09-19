package org.siraya.rent.user.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.VerifyEvent;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})

public class TestVerifyEvent  extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IVerifyEventDao verifyEventDao;

    private VerifyEvent verifyEvent = new VerifyEvent();

    @Before
    public void setUp(){
    	long time=java.util.Calendar.getInstance().getTimeInMillis();
    	verifyEvent.setModified(time);
    	verifyEvent.setCreated(time);
    	verifyEvent.setStatus(0);
    	verifyEvent.setVerifyDetail("xxx@gmail.com");
    	verifyEvent.setUserId("u"+time);
    }
    
    @Test   
    public void testCRUD()throws Exception{
    	verifyEventDao.newVerifyEvent(verifyEvent);
    	
    }
}
