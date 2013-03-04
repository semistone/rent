package org.siraya.rent.user.service;
import org.siraya.rent.utils.ApplicationConfig;
import java.util.Calendar;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import org.siraya.rent.pojo.*;
import org.siraya.rent.user.dao.IDeviceDao;
public class TestApiService {
	private ApiService test = new ApiService();
	private Session session = new Session();
	private String authData ;
	private long timestamp = Calendar.getInstance().getTime().getTime()/1000;
	private List<Integer> roles = null;
	private Mockery context;
	private IDeviceDao deviceDao;
	private ISessionService sessionService;
	private Device device = new Device();
	@Before
	public void setUp() throws Exception{
		session.setDeviceId("device");
		session.setUserId("user1");
		session.setId("id1");
		context = new JUnit4Mockery();
		deviceDao = context.mock(IDeviceDao.class);
		test.setDeviceDao(deviceDao);
		device.setId(session.getDeviceId());
		device.setToken("123");
		device.setUserId(session.getUserId());
		sessionService= context.mock(ISessionService.class);
		test.setSessionService(sessionService);
		test.setApplicationConfig(new ApplicationConfig());
	}
	
	@Test
	public void testRequestSession() {
		authData = ApiService.genAuthData(device.getToken(), timestamp);
		context.checking(new Expectations() {
			{
				one(deviceDao).getAppDeviceByDeviceId(session.getDeviceId());
				will(returnValue(device));
				one(sessionService).getSessions(device, 1, 0);
				ArrayList<Session> lists = new ArrayList<Session>();
				will(returnValue(lists));
				one(sessionService).newApiSession(with(any(Session.class)), with(any(List.class)));
			}
		});
		
		test.requestSession(session, authData, timestamp, roles);
	}
	
	
	@Test
	public void testUpdateSession(){
		authData = ApiService.genAuthData(device.getToken(), timestamp);
		context.checking(new Expectations() {
			{
				one(deviceDao).getAppDeviceByDeviceId(session.getDeviceId());
				will(returnValue(device));
				one(sessionService).getSessions(device, 1, 0);
				ArrayList<Session> lists = new ArrayList<Session>();
				lists.add(session);
				will(returnValue(lists));
				one(sessionService).updateApiSession(session);


			}
		});
		test.updateSession(session, authData, timestamp);
	}
	
	@Test
	public void testGenAuthData(){
		String data=ApiService.genAuthData("648987", 1333333333);
		System.out.println(data);
	}
}
