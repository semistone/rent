package org.siraya.rent.rest;
import java.util.Map;
import java.net.HttpURLConnection;
import java.util.HashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.user.service.IUserService;
import org.siraya.rent.utils.EncodeUtility;
import org.siraya.rent.utils.IApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.utils.RentException;
import javax.annotation.security.RolesAllowed;
@Component("adminRestApi")
@Path("/admin")
@RolesAllowed({"admin"})
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
    public AdminRestApi(){
    	logger.debug("new admin rest api");
    }

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/set_sently_debug_flag/{debug_flag}")
    public Response put(@PathParam("debug_flag") boolean flag) {
    	logger.debug("set debug flag "+flag);
    	Map<String,Object> sently = this.applicationConfig.get("sently");
    	sently.put("debug", flag);
    	HashMap<String,Boolean> response = new HashMap<String,Boolean>();
		response.put("flag", new Boolean(flag));
    	return Response.status(HttpURLConnection.HTTP_OK).entity(response).build();
    }
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/show_token")
	public Response show_token(){
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
		response.put("token", encodeUtility.decrypt(device.getToken(),device.ENCRYPT_KEY));
		return Response.status(HttpURLConnection.HTTP_OK).entity(response).build();
	}
}
