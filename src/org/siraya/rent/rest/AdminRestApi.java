package org.siraya.rent.rest;
import java.util.List;
import java.util.Map;
import java.net.HttpURLConnection;
import java.util.HashMap;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.pojo.MobileAuthRequest;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.user.service.IUserService;
import org.siraya.rent.utils.EncodeUtility;
import org.siraya.rent.utils.IApplicationConfig;
import javax.ws.rs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.pojo.User;
import org.siraya.rent.utils.RentException;
import javax.annotation.security.RolesAllowed;
@Component("adminRestApi")
@Path("/admin")
@RolesAllowed({org.siraya.rent.filter.UserRole.ADMIN})
public class AdminRestApi {
    @Autowired
    private IApplicationConfig applicationConfig;
    private static Logger logger = LoggerFactory.getLogger(AdminRestApi.class);
	@Autowired
	private UserAuthorizeData userAuthorizeData;
	@Autowired
	private IUserService userService;
	@Autowired
    private EncodeUtility encodeUtility;
    private static Map<String,String> OK;
    public AdminRestApi(){
    	logger.debug("new admin rest api");
    	if (OK == null) {
    		OK = new HashMap<String,String>();
<<<<<<< HEAD
    		OK.put("status", "SUCCESS");
=======
>>>>>>> master
    	}
    }

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/sms_gateway_debug_mode/{mode}")
    public Response smsGatewayDebugMode(@PathParam("mode") boolean mode) {
    	logger.debug("set debug flag "+mode);
    	Map<String,Object> sently = this.applicationConfig.get("sently");
    	sently.put("debug", mode);
    	HashMap<String,Boolean> response = new HashMap<String,Boolean>();
		response.put("mode", new Boolean(mode));
    	return Response.status(HttpURLConnection.HTTP_OK).entity(response).build();
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/show_token")
	public Response showToken(){
		String deviceId = userAuthorizeData.getDeviceId();
		String userId = userAuthorizeData.getUserId();
		if (userId == null) {
			throw new RentException(RentException.RentErrorCode.ErrorInvalidParameter,"user id is null");
		}
		if (!applicationConfig.get("general").get("debug").equals(Boolean.TRUE)) {
			throw new RentException(RentException.RentErrorCode.ErrorPermissionDeny,"only support in debug mode");
		}
		Device device = new Device();
		device.setId(deviceId);
		device.setUserId(userId);
		device = userService.getDevice(device);
		
		HashMap<String,String> response = new HashMap<String,String>();
		response.put("token", encodeUtility.decrypt(device.getToken(),Device.ENCRYPT_KEY));
		return Response.status(HttpURLConnection.HTTP_OK).entity(response).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/show_mobile_auth_request/{requestId}")
	public MobileAuthRequest getMobileAuthRequest(@PathParam("requestId")String requestId){
		return this.userService.getMobileAuthRequest(requestId);	
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list_users")
	public List<User> listUsers(@DefaultValue("20") @QueryParam("limit") int limit ,
			@DefaultValue("0") @QueryParam("offset")int offset){
		logger.debug("get devices list limit:"+limit+" offset"+offset);
		return this.userService.getDeviceUsers(this.userAuthorizeData.getDeviceId(), limit, offset);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list_sso_devices")
	public List<Device> getSsoDevices(){
		return this.userService.getSsoDevices();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get_signature_of_mobile_auth_request")
	public Response getSignatureOfMobileAuthRequest(MobileAuthRequest request){
		String sign = this.userService.getSignatureOfMobileAuthRequest(request);
    	HashMap<String,String> response = new HashMap<String,String>();
		response.put("sign", sign);
		return Response.status(HttpURLConnection.HTTP_OK).entity(response).build();
	}
	
}
