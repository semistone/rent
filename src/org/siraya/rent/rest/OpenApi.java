package org.siraya.rent.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

import org.siraya.rent.filter.UserAuthorizeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.user.service.*;
import org.siraya.rent.pojo.*;
@Component("openApi")
@Path("/api")
public class OpenApi {
	@Autowired
	private UserAuthorizeData userAuthorizeData;
	@Autowired
	private IApiService apiService;
	/**
	 * user want to apply api permission from another device.
	 * @param applicationName
	 * @return application id and secure token.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	@Path("/apply/{applicationName}")
	public Map<String,String> newApiClient(String applicationName){
		Device device = apiService.apply(userAuthorizeData.getUserId(), applicationName);
		//
		// return secure token
		//
		HashMap<String,String> ret = new HashMap<String,String>();
		ret.put("device_id", device.getId());
		ret.put("secure_token",device.getToken());		
		return ret;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/request_session")
	public String requestSession(Map<String,String> request){
		String deviceId = userAuthorizeData.getDeviceId(); 
		String timestamp = request.get("timestamp");
		String authData = request.get("auth_data");
		
		return null;
	}
	
}
