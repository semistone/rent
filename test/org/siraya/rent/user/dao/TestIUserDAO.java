package org.siraya.rent.user.dao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.pojo.User;

@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})
public class TestIUserDAO extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IUserDAO userDao;
    private String loginType = "FB";
	@Before
    public void setUp(){
    	
    }
    
    @Test   
    public void testCRUD(){
    	User user=new User();
    	long time=java.util.Calendar.getInstance().getTimeInMillis();
    	String loginId="loginid"+time;
    	String mobilePhone=Long.toString(time/1000);
    	user.setId("id"+time);
    	user.setMobilePhone(mobilePhone);
    	user.setCreated(time/1000);
    	user.setStatus(0);
    	user.setCc("TW");
    	user.setName("name");
    	user.setLang("zh");
    	user.setModified(time/1000);
    	userDao.newUser(user);
    	
    	//
    	//
    	//
    	String email = "xxx@gmail.com";
    	user.setEmail(email);
    	user.setLoginId(loginId);
    	user.setLoginType(loginType);
    	int ret = userDao.initLoginIdAndType(user);
    	Assert.assertEquals(1, ret);
    	userDao.updateUserEmail(user);
    	User user2 = userDao.getUserByLoginIdAndLoginType(loginId, loginType);
    	User user3 = userDao.getUserByMobilePhone(mobilePhone);
    	Assert.assertEquals(user.getId(),user2.getId());
    	Assert.assertEquals(email,user2.getEmail());
    	Assert.assertEquals(email,user3.getEmail());

    }
    
}

