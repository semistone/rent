package org.siraya.rent.filter;


import org.siraya.rent.user.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	public UserAuthorizeData getUserAuthorizeData() {
		return userAuthorizeData;
	}
	
	public void setUserAuthorizeData(UserAuthorizeData userAuthorizeData) {
		this.userAuthorizeData = userAuthorizeData;
	}
	
	@Override
	public ContainerRequest filter(ContainerRequest request) {
		logger.debug("pass filter");
	    System.out.println(this.userAuthorizeData);
		logger.debug("user service is "+userService);
		Map<String,Cookie>cookies = request.getCookies();
		MultivaluedMap<String, String> headers = request.getRequestHeaders();
		if (cookies.containsKey("D")){
			String value = cookies.get("D").getValue();
		    String[] strings=value.split(":");
		    int len = strings.length;
		    if (len > 0){
		    	logger.debug("set device id as "+strings[0]);
			    headers.add("DEVICE_ID", strings[0]);	
			    userAuthorizeData.setDeviceId(strings[0]);

		    } 
		    if (len > 1){
		    	logger.debug("set user id as "+strings[1]);
		    	headers.add("USER_ID", strings[1]);
			    userAuthorizeData.setUserId(strings[1]);
		    }

		}
	    request.setHeaders((InBoundHeaders)headers);		
	    return request;
	}

}
