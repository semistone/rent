package org.siraya.rent.repl.dao;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.pojo.*;
import org.siraya.rent.repl.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import junit.framework.Assert;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})
public class TestQueueDao extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IQueueDao queueDao; 
    private Message message;

	@Before
    public void setUp(){
    	message = new Message();
    }
    
    @Test
	@Transactional(value = "queueTxManager", readOnly = false)
    public void testCRUD(){
    	queueDao.insert(message);
    }
}
