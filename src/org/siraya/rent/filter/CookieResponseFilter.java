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
	public ContainerResponse filter(ContainerRequest request,
			ContainerResponse response) {
		MultivaluedMap<String, String> headers = request.getRequestHeaders();
		String ip = headers.getFirst("X-Real-IP");
		logger.debug("real ip is "+ip);
		Map<String,Cookie>cookies = request.getCookies();
		if (!cookies.containsKey("S")) {
			logger.debug("create new session cookie");

			String userId= headers.getFirst("USER_ID");
			String deviceId = headers.getFirst("DEVICE_ID");
			Session session = new Session();
			session.setDeviceId(deviceId);
			session.setUserId(userId);

			session.setLastLoginIp(ip);
			NewCookie sessionCookie = cookieUtils.newSessionCookie(session);
			if (sessionCookie == null) {
				return response;
			}
			Response cookieResponse = Response.fromResponse(response.getResponse()).cookie(sessionCookie).build();			  
			response.setResponse(cookieResponse);
			if (userId != null && deviceId != null) {
				logger.info("new session");
				sessionService.newSession(session);				
			}
		}
		return response;
	}

}
