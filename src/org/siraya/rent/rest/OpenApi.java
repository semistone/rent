package org.siraya.rent.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

import org.siraya.rent.filter.UserAuthorizeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.user.service.*;
import org.siraya.rent.pojo.*;
@Component("openApi")
@Path("/api")
public class OpenApi {
	@Autowired
	private UserAuthorizeData userAuthorizeData;
	@Autowired
	private IApiService apiService;
	@Autowired
	private CookieUtils cookieUtils;
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
		ret.put("deviceId", device.getId());
		ret.put("secureToken",device.getToken());		
		return ret;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/request_session")
	public Map<String,String> requestSession(Map<String,String> request){
		String timestamp = request.get("timestamp");
		String authData = request.get("authData");
		String sessionKey;
		sessionKey = request.get("sessionKey");
		Session session = cookieUtils.extraceSessionKey(sessionKey);
		//
		// if device id is null then get it from request.
		//   only for first time not for update session.
		//
		if (session.getDeviceId() == null && request.containsKey("deviceId")) {
			String deviceId = session.getDeviceId();
			session.setDeviceId(deviceId);
		}
		
		this.apiService.requestSession(session, authData, Long.parseLong(timestamp));
		HashMap<String,String> ret = new HashMap<String,String>();
		String newSessionKey = cookieUtils.encryptSession(session);
		ret.put("sessionKey", newSessionKey);
		return ret;
	}
	
}
