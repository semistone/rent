package org.siraya.rent.repl.service;

import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.pojo.*;
import org.siraya.rent.repl.dao.QueueDao;
import org.siraya.rent.utils.ApplicationConfig;
import org.siraya.rent.utils.IApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
public class TestLocalQueueService {
	private LocalQueueService localQueueService;
	private String queue;
    private IApplicationConfig applicationConfig;
	private static Logger logger = LoggerFactory
			.getLogger(TestLocalQueueService.class);
    @Before
	public void setUp() throws Exception{
		queue= "test";
		localQueueService = new LocalQueueService();
		QueueDao queueDao = new QueueDao();
		applicationConfig = new ApplicationConfig();
		queueDao.setApplicationConfig(applicationConfig);
		localQueueService.setApplicationConfig(applicationConfig);
		localQueueService.setQueueDao(queueDao);
		localQueueService.init(queue);
	}
	
    @Test   
	public void testInsert() throws Exception{
		Message message=new Message();	
		message.setData("test ".getBytes());
		message.setCmd("cmd");
		message.setUserId("user1");
		localQueueService.insert(message);
		
	}
    
    @Test   
    public void testDump() throws Exception{
    	List<Message> msgs =localQueueService.dump(-1);
    	for (Message msg : msgs) {
    		logger.info(msg.toString());
    	}
    }
    
    @Test 
    public void testGetMeta() throws Exception{
    	QueueMeta meta = localQueueService.getMeta();
    	logger.info("meta is "+meta.toString());
    }
}
