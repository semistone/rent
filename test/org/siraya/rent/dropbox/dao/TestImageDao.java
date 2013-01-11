package org.siraya.rent.dropbox.dao;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.dropbox.dao.ImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.Image;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})

public class TestImageDao extends AbstractJUnit4SpringContextTests{
    @Autowired
    private ImageDao test;
    
    private Image image;
    @Before
    public void setUp(){
    	image = new Image();
    	long time=java.util.Calendar.getInstance().getTimeInMillis();
    	image.setId("i"+time);
    	image.setCreated(time/1000);
    	image.setImgTarget("test/test1");
    	image.setShareUrl("http://hahaha.com/test.img");
    	image.setModified(time/1000);
    }
    
    @Test   
    public void testCRUD()throws Exception{
    	int ret = test.insert(image);
    	Assert.assertEquals(1, ret);
    	
    	ret = test.delete(image.getId());
    	Assert.assertEquals(1, ret);
    }
}
