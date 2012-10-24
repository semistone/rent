package org.siraya.rent.rest;
import org.siraya.rent.pojo.Session;
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
import org.siraya.rent.pojo.MobileAuthRequest;
import org.siraya.rent.pojo.MobileAuthResponse;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.pojo.User;
import org.siraya.rent.utils.RentException;
import org.siraya.rent.utils.RentException.RentErrorCode;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.user.service.DeviceStatus;
import org.siraya.rent.user.service.IMobileAuthService;
import org.siraya.rent.user.service.IUserService;
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
@Component("userRestApi")
@Path("/user")
public class UserRestApi {
	@Autowired
	private IUserService userService;
	@Autowired
	private IMobileAuthService mobileAuthService;
	@Autowired
	private CookieUtils cookieUtils;

	@Autowired
	private UserAuthorizeData userAuthorizeData;

	private Validator validator;
	private static Logger logger = LoggerFactory.getLogger(UserRestApi.class);
    private static Map<String,String> OK;
    public UserRestApi (){
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
	public Response post(Map<String,Object> request) throws Exception{
		logger.debug("call new device");
		Response response =  this.newDevice(request);
		if (response.getStatus() == HttpURLConnection.HTTP_OK) {
			//
			// send mobile auth message
			//
			Response response2 = this.sendMobileAuthMessage();
			logger.debug("rebuild response");
			response = Response.fromResponse(response).status(response2.getStatus()).build();
		}
		return response;
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@DefaultValue("") @QueryParam("deviceId") String deviceId) throws Exception{
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
	public Response get() throws Exception{
		logger.debug("call get device");

		String deviceId = this.userAuthorizeData.getDeviceId();
		String userId = this.userAuthorizeData.getUserId();
		if (userId == null || userId.equals("anonymous")) {	
			throw new RentException(RentErrorCode.ErrorNotFound, "device id or user id is null");
		}
		Device device = new Device(deviceId,userId);
		//
		// if device authed add role.
		//
		Session session = this.userAuthorizeData.getSession();
		if (device.getStatus() == DeviceStatus.Authed.getStatus()) {
			session.setDeviceVerified(true);
		}
		device = userService.getDevice(device);
		return Response.status(HttpURLConnection.HTTP_OK).entity(device).build();
	}
	/**
     * create new device and assign a device id for it.
     * @param cc
     * @param mobilePhone
     * @return
     */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/new_device")
	public Response newDevice(Map<String,Object> request) throws Exception{
		String deviceId = this.userAuthorizeData.getDeviceId();
		try {
			String cc=(String)request.get("countryCode");
			String mobilePhone=(String)request.get("mobilePhone");

			User user =userService.newUserByMobileNumber(Integer.parseInt(cc), mobilePhone);
			Device device = new Device();
			device.setId(deviceId);
			device.setUser(user);
			device = userService.newDevice(device);
			//
			// set user authroized data for send auth message next .
			//
			this.userAuthorizeData.setDeviceId(device.getId());
			this.userAuthorizeData.setUserId(device.getUserId());

			//
			// set device id into cookie
			//

			NewCookie deviceCookie = cookieUtils.createDeviceCookie(device);
			
			return Response.status(HttpURLConnection.HTTP_OK).entity(device).cookie(deviceCookie)
					.build();
		}catch(java.lang.NumberFormatException e){
			throw new RentException(RentErrorCode.ErrorInvalidParameter, "country code or mobile number must be number");
		}
	}


	

	/**
	 * send auth message
	 * @param deviceId
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/send_mobile_auth_message")
	public Response sendMobileAuthMessage() throws Exception{
		String deviceId = this.userAuthorizeData.getDeviceId();
		String userId = this.userAuthorizeData.getUserId();
		mobileAuthService.sendAuthMessage(deviceId,userId);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();

	}
	
	/**
	 * verify auth code
	 * @param deviceId
	 * @param authCode
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/verify_mobile_auth_code")
	public Response verifyMobileAuthCode(Map<String,Object> request) throws Exception{
		String deviceId = this.userAuthorizeData.getDeviceId();
		String userId = this.userAuthorizeData.getUserId();
		String authCode = (String)request.get("authCode");
		mobileAuthService.verifyAuthCode(deviceId, userId, authCode);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}

	/**
	 * verify auth code
	 * @param deviceId
	 * @param authCode
	 * @return
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/verify_mobile_auth_request_code")
	public MobileAuthResponse verifyMobileAuthRequestCode(MobileAuthRequest request){
		Device device = new Device(this.userAuthorizeData.getDeviceId(), this.userAuthorizeData.getUserId());
		request.setDevice(device);
		MobileAuthResponse response = this.mobileAuthService.verifyMobileAuthRequestCode(request);
		return response;
	}
	

	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/name_device")
	public Response nameDevice(Map<String,Object> request){
		String name = (String)request.get("name");
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
	public Response mobileAuthRequest(MobileAuthRequest request){	
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
		if (request.isForceReauth() || response.getStatus() == DeviceStatus.Init.getStatus()) {
			this.mobileAuthService.sendAuthMessage(request,response);
		}
		return Response.status(HttpURLConnection.HTTP_OK).entity(response)
				.build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list_devices")
	public List<Device> deviceList(@DefaultValue("20") @QueryParam("limit") int limit ,
			@DefaultValue("0") @QueryParam("offset")int offset){
		logger.debug("get devices list limit:"+limit+" offset"+offset);
		return this.userService.getUserDevices(this.userAuthorizeData.getUserId(), limit, offset);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/show_mobile_auth_request/{requestId}")
	public MobileAuthRequest getMobileAuthRequest(@PathParam("requestId")String requestId){		
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
	@Path("/sign_off")
	public void signOff() {
		this.userAuthorizeData.signOff();
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
}
