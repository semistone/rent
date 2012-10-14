package org.siraya.rent.user.dao;

import junit.framework.Assert;
import java.util.List;
import org.siraya.rent.pojo.Session;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.user.dao.IUserDAO;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})
public class TestIDeviceDao  extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IDeviceDao deviceDao;
    @Autowired
    private IUserDAO userDao;  
    Device device;
    Session session;
    User user;
    long time;
    
	@Before
    public void setUp(){
		device = new Device();
		user = new User();
		time=java.util.Calendar.getInstance().getTimeInMillis();
    	String userId = "i"+time;
    	String id="id"+time;
    	device.setId(id);
    	device.setUserId(userId);

    	device.setCreated(time/1000);
    	device.setStatus(0);
    	device.setToken("1234");
    	device.setModified(time/1000);
    	user.setId(device.getUserId());
    	user.setMobilePhone("m"+time/1000);
    	user.setCreated(time/1000);
    	user.setModified(time/1000);
    	user.setCc("233");
    	user.setStatus(0);
    	this.userDao.newUser(user);
    	session = new Session();
    	session.setDeviceId(id);
    	session.setUserId(userId);
    	session.setLastLoginIp("127.0.0.1");
    }
    
    @Test   
    public void testCRUD()throws Exception{

    	deviceDao.newDevice(device);
        int ret=deviceDao.updateStatusAndRetryCount(device.getId(),device.getUserId(), 1, 0, time/1000);
        Assert.assertEquals(1, ret);
        //
        // check get device by user id and authing
        //
        Device device3 = deviceDao.getDeviceByUserIdAndStatusAuthing(device.getUserId());
        Assert.assertEquals(device.getCreated(), device3.getCreated());
        //
        // 
        //
        ret = deviceDao.getDeviceCountByUserId(device.getUserId());
        Assert.assertEquals(1, ret);
        ret = deviceDao.getDeviceCountByDeviceId(device.getId());
        Assert.assertEquals(1, ret);
    	Device device2=deviceDao.getDeviceByDeviceIdAndUserId(device.getId(),device.getUserId());
        Assert.assertEquals(device.getCreated(), device2.getCreated());
        Assert.assertEquals(1, device2.getAuthRetry());
        //
        //
        List<Device> list = this.deviceDao.getUserDevices(device.getUserId(), 10, 0);
        Assert.assertEquals(1, list.size());
    

        List<User> list2  = this.deviceDao.getDeviceUsers(device.getId(),10,0);
        Assert.assertEquals(1, list2.size());

        ret = this.deviceDao.updateLastLoginIp(session);
        Assert.assertEquals(1, ret);

    }
}
