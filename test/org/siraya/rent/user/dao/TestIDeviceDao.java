package org.siraya.rent.user.dao;

import junit.framework.Assert;

import org.junit.Test;
import org.siraya.rent.pojo.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.user.dao.IDeviceDao;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})
public class TestIDeviceDao  extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IDeviceDao deviceDao;
    
    @Test   
    public void testCRUD()throws Exception{
    	Device device=new Device();
    	long time=java.util.Calendar.getInstance().getTimeInMillis();
    	String userId = "i"+time;
    	String loginId="d"+time;
    	device.setId("id"+time);
    	device.setUserId(userId);

    	device.setCreated(time/1000);
    	device.setStatus(0);
    	device.setToken("1234");
    	device.setModified(time/1000);
    	deviceDao.newDevice(device);
        int ret=deviceDao.updateStatusAndRetryCount(device.getId(),device.getUserId(), 1, 0, time/1000);
        Assert.assertEquals(1, ret);
        //
        // check get device by user id and authing
        //
        Device device3 = deviceDao.getDeviceByUserIdAndStatusAuthing(userId);
        Assert.assertEquals(device.getCreated(), device3.getCreated());
        //
        // 
        //
        ret = deviceDao.getDeviceCountByUserId(userId);
        Assert.assertEquals(1, ret);
        
    	Device device2=deviceDao.getDeviceByDeviceIdAndUserId(device.getId(),device.getUserId());
        Assert.assertEquals(device.getCreated(), device2.getCreated());
        Assert.assertEquals(1, device2.getAuthRetry());
    }
}
