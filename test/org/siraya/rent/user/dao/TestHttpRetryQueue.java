package org.siraya.rent.user.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.HttpRetryQueue;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})

public class TestHttpRetryQueue extends AbstractJUnit4SpringContextTests {
    @Autowired
    private IHttpRetryQueueDao httpRetryQueueDao; 
    private HttpRetryQueue queue;
	@Before
    public void setUp(){
		long time=java.util.Calendar.getInstance().getTimeInMillis();

    	queue = new HttpRetryQueue();
    	queue.setCreated(time/1000);
    	queue.setModified(time/1000);
    	queue.setStatus(0);
    	queue.setId("u"+time);
    	queue.setUrl("http://tw.yahoo.com");
	}
    
    @Test   
    public void testCRUD()throws Exception{
    	this.httpRetryQueueDao.newEntity(queue);
    }
}
