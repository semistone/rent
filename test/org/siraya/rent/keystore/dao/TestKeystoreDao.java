package org.siraya.rent.keystore.dao;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.siraya.rent.keystore.dao.IKeystoreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-keystore.xml"})
public class TestKeystoreDao extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IKeystoreDao test;
    private String key = "key";
    @Before
    public void setUp(){
    	test.create();
    }
    
    @After
    public void teamDown(){
    	test.delete(key);
    }
    
    @Test 
    public void testCrud(){
    	String value="value";
    	test.insert(key, value);
    	Assert.assertEquals(value, test.get(key));
    }
    
}
