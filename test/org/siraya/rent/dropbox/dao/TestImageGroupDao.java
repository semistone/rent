package org.siraya.rent.dropbox.dao;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.pojo.ImageGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})

public class TestImageGroupDao extends AbstractJUnit4SpringContextTests{
    @Autowired
    private ImageGroupDao test;
    private ImageGroup imageGroup;
    
    @Before
    public void setUp(){
    	
    	imageGroup = new ImageGroup();
    	imageGroup.setUserId("user");
    	imageGroup.setPath("path1");
    	imageGroup.setId(ImageGroup.genId());
    	
    }
    
    @Test  
    public void testCrud(){
    	int ret = test.insert(imageGroup);
    	Assert.assertEquals(1, ret);
    }
}
