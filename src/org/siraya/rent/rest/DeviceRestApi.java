package org.siraya.rent.rest;

import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.siraya.rent.pojo.Member;
import org.siraya.rent.pojo.Session;

import javax.annotation.security.RolesAllowed;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.List;
import javax.ws.rs.core.Response;
import org.siraya.rent.pojo.*;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.filter.UserRole;
import org.siraya.rent.filter.UserRole.UserRoleId;
import org.siraya.rent.utils.RentException;
import org.siraya.rent.utils.RentException.RentErrorCode;
import org.siraya.rent.user.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import javax.ws.rs.core.NewCookie;
import java.net.HttpURLConnection;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;

import junit.framework.Assert;

@Component("deviceRestApi")
@Path("/device")
public class DeviceRestApi {
	@Autowired
	private IUserService userService;
	@Autowired
	private IMobileAuthService mobileAuthService;
	@Autowired
	private ISessionService sessionService;

	@Autowired
	private CookieUtils cookieUtils;

	@Autowired
	private UserAuthorizeData userAuthorizeData;
	@Autowired
	private IMobileGatewayService mobileGatewayService;
	private Validator validator;
	private static Logger logger = LoggerFactory.getLogger(DeviceRestApi.class);
	private static Map<String, String> OK;

	public DeviceRestApi() {
    	logger.info("new device rest api");
		if (OK == null) {
			OK = new HashMap<String, String>();
			OK.put("status", "SUCCESS");
		}
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(Map<String, Object> request) throws Exception {
		logger.debug("call new device");
		Response response = this.newDevice(request);
		//
		// if session exist, mean already authed.
		//
		Session session = this.userAuthorizeData.getSession();
		if (session != null && session.isNew()) {
			logger.debug("device have authed");
			return response;
		}
		if (response.getStatus() == HttpURLConnection.HTTP_OK) {
			//
			// send mobile auth message
			//
			Response response2 = this.sendMobileAuthMessage();
			logger.debug("rebuild response");
			response = Response.fromResponse(response)
					.status(response2.getStatus()).build();
		}
		return response;
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response delete(
			@DefaultValue("") @QueryParam("deviceId") String deviceId)
			throws Exception {
		if (deviceId == null || deviceId.equals("")) {
			deviceId = this.userAuthorizeData.getDeviceId();
			logger.debug("delete current device");
		}
		String userId = this.userAuthorizeData.getUserId();
		Device device = new Device();
		device.setUserId(userId);
		device.setId(deviceId);
		userService.removeDevice(device);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Device get() throws Exception {
		logger.debug("call get device");
		String deviceId = this.userAuthorizeData.getDeviceId();
		String userId = this.userAuthorizeData.getUserId();
		if (userId == null || userId.equals("anonymous")) {
			throw new RentException(RentErrorCode.ErrorNotFound,
					"device id or user id is null");
		}
		return this._get(deviceId, userId);
	}

	@GET
	@Path("/get_device/{deviceId}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Device getDeviceById(@PathParam("requestId") String deviceId) {
		logger.debug("call get device");
		String userId = this.userAuthorizeData.getUserId();
		return this._get(deviceId, userId);
	}

	private Device _get(String deviceId, String userId) {
		Device device = new Device(deviceId, userId);
		//
		// if device authed add role.
		//
		device = userService.getDevice(device);
		if (device.getStatus() == DeviceStatus.Authed.getStatus()) {
			Session session = this.userAuthorizeData.getSession();
			session.setDeviceVerified(true);
		}
		return device;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/new_device")
	/**
	 * create new device and assign a device id for it.
	 * @param cc
	 * @param mobilePhone
	 * @return
	 */
	public Response newDevice(Map<String, Object> request) throws Exception {
		String deviceId = this.userAuthorizeData.getDeviceId();
		try {
			String cc = (String) request.get("countryCode");
			String mobilePhone = (String) request.get("mobilePhone");

			User user = userService.newUserByMobileNumber(Integer.parseInt(cc),
					mobilePhone);
			Device device = new Device(deviceId, user.getId());
			device.setUser(user);
			device = userService.newDevice(device);
			//
			// set user authroized data for send auth message next .
			//
			this.userAuthorizeData.setDeviceId(device.getId());
			this.userAuthorizeData.setUserId(device.getUserId());
			//
			// if device authed, then new session.
			//
			if (device.getStatus() == DeviceStatus.Authed.getStatus()) {
				logger.debug("device has authed, new session");
				Session session = this.userAuthorizeData.getSession();
				if (session == null) {
					session = new Session();
					this.userAuthorizeData.setSession(session);
				}
				session.setDeviceId(deviceId);
				session.setUserId(device.getUserId());
				session.setDeviceVerified(true);
			}
			//
			// set device id into cookie
			//
			logger.debug("set device cookie");
			NewCookie deviceCookie = cookieUtils.createDeviceCookie(device);

			return Response.status(HttpURLConnection.HTTP_OK).entity(device)
					.cookie(deviceCookie).build();
		} catch (java.lang.NumberFormatException e) {
			throw new RentException(RentErrorCode.ErrorInvalidParameter,
					"country code or mobile number must be number");
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list_sessions")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public List<Session> getSessions(@QueryParam("deviceId") String deviceId,
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) {
		Device device = new Device(deviceId, this.userAuthorizeData.getUserId());
		return this.sessionService.getSessions(device, limit, offset);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/send_mobile_auth_message")
	/**
	 * send auth message
	 * @param deviceId
	 * @return
	 */
	public Response sendMobileAuthMessage() throws Exception {
		String deviceId = this.userAuthorizeData.getDeviceId();
		String userId = this.userAuthorizeData.getUserId();
		mobileAuthService.sendAuthMessage(deviceId, userId);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();

	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/verify_mobile_auth_code")
	/**
	 * verify auth code
	 * @param deviceId
	 * @param authCode
	 * @return
	 */
	public Response verifyMobileAuthCode(Map<String, Object> request)
			throws Exception {
		String deviceId = this.userAuthorizeData.getDeviceId();
		String userId = this.userAuthorizeData.getUserId();
		String authCode = (String) request.get("authCode");
		mobileAuthService.verifyAuthCode(deviceId, userId, authCode);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/verify_mobile_auth_request_code")
	/**
	 * verify auth code
	 * @param deviceId
	 * @param authCode
	 * @return
	 */
	public MobileAuthResponse verifyMobileAuthRequestCode(
			MobileAuthRequest request) {
		Device device = new Device(this.userAuthorizeData.getDeviceId(),
				this.userAuthorizeData.getUserId());
		request.setDevice(device);
		MobileAuthResponse response = this.mobileAuthService
				.verifyMobileAuthRequestCode(request);
		return response;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/name_device")
	public Response nameDevice(Map<String, Object> request) {
		String name = (String) request.get("name");
		Device device = new Device();
		device.setId(this.userAuthorizeData.getDeviceId());
		device.setUserId(this.userAuthorizeData.getUserId());
		device.setName(name);
		userService.nameDevice(device);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/mobile_auth_request")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response mobileAuthRequest(MobileAuthRequest request) {
		Set<ConstraintViolation<MobileAuthRequest>> constraintViolations = validator
				.validate(request);
		if (constraintViolations.size() != 0) {
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"validate request object fail");
		}

		Device currentDevice = new Device(this.userAuthorizeData.getDeviceId(),
				this.userAuthorizeData.getUserId());
		request.setDevice(currentDevice);
		MobileAuthResponse response = userService.mobileAuthRequest(request);
		//
		// if force reauth or status is init, then sent sms auth message.
		//
		int status = response.getStatus();
		if (response.getUser() != null
				&& (status == DeviceStatus.Init.getStatus() || status == DeviceStatus.Authing
						.getStatus())) {
			this.mobileAuthService.sendAuthMessage(request, response);
		}
		return Response.status(HttpURLConnection.HTTP_OK).entity(response)
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list_devices")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public List<Device> deviceList(
			@DefaultValue("20") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) {
		logger.debug("get devices list limit:" + limit + " offset" + offset);
		return this.userService.getUserDevices(
				this.userAuthorizeData.getUserId(), limit, offset);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/show_mobile_auth_request/{requestId}")
	public MobileAuthRequest getMobileAuthRequest(
			@PathParam("requestId") String requestId) {
		MobileAuthRequest request = this.userService
				.getMobileAuthRequest(requestId);
		if (!this.userAuthorizeData.getUserId().equals(request.getAuthUserId())) {
			throw new RentException(
					RentException.RentErrorCode.ErrorPermissionDeny,
					"can't not access");
		}
		return request;
	}


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get_roles")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public List<Role> getRoles() {
		String userId = this.userAuthorizeData.getUserId();
		List<Role> roles = this.sessionService.getRoles(userId);
		Session session = this.userAuthorizeData.getSession();
		List<Integer> sessionRoles = session.getRoles();

		for (Role role : roles) {
			int roleId = role.getRoleId();
			if (!sessionRoles.contains(roleId)) {
				logger.debug("add role " + roleId + " into session");
				session.addRole(role.getRoleId());
			}
		}

		return roles;
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/apply_sso_application")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.MOBILE_PROVIDER })
	public Device applySSOApplication() {
		Device device = new Device(UserService.SSO_DEVICE_ID,
				this.userAuthorizeData.getUserId());
		device.setLastLoginIp(this.userAuthorizeData.getSession()
				.getLastLoginIp());
		this.userService.applySSOApplication(device);
		logger.debug("add sso app role");
		this.userAuthorizeData.getSession().addRole(
				UserRoleId.SSO_APP.getRoleId());
		return device;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get_sso_application_token")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.SSO_APP })
	public Map<String, String> getSSOApplication() {
		Device device = new Device(UserService.SSO_DEVICE_ID,
				this.userAuthorizeData.getUserId());
		String token = this.userService.getDevice(device).getToken();
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("token", token);
		return response;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get_signature_of_mobile_auth_request")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response getSignatureOfMobileAuthRequest(MobileAuthRequest request) {
		if (!request.getRequestFrom()
				.equals(this.userAuthorizeData.getUserId())) {
			throw new RentException(
					RentException.RentErrorCode.ErrorPermissionDeny,
					"user not match");
		}
		String sign = this.userService.getSignatureOfMobileAuthRequest(request);
		HashMap<String, String> response = new HashMap<String, String>();
		response.put("sign", sign);
		return Response.status(HttpURLConnection.HTTP_OK).entity(response)
				.build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sign_off")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response signOff() {
		this.userAuthorizeData.signOff();
		return Response.status(HttpURLConnection.HTTP_OK).entity(this.OK)
				.build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/new_mobile_provider")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response newMobileProvider(MobileProvider mobileProvider) {
		mobileProvider.setId(this.userAuthorizeData.getUserId());
		//
		// simple validate
		//
		Set<ConstraintViolation<MobileProvider>> constraintViolations = validator
				.validate(mobileProvider);
		if (constraintViolations.size() != 0) {
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"validate request object fail");
		}
		//
		// new mobile provider
		//
		mobileGatewayService.newProvider(mobileProvider);
		this.userAuthorizeData.getSession().addRole(UserRole.UserRoleId.MOBILE_PROVIDER.getRoleId());
		return Response.status(HttpURLConnection.HTTP_OK)
				.entity(OK).build();
	}

	@GET
	@Path("/connect/{callback}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response connect(@PathParam("callback") String callback) {
		logger.debug("connect");
		Session session = userAuthorizeData.getSession();
		if (session.getCallback() != null) {
			return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
		}
		session.setCallback(callback);		
		sessionService.connect(session);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}

	@GET
	@Path("/disconnect")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response disconnect(@PathParam("id") String id) {
		logger.debug("disconnect");
		Session session = userAuthorizeData.getSession();
		sessionService.disconnect(session);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}

	@GET
	@Path("/callbacks/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public List<String> callbacks(@PathParam("userId") String userId) {
		logger.debug("callbacks");
		return sessionService.callbacks(userId);
	}
	
	@GET
	@Path("/callbacks/reset/{callback}")
	public void callbackReset(@PathParam("callback") String callback){
		sessionService.callbackReset(callback);
	}

	void setUserService(IUserService userService) {
		this.userService = userService;
	}

	void setMobileAuthService(IMobileAuthService mobileAuthService) {
		this.mobileAuthService = mobileAuthService;
	}

	public UserAuthorizeData getUserAuthorizeData() {
		return userAuthorizeData;
	}

	public void setUserAuthorizeData(UserAuthorizeData userAuthorizeData) {
		this.userAuthorizeData = userAuthorizeData;
	}

	public void setCookieUtils(CookieUtils cookieUtils) {
		this.cookieUtils = cookieUtils;
	}

	public ISessionService getSessionService() {
		return sessionService;
	}

	public void setSessionService(ISessionService sessionService) {
		this.sessionService = sessionService;
	}
}
