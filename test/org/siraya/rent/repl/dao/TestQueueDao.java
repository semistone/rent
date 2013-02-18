package org.siraya.rent.repl.dao;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.pojo.*;
import org.siraya.rent.repl.dao.*;
import java.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.siraya.rent.utils.*;
import java.sql.DriverManager;
import java.util.List;

import junit.framework.Assert;
public class TestQueueDao {
    @Autowired
    private QueueDao queueDao; 
    private Message message;
    private Connection connection;
    private IApplicationConfig applicationConfig;
	@Before
    public void setUp() throws Exception{
		queueDao = new QueueDao();
		applicationConfig = new ApplicationConfig();
		queueDao.setApplicationConfig(applicationConfig);
		message = new Message();
    	
		Class.forName("org.sqlite.JDBC");
		connection = DriverManager.getConnection("jdbc:sqlite:");
    	queueDao.initMeta(connection);
		queueDao.initVolumnFile(connection);
		
    }
	
    @Test    
	public void testInitQueue() throws Exception{
    	queueDao.initQueue("test");
	}
    
    @Test 	
    public void testGetMeta()throws Exception{
    	QueueMeta ret =queueDao.getMeta(connection);
    	Assert.assertEquals(0, ret.getVolumn());
    }
    
    @Test 
    public void testInitVolumnFile() throws Exception{
    	String queue = "test";
    	queueDao.initQueue(queue);
    	queueDao.initVolumnFile(queue, 0);
    }
    
    @Test 
    public void testInsert()throws Exception{
    	QueueMeta meta = new QueueMeta();
    	Message message = new Message();
    	message.setCmd("cmd1");
    	message.setData("test".getBytes());
    	queueDao.insert(connection, connection, meta, message);
    }
    
    @Test 
    public void testReset() throws Exception{
    	QueueMeta meta = new QueueMeta();
    	queueDao.resetVolumn(connection, meta);
    }

}
