package org.siraya.rent.filter;


import org.siraya.rent.rest.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import org.siraya.rent.utils.RentException.RentErrorCode;

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
    private static Logger logger = LoggerFactory.getLogger(CookieExtractFilter.class);

	@Autowired
	private UserAuthorizeData userAuthorizeData;
	@Autowired
	private CookieUtils cookieUtils;
    @Autowired
    protected IApplicationConfig applicationConfig;
	
	public UserAuthorizeData getUserAuthorizeData() {
		return userAuthorizeData;
	}
	
	public void setUserAuthorizeData(UserAuthorizeData userAuthorizeData) {
		this.userAuthorizeData = userAuthorizeData;
	}
	
	@Override
	public ContainerRequest filter(ContainerRequest request) {
		String path = request.getPath();
		if (this.isExclude(path)){
			return request;
		}
		//
		// test security context
		//
		userAuthorizeData.request = request;
		
		logger.debug("pass filter");
		Map<String,Cookie>cookies = request.getCookies();
		MultivaluedMap<String, String> headers = request.getRequestHeaders();
		if (cookies.containsKey("D")){
			String value = cookies.get("D").getValue();
			cookieUtils.extractDeviceCookie(value,userAuthorizeData);
			if (userAuthorizeData.getDeviceId() != null)
				headers.add("DEVICE_ID", userAuthorizeData.getDeviceId());
			if (userAuthorizeData.getUserId() != null)
				headers.add("USER_ID", userAuthorizeData.getUserId());    
		} else {
			if (headers.containsKey("DEVICE_ID")) {
				userAuthorizeData.setDeviceId(headers.getFirst("DEVICE_ID"));				
			}
			if (headers.containsKey("USER_ID")) {
				userAuthorizeData.setUserId(headers.getFirst("USER_ID"));				
			}
		}
		
		if (userAuthorizeData.getDeviceId() == null) {
			throw new RentException(RentErrorCode.ErrorNullDeviceId, "no device cookie");			
		}
		logger.debug("set security context");
		request.setSecurityContext(new Authorizer(userAuthorizeData));
	    
		request.setHeaders((InBoundHeaders)headers);		
	    return request;
	}

	private boolean isExclude(String path){
		logger.debug("path is "+path);
		List<Pattern> exclude = (List<Pattern>)applicationConfig.get("filter").get("_exclude_pattern_cache");			
		if (exclude == null) {
			logger.debug("pattern cache not exist");
			exclude = new java.util.ArrayList<Pattern>();
			List<String> excludeString = (List<String>)applicationConfig.get("filter").get("exclude");			
			int size = excludeString.size();
			for (int i = 0 ; i < size ; i++) {
				Pattern pattern = Pattern.compile(excludeString.get(i));
				exclude.add(pattern);	
			}
			applicationConfig.get("filter").put("_exclude_pattern_cache", exclude);
		}
		int size = exclude.size();
		for (int i = 0 ; i < size ; i++) {
			Pattern pattern = exclude.get(i);
			//logger.debug("try match "+pattern.pattern());
			if (pattern.matcher(path).find()) {
				logger.debug("match pattern rule "+exclude.get(i).pattern());
				return true;
			}
		}
		return false;
	}
}
