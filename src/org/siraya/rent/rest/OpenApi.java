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
	public Map<String,String> requestSession(Map<String,Object> request){
		long timestamp = (long)request.get("timestamp");
		String authData = (String)request.get("authData");
		String deviceId = (String)request.get("deviceId");

		List<Integer> roles = null;
		if (request.containsKey("roles")) 
			roles = (List<Integer>) request.get("roles");
		
		Session session = this.userAuthorizeData.getSession();
		session.setDeviceId(deviceId);
		this.apiService.requestSession(session, authData, timestamp, roles);
		HashMap<String,String> ret = new HashMap<String,String>();
		String newSessionKey = cookieUtils.encryptSession(session);
		ret.put("sessionKey", newSessionKey);
		return ret;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/request_session")
	public Map<String,String> updateSession(Map<String,Object> request){
		long timestamp = (long)request.get("timestamp");
		String authData = (String)request.get("authData");		
		String sessionKey = (String)request.get("sessionKey");
		Session session = cookieUtils.extraceSessionKey(sessionKey);
		//
		// change ip
		//
		String ip = this.userAuthorizeData.getSession().getLastLoginIp();		
		session.setLastLoginIp(ip);
		this.apiService.updateSession(session, authData, timestamp);
		HashMap<String,String> ret = new HashMap<String,String>();
		String newSessionKey = cookieUtils.encryptSession(session);
		ret.put("sessionKey", newSessionKey);
		return ret;
	}
	
	public UserAuthorizeData getUserAuthorizeData() {
		return userAuthorizeData;
	}

	public void setUserAuthorizeData(UserAuthorizeData userAuthorizeData) {
		this.userAuthorizeData = userAuthorizeData;
	}

	public IApiService getApiService() {
		return apiService;
	}

	public void setApiService(IApiService apiService) {
		this.apiService = apiService;
	}
	public CookieUtils getCookieUtils() {
		return cookieUtils;
	}

	public void setCookieUtils(CookieUtils cookieUtils) {
		this.cookieUtils = cookieUtils;
	}
}
