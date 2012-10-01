package org.siraya.rent.filter;


import org.siraya.rent.rest.CookieUtils;
import org.siraya.rent.user.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.utils.RentException;
import org.siraya.rent.utils.RentException.RentErrorCode;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.core.header.InBoundHeaders;
import javax.ws.rs.core.Cookie;
@Component
public class CookieExtractFilter implements ContainerRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(CookieExtractFilter.class);
	@Autowired
	private IUserService userService;
	@Autowired
	private UserAuthorizeData userAuthorizeData;
	@Autowired
	private CookieUtils cookieUtils;
	
	
	public UserAuthorizeData getUserAuthorizeData() {
		return userAuthorizeData;
	}
	
	public void setUserAuthorizeData(UserAuthorizeData userAuthorizeData) {
		this.userAuthorizeData = userAuthorizeData;
	}
	
	@Override
	public ContainerRequest filter(ContainerRequest request) {
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
	    request.setHeaders((InBoundHeaders)headers);		
	    return request;
	}

}
