package org.siraya.rent.user.service;
import java.util.List;
import org.siraya.rent.pojo.Role;
import org.siraya.rent.user.dao.IRoleDao;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.siraya.rent.user.dao.ISessionDao;
import org.siraya.rent.user.dao.IDeviceDao;
import org.junit.Test;
import org.siraya.rent.filter.UserRole;
import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.siraya.rent.pojo.Session;
import org.siraya.rent.pojo.User;
public class TestSessionService {
	SessionService sessionService;
	private boolean isMock = true;
	private Mockery context;
	private Session session;
	private ISessionDao sessionDao; 
	private IRoleDao roleDao;
	private IDeviceDao deviceDao; 
	private List<Role> roles;
	long time = java.util.Calendar.getInstance().getTimeInMillis();
	@Before
	public void setUp() {
		if (this.isMock){
			sessionService=new SessionService();	
			context = new JUnit4Mockery();
			this.sessionDao = context.mock(ISessionDao.class);
			this.roleDao = context.mock(IRoleDao.class);
			this.deviceDao = context.mock(IDeviceDao.class);
			this.sessionService.setDeviceDao(deviceDao);
			this.sessionService.setSessionDao(sessionDao);
			this.sessionService.setRoleDao(roleDao);
		}
		this.roles = new java.util.ArrayList<Role>();
		roles.add(new Role("x",UserRole.UserRoleId.ADMIN));
		roles.add(new Role("x",UserRole.UserRoleId.ROOT));
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
					one(roleDao).getRoleByUserId(session.getUserId());
					will(returnValue(roles));
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
