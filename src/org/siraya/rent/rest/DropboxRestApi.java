package org.siraya.rent.rest;

import java.net.HttpURLConnection;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.siraya.rent.dropbox.service.IDropboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("dropboxRestApi")
@Path("/dropbox")
public class DropboxRestApi {
	@Autowired
	private IDropboxService dropboxService;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/do_link")
	public Response doLink(){	
		Map<String, String> result = new java.util.HashMap<String, String>();
		String url = this.dropboxService.doLink();
		result.put("url", url);
		return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
	}
}
