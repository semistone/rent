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
import org.siraya.rent.pojo.Device;
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
	private Device device;
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
			device = new Device();
			device.setId("testid ");
			device.setUserId("userid");

			this.roles = new java.util.ArrayList<Role>();
			roles.add(new Role("x", UserRole.UserRoleId.ADMIN));
			roles.add(new Role("x", UserRole.UserRoleId.ROOT));
			session = new Session();
			session.setDeviceId(device.getId());
			session.setUserId(device.getUserId());
			session.genId();
		}
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
					one(deviceDao).getDeviceByDeviceIdAndUserId(device.getId(), device.getUserId());
					will(returnValue(device));

				}
			});
		}
		sessionService.newSession(session);
	}
	
	@Test
	public void testGetRoles(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(roleDao).getRoleByUserId(session.getUserId());
					will(returnValue(roles));
				}
			});
		}
		sessionService.getRoles(session.getUserId());
	}
}
