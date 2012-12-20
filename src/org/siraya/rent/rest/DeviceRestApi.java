package org.siraya.rent.rest;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.pojo.Session;
import org.siraya.rent.user.service.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("deviceRestApi")
@Path("/device")
@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
public class DeviceRestApi {
	private static Map<String, String> OK;
	private static Logger logger = LoggerFactory.getLogger(DeviceRestApi.class);
	@Autowired
	private UserAuthorizeData userAuthorizeData;
	@Autowired
	private ISessionService sessionService;

	public DeviceRestApi() {
		if (OK == null) {
			OK = new HashMap<String, String>();
			OK.put("status", "SUCCESS");
		}
	}

	@GET
	@Path("/connect/{callback}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response connect(@PathParam("callback") String callback) {
		logger.debug("connect");
		Session session = userAuthorizeData.getSession();
		if (session.getCallback() != null) {
			return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
		}
		session.setCallback(callback);		
		sessionService.connect(session);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}

	@GET
	@Path("/disconnect")
	@Produces(MediaType.APPLICATION_JSON)
	public Response disconnect(@PathParam("id") String id) {
		logger.debug("disconnect");
		Session session = userAuthorizeData.getSession();
		sessionService.disconnect(session);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}

}
