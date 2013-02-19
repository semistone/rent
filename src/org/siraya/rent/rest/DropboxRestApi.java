package org.siraya.rent.rest;

import java.net.HttpURLConnection;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.siraya.rent.dropbox.service.IDropboxService;
import org.siraya.rent.filter.UserAuthorizeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dropboxRestApi")
@Path("/dropbox")
public class DropboxRestApi {
	@Autowired
	private IDropboxService dropboxService;
	@Autowired
	private UserAuthorizeData userAuthorizeData;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/do_link")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response doLink(@QueryParam(".done") String done ) throws Exception{	
		String url = this.dropboxService.doLink(userAuthorizeData.getUserId(), done);
		return Response.seeOther(new java.net.URI(url)).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/retrieve_token")
	@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
	public Response retrieveToken(@QueryParam(".done") String done)throws Exception{
		this.dropboxService.retrieveWebAccessToken(userAuthorizeData.getUserId());
		return Response.seeOther(new java.net.URI(done)).build();
	}
}
