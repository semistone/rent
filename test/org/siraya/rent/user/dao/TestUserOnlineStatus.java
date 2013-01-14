package org.siraya.rent.user.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.UserOnlineStatus;

@ContextConfiguration(locations = { "classpath*:/applicationContext.xml",
		"classpath*:/applicationContext-mybatis.xml" })
public class TestUserOnlineStatus extends AbstractJUnit4SpringContextTests {
	@Autowired
	private IUserOnlineStatusDao userOnlineStatusDao;

	private UserOnlineStatus userOnlineStatus;

	@Before
	public void setUp() {
		userOnlineStatus = new UserOnlineStatus();
		long time = java.util.Calendar.getInstance().getTimeInMillis();
		userOnlineStatus.setId("x" + time / 1000);
		userOnlineStatus.setOnlineStatus(1);
	}

	@Test
	public void testCRUD() {
		userOnlineStatusDao.insert(userOnlineStatus);
		userOnlineStatusDao.updateOnlineStatus(userOnlineStatus);

	}
}
