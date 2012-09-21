package org.siraya.rent.rest;

import javax.ws.rs.Path;
import org.siraya.rent.user.service.IMobileAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
@Component
@Path("/sently_callback")
public class SentlyCallback {
	@Autowired
	private IMobileAuthService mobileAuthService;
    private static Logger logger = LoggerFactory.getLogger(SentlyCallback.class);

    @GET
	public Response receiveMessage(@QueryParam("from")String from,@QueryParam("text")String text) throws Exception{
		logger.debug("receive message "+text +" from "+from);
		String deviceId="test";
		String authCode="1234";
		logger.debug("mobile service is "+mobileAuthService);
		mobileAuthService.verifyAuthCode(deviceId, authCode);
		return Response.status(200).build();
	}
}
