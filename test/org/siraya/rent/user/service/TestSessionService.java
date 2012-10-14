package org.siraya.rent.user.service;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.siraya.rent.user.dao.ISessionDao;
import org.siraya.rent.user.dao.IDeviceDao;
import org.junit.Test;
import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.siraya.rent.pojo.Session;
import org.siraya.rent.pojo.User;
public class TestSessionService {
	SessionService sessionService;
	private boolean isMock = true;
	private Mockery context;
	private Session session;
	private ISessionDao sessionDao; 
	private IDeviceDao deviceDao; 
	long time = java.util.Calendar.getInstance().getTimeInMillis();
	@Before
	public void setUp() {
		if (this.isMock){
			sessionService=new SessionService();	
			context = new JUnit4Mockery();
			this.sessionDao = context.mock(ISessionDao.class);	
			this.deviceDao = context.mock(IDeviceDao.class);
			this.sessionService.setDeviceDao(deviceDao);
			this.sessionService.setSessionDao(sessionDao);
		}
		
		session = new Session();
		session.setDeviceId("d"+time);
		session.setUserId("u"+time);
		session.genId();
	}
	
	@Test
	public void testNewSession(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(sessionDao).newSession(session);
					one(deviceDao).updateLastLoginIp(session);
					will(returnValue(1));
				}
			});
		}
		sessionService.newSession(session);
	}
	
	@Test(expected=org.siraya.rent.utils.RentException.class)
	public void testNewSessionButUpdateFail(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(sessionDao).newSession(session);
					one(deviceDao).updateLastLoginIp(session);
					will(returnValue(0));
				}
			});
		}
		sessionService.newSession(session);
	}
}
