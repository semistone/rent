package org.siraya.rent.user.service;
import java.util.HashMap;
import org.siraya.rent.user.dao.IUserOnlineStatusDao;
import java.util.List;
import java.util.Map;

import org.siraya.rent.pojo.Role;
import org.siraya.rent.user.dao.IRoleDao;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.siraya.rent.user.dao.ISessionDao;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.utils.IApplicationConfig;
import org.junit.Test;
import org.siraya.rent.filter.UserRole;
import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.siraya.rent.pojo.*;
public class TestSessionService {
	SessionService sessionService;
	private boolean isMock = true;
	private Mockery context;
	private Session session;
	private IApplicationConfig config;
	private ISessionDao sessionDao; 
	private IUserOnlineStatusDao userOnlineStatusDao;
	private IRoleDao roleDao;
	private IDeviceDao deviceDao; 
	private List<Role> roles;
	private Device device;
	private Map<String, Object> setting;
	long time = java.util.Calendar.getInstance().getTimeInMillis();
	@Before
	public void setUp() {
		if (this.isMock){
			sessionService=new SessionService();	
			context = new JUnit4Mockery();
			this.sessionDao = context.mock(ISessionDao.class);
			this.roleDao = context.mock(IRoleDao.class);
			this.deviceDao = context.mock(IDeviceDao.class);
			this.userOnlineStatusDao = context.mock(IUserOnlineStatusDao.class);
			this.sessionService.setDeviceDao(deviceDao);
			this.sessionService.setSessionDao(sessionDao);
			this.sessionService.setRoleDao(roleDao);
			this.sessionService.setUserOnlineStatusDao(userOnlineStatusDao);
			device = new Device();
			device.setId("testid ");
			device.setUserId("userid");
			config = context.mock(IApplicationConfig.class);
			setting = new HashMap<String, Object>();
			setting.put("geoip_data", "/xx.dat");
			this.roles = new java.util.ArrayList<Role>();
			roles.add(new Role("x", UserRole.UserRoleId.ADMIN));
			roles.add(new Role("x", UserRole.UserRoleId.ROOT));
			session = new Session();
			session.setCallback("http://127.0.0.2");
			session.setDeviceId(device.getId());
			session.setUserId(device.getUserId());
			session.genId();
			this.sessionService.setApplicationConfig(config);
		}
	}
	
	@Test
	public void testNewSession(){
		if (isMock) {
			context.checking(new Expectations() {
				{
					one(config).get("general");
					will(returnValue(setting));
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

	@Test
	public void testConnect() {
		context.checking(new Expectations() {
			{
				one(sessionDao).updateOnlineStatus(session);
				one(userOnlineStatusDao).updateOnlineStatus(
						with(any(UserOnlineStatus.class)));
				will(returnValue(0));
				one(userOnlineStatusDao).insert(
						with(any(UserOnlineStatus.class)));

			}
		});
		sessionService.connect(session);

	}

	@Test
	public void testDisconnect() {
		context.checking(new Expectations() {
			{
				one(sessionDao).updateOnlineStatus(session);
				one(sessionDao).getUserOnlineStatusFromSessions(
						session.getUserId());
				will(returnValue(0));
				one(userOnlineStatusDao).updateOnlineStatus(
						with(any(UserOnlineStatus.class)));
				will(returnValue(0));
			}
		});
		sessionService.disconnect(session);

	}
}
