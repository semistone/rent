package org.siraya.rent.user.dao;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.MobileAuthRequest;
import org.siraya.rent.pojo.MobileAuthResponse;

@ContextConfiguration(locations = { "classpath*:/applicationContext.xml",
		"classpath*:/applicationContext-mybatis.xml" })
public class TestMobileAuthRequestDao extends AbstractJUnit4SpringContextTests {
	@Autowired
	private IMobileAuthRequestDao mobileAuthRequestDao;
	@Autowired
	private IMobileAuthResponseDao mobileAuthResponseDao;
	private MobileAuthRequest request;
	private MobileAuthResponse response;

	@Before
	public void setUp() {
		long time=java.util.Calendar.getInstance().getTimeInMillis();
		String requestId = "r"+time/1000;
		request = new MobileAuthRequest();
		request.setRequestTime(time/1000);
		request.setForceReauth(true);
		request.setRequestFrom("xxx");
		request.setDone("http://www.yahoo.com");
		request.setAuthUserId("auth id ");
		request.setRequestId(requestId);
		response = new MobileAuthResponse();
		response.setRequestId(requestId);
		response.setStatus(1);
		response.setResponseTime(time/1000);
	}

	@Test
	public void testCRUD() {
		mobileAuthRequestDao.newRequest(request);
		mobileAuthResponseDao.updateResponse(response);
	}
}
