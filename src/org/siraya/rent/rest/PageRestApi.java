package org.siraya.rent.rest;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.HttpURLConnection;
import java.util.Map;
import org.siraya.rent.page.service.IPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.pojo.Space;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("pageRestApi")
@Path("/page")
public class PageRestApi {
	@Autowired
	private IPageService pageService;
	private static Logger logger = LoggerFactory
			.getLogger(PageRestApi.class);
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}.json")
	public Response get(@PathParam("id") String id){
		logger.debug("get page space "+id);
		java.util.List<Space> ret=pageService.getSpaces(id);
		java.util.HashMap<String, String> ret2= new java.util.HashMap<String, String>();
		for (Space space : ret) {
			ret2.put(space.getName(), space.getContent());
		}
		java.util.Date expirationDate = new java.util.Date(System.currentTimeMillis() + 3600000 * 24);
		return Response.status(HttpURLConnection.HTTP_OK).entity(ret2).expires(expirationDate).build();
	}
}
