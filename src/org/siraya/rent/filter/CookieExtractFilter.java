package org.siraya.rent.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.core.header.InBoundHeaders;
import javax.ws.rs.core.Cookie;
public class CookieExtractFilter implements ContainerRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(CookieExtractFilter.class);
	@Override
	public ContainerRequest filter(ContainerRequest request) {
		logger.debug("pass filter");
		Map<String,Cookie>cookies = request.getCookies();
		MultivaluedMap<String, String> headers = request.getRequestHeaders();
		if (cookies.containsKey("D")){
		    headers.add("DEVICE_ID", cookies.get("D").getValue());			
		}
	    request.setHeaders((InBoundHeaders)headers);		
	    return request;
	}

}
