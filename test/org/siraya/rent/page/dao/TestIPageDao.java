package org.siraya.rent.page.dao;

import org.junit.Test;
import org.siraya.rent.pojo.Space;
import org.siraya.rent.page.dao.ISpaceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import java.util.List;

import junit.framework.Assert;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})

public class TestIPageDao extends AbstractJUnit4SpringContextTests{
    @Autowired
    private ISpaceDao spaceDao; 
    @Test   
    public void testCRUD()throws Exception{
    	List<Space> ret = spaceDao.getByPageName("index");
    	Assert.assertTrue(ret.size() > 0);
    }
}
