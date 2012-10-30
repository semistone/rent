package org.siraya.rent.donttry.dao;

import junit.framework.Assert;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.donttry.dao.IDontTryDao;
import org.siraya.rent.pojo.DontTry;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})
public class TestIDontTryDao  extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IDontTryDao test;
    @Test   
    public void testCRUD()throws Exception{
    	DontTry dontTry = new DontTry();
    	long time=java.util.Calendar.getInstance().getTimeInMillis();
    	dontTry.setName("t1349662269");
        DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        Date date = (Date)formatter.parse("01/29/02");
        dontTry.setStartDate(date);
    	int ret = test.newCounter(dontTry);
    	//System.out.println("ret is "+ret);
 
    	// test select
    	DontTry dontTry2 = test.getByName(dontTry.getName(), date);
    	//System.out.println("2 is "+dontTry2);
    	Assert.assertEquals(dontTry.getName(), dontTry2.getName());
    	//System.out.println("count is "+dontTry2.getCount());
    }
}
