package org.siraya.rent.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.springframework.stereotype.Component;
@Component
@Path("/user")
public class UserRestApi {
	
	@GET
	@Path("/newDevice")
	public Response newDevice(){
		return Response.status(200).entity("test").build();
	}
}
