package org.siraya.rent.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.siraya.rent.pojo.User;
import org.siraya.rent.pojo.Device;
import junit.framework.Assert;

import org.siraya.rent.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Component
@Path("/user")
public class UserRestApi {
	@Autowired
	private IUserService userService;

	@GET
	@Path("/newDevice")
	public Response newDevice(@QueryParam("country_code") String cc ,@QueryParam("mobile_phone") String mobilePhone){
		System.out.println("mobile phone is "+mobilePhone);
		try {
			User user =userService.newUserByMobileNumber(Integer.parseInt(cc), mobilePhone);
			Device device = new Device();
			device.setUser(user);
			userService.newDevice(device);
			return Response.status(200).entity(device.getId()).build();
		}catch(Exception e) {
			return Response.status(200).entity("test").build();			
		}
	}
}
