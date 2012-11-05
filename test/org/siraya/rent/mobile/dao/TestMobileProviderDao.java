package org.siraya.rent.mobile.dao;

import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.pojo.MobileProvider;
import org.siraya.rent.mobile.dao.IMobileProviderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})

public class TestMobileProviderDao extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IMobileProviderDao mobileProviderDao; 
    private MobileProvider mobileProvider;
	@Before
    public void setUp(){
		long time=java.util.Calendar.getInstance().getTimeInMillis();

		mobileProvider = new MobileProvider();
		mobileProvider.setCreated(time/1000);
		mobileProvider.setModified(time/1000);
		mobileProvider.setType("SENTLY");
		mobileProvider.setId("u"+time);
		mobileProvider.setUser("xxx");
		mobileProvider.setPassword("password");

	}
	
	
    @Test   
    public void testCRUD()throws Exception{
    	mobileProviderDao.newProvider(mobileProvider);
    }
}	
