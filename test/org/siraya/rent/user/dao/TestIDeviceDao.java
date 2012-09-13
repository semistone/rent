package org.siraya.rent.user.dao;

import org.junit.Test;
import org.siraya.rent.pojo.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.user.dao.IDeviceDao;
@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"})
public class TestIDeviceDao  extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IDeviceDao deviceDao;
    
    @Test   
    public void testCRUD()throws Exception{
    	Device device=new Device();
    	long time=java.util.Calendar.getInstance().getTimeInMillis();
    	String loginId="d"+time;
    	device.setId("id"+time);
    	device.setUserId("i"+time);
    	device.setCreated(time/1000);
    	device.setStatus(0);
    	device.setToken("1234");
    	device.setModified(time/1000);
    	deviceDao.newDevice(device);
    }
}
