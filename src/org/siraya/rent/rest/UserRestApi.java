package org.siraya.rent.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.HashMap;
import org.siraya.rent.user.service.IUserService;
import org.siraya.rent.user.service.ISessionService;
import org.siraya.rent.utils.RentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("userRestApi")
@Path("/user")
@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
public class UserRestApi {
	@Autowired
	private IUserService userService;
	@Autowired
	private ISessionService sessionService;
	private static Map<String, String> OK;
	private static Logger logger = LoggerFactory.getLogger(UserRestApi.class);
	@Autowired
	private UserAuthorizeData userAuthorizeData;
	public UserRestApi() {
		logger.debug("new user rest api");
		if (OK == null) {
			OK = new HashMap<String, String>();
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online_status")
	public List<UserOnlineStatus> onlineStatus(List<String> ids) {
		return sessionService.list(ids);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatProfile(User user){
		user.setId(userAuthorizeData.getUserId());
		logger.debug("update id is "+user.getId());
		logger.debug("update email is "+user.getEmail());
		this.userService.updateProfile(user);
		return Response.status(HttpURLConnection.HTTP_OK).entity(OK).build();

	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/link_facebook")
	public Response linkFacebook(User user) {
		user.setId(userAuthorizeData.getUserId());
		if (user == null || user.getLoginId() == null) {
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"user not exist");
		}
		this.userService.initLoginIdAndType(user);
		logger.info("link to facebook success");
		return Response.status(HttpURLConnection.HTTP_OK).entity(this.OK)
				.build();
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/online")
	public List<User> online(
			@DefaultValue("10") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) {
		logger.debug("limit "+limit+" offset "+offset);
		return this.userService.getOnineUser(limit, offset);
	}
}
