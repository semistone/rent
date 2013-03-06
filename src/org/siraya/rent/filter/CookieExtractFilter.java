package org.siraya.rent.filter;

import org.siraya.rent.pojo.Device;
import org.siraya.rent.pojo.Session;
import org.siraya.rent.rest.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.user.service.ISessionService;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import org.siraya.rent.utils.RentException.RentErrorCode;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.core.header.InBoundHeaders;
import javax.ws.rs.core.Cookie;

@Component
public class CookieExtractFilter implements ContainerRequestFilter {
	private static Logger logger = LoggerFactory
			.getLogger(CookieExtractFilter.class);

	@Autowired
	private UserAuthorizeData userAuthorizeData;
	@Autowired
	private CookieUtils cookieUtils;
	@Autowired
	private IApplicationConfig applicationConfig;
	@Autowired
	private ISessionService sessionService;
	public UserAuthorizeData getUserAuthorizeData() {
		return userAuthorizeData;
	}

	public void setUserAuthorizeData(UserAuthorizeData userAuthorizeData) {
		this.userAuthorizeData = userAuthorizeData;
	}

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		String path = request.getPath();
		if (this.isExclude(path)) {
			return request;
		}
		//
		// test security context
		//
		userAuthorizeData.setRequest(request);

		this.extraceDeviceCookie(request);
		if (userAuthorizeData.getDeviceId() == null) {
			String deviceId = Device.genId();
			this.userAuthorizeData.setDeviceId(deviceId);
			this.userAuthorizeData.setNewDevice(true);
		}
		this.extractSessionCookie(request);
		logger.debug("set security context");
		request.setSecurityContext(new Authorizer(userAuthorizeData));

		return request;
	}

	/**
	 * extract session cookie and set into userAuthorizeData.session
	 * 
	 * @param request
	 */
	private void extractSessionCookie(ContainerRequest request){
		Map<String, Cookie> cookies = request.getCookies();
		MultivaluedMap<String, String> headers = request.getRequestHeaders();
		String ip = headers.getFirst("X-Real-IP");
		String cookieValue =  null;
		if (!this.userAuthorizeData.isBrower() || headers.containsKey("SESSIONKEY")) {
			//
			// get sesson ley from cookie or HEADER[SESSION_KEY]
			//
			userAuthorizeData.setBrower(false);
			cookieValue = headers.getFirst("SESSIONKEY");
		} else if (cookies.containsKey("S")) {
			cookieValue =  cookies.get("S").getValue();
		}
		
		if (cookieValue != null) {
			logger.debug("extract session cookie exist");
			cookieUtils.extractSessionCookie(cookieValue, userAuthorizeData);	
			Session session = userAuthorizeData.getSession();
			if (session != null) {
				if (ip == null) {
					logger.debug("ip is null");
				} else if (!session.getLastLoginIp().equals(ip)){
					logger.debug("ip not match remove session cookie");
					if (userAuthorizeData.isBrower()) {
						cookies.remove("S");
						this.newSession(ip);						
					} else {
						//
						// open api only allow right ip
						//
						throw new RentException(
								RentException.RentErrorCode.ErrorPermissionDeny,
								"ip not match");
					}
				} 
			}
			//
			// check session timeout, 
			//			
			if (session.getTimeout() > 0) {
				long now = Calendar.getInstance().getTime().getTime();
				if (session.getTimeout() > now) {
					throw new RentException(RentException.RentErrorCode.ErrorSessionTimeout,"session timeout");
				}
			}
		}else{
			newSession(ip);
		}
	}
	
	/**
	 * if user id and device id is not null, then set new session in user auth data.
	 * @param ip
	 */
	private void newSession(String ip){
		String userId = this.userAuthorizeData.getUserId();
		String deviceId = this.userAuthorizeData.getDeviceId();
		if (userId != null && deviceId != null) {
			Session session = new Session();
			session.genId();
			session.setDeviceId(deviceId);
			session.setUserId(userId);	
			session.setLastLoginIp(ip);		
			sessionService.newSession(session);				
			this.userAuthorizeData.setSession(session);
		}  else if (!this.userAuthorizeData.isBrower()){
			//
			// if open api, then set new session for ip address
			//
			Session session = new Session();
			session.genId();
			session.setLastLoginIp(ip);		
			this.userAuthorizeData.setSession(session);
		}
		
			
	}
	private void extraceDeviceCookie(ContainerRequest request) {
		Map<String, Cookie> cookies = request.getCookies();
		MultivaluedMap<String, String> headers = request.getRequestHeaders();
		if (headers.containsKey("DEVICEID")) {
			this.userAuthorizeData.setBrower(false);
			this.userAuthorizeData.setDeviceId(headers.getFirst("DEVICEID"));
		} else if (cookies.containsKey("D")) {
			String value = cookies.get("D").getValue();
			cookieUtils.extractDeviceCookie(value, userAuthorizeData);
		} 
		request.setHeaders((InBoundHeaders) headers);
	}

	private boolean isExclude(String path) {
		logger.debug("path is " + path);
		List<Pattern> exclude = (List<Pattern>) applicationConfig.get("filter")
				.get("_exclude_pattern_cache");
		if (exclude == null) {
			logger.debug("pattern cache not exist");
			exclude = new java.util.ArrayList<Pattern>();
			List<String> excludeString = (List<String>) applicationConfig.get(
					"filter").get("exclude");
			int size = excludeString.size();
			for (int i = 0; i < size; i++) {
				Pattern pattern = Pattern.compile(excludeString.get(i));
				exclude.add(pattern);
			}
			applicationConfig.get("filter").put("_exclude_pattern_cache",
					exclude);
		}
		int size = exclude.size();
		for (int i = 0; i < size; i++) {
			Pattern pattern = exclude.get(i);
			// logger.debug("try match "+pattern.pattern());
			if (pattern.matcher(path).find()) {
				logger.debug("match pattern rule " + exclude.get(i).pattern());
				return true;
			}
		}
		return false;
	}
}
