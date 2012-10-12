package org.siraya.rent.filter;

import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Cookie;

import org.siraya.rent.rest.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import javax.ws.rs.core.NewCookie;
@Component
public class CookieResponseFilter implements ContainerResponseFilter {
    private static Logger logger = LoggerFactory.getLogger(CookieResponseFilter.class);
	@Autowired
	private CookieUtils cookieUtils;
	public ContainerResponse filter(ContainerRequest request,
			ContainerResponse response) {

		Map<String,Cookie>cookies = request.getCookies();
		if (!cookies.containsKey("S")) {
			logger.debug("create new session cookie");
			NewCookie sessionCookie = cookieUtils.newSessionCookie();
			Response cookieResponse = Response.fromResponse(response.getResponse()).cookie(sessionCookie).build();			  
			response.setResponse(cookieResponse);
		}
		return response;
	}

}
