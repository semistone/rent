package org.siraya.rent.filter;

import java.util.Map;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Cookie;
import org.siraya.rent.pojo.Session;
import org.siraya.rent.rest.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.user.service.ISessionService;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import javax.ws.rs.core.NewCookie;
@Component
public class CookieResponseFilter implements ContainerResponseFilter {
    private static Logger logger = LoggerFactory.getLogger(CookieResponseFilter.class);
	@Autowired
	private CookieUtils cookieUtils;
	@Autowired
	private ISessionService sessionService;
	@Autowired
	private UserAuthorizeData userAuthorizeData;
	public ContainerResponse filter(ContainerRequest request,
			ContainerResponse response) {
		Session session = this.userAuthorizeData.getSession();
		boolean changeCookie = false;
		NewCookie sessionCookie = null;
		NewCookie deviceCookie = null;
		//
		// update session cookie if need.
		//
		if (session != null && session.isChange()) {
			sessionCookie = cookieUtils.newSessionCookie(session);
			logger.debug("build new response with session cookie");
			changeCookie = true;
		} 
		//
		// update device cookie if need.
		//
		if (this.userAuthorizeData.isNewDevice()) {
			deviceCookie = cookieUtils.newDeviceCookie(this.userAuthorizeData.getDeviceId());
			logger.debug("build new response with device cookie");
			changeCookie = true;									
		}
		
		if (changeCookie) {			
			Response.ResponseBuilder responseBuilder  = Response.fromResponse(response.getResponse());
			if (sessionCookie != null) {
				responseBuilder.cookie(sessionCookie);
			}
			if (deviceCookie != null) {
				responseBuilder.cookie(deviceCookie);				
			}
			Response cookieResponse = responseBuilder.build();
			response.setResponse(cookieResponse);
		}
		return response;
	}

}
