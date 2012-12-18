package org.siraya.rent.rest;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("deviceRestApi")
@Path("/device")
@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
public class DeviceRestApi {
	private static Map<String, String> OK;
	private static Logger logger = LoggerFactory.getLogger(DeviceRestApi.class);
	public DeviceRestApi(){
		if (OK == null) {
			OK = new HashMap<String, String>();
			OK.put("status", "SUCCESS");
		}
	}
	
	@GET
	@Path("/connect")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response connect(){
		logger.debug("connect");
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}

	
	@GET
	@Path("/disconnect")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response disconnect(){
		logger.debug("disconnect");
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();
	}
	
}
