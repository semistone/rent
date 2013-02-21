package org.siraya.rent.repl.service;

import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.repl.dao.QueueDao;
import org.siraya.rent.repl.service.example.LogReader1;
import org.siraya.rent.utils.ApplicationConfig;
import org.siraya.rent.utils.IApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogReaderService implements Runnable{
	private LocalQueueService localQueueService;
    private IApplicationConfig applicationConfig;
	private LogReaderService logReaderService; 
	private String queue;
	private static Logger logger = LoggerFactory.getLogger(TestLogReaderService.class);
	@Before
    public void setUp() throws Exception{
		logReaderService = new LogReaderService();

		queue= "test";
		localQueueService = new LocalQueueService();
		QueueDao queueDao = new QueueDao();
		applicationConfig = new ApplicationConfig();
		queueDao.setApplicationConfig(applicationConfig);
		localQueueService.setApplicationConfig(applicationConfig);
		localQueueService.setQueueDao(queueDao);
		localQueueService.setBeanName(queue);
		localQueueService.afterPropertiesSet();
		
		logReaderService.setBeanName("reader1");
		logReaderService.setLocalQueueService(localQueueService);
		
		logReaderService.setLogReader(new org.siraya.rent.repl.service.example.LogReader1());
	}
	public void run(){
		try {
			Thread.currentThread().sleep(2000);
			logger.info("stop reader");
			logReaderService.setShutdown(true);
			logReaderService.newMessageEvent();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test 
	public void testRun() throws Exception{		
		new Thread(this).start();
		logReaderService.run();
	}
	

}
