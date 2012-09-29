package org.siraya.rent.rest;
import java.util.Map;
import java.net.HttpURLConnection;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;
import org.siraya.rent.utils.IApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/admin")
public class AdminRestApi {
    @Autowired
    private IApplicationConfig applicationConfig;
    private static Logger logger = LoggerFactory.getLogger(AdminRestApi.class);

    public AdminRestApi(){
    	logger.debug("new admin rest api");
    }

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/set_sently_debug_flag/{debug_flag}")
    public Response put(@PathParam("debug_flag") boolean flag) {
    	logger.debug("set debug flag "+flag);
    	Map<String,Object> sently = this.applicationConfig.get("sently");
    	sently.put("debug", flag);
    	HashMap<String,Boolean> response = new HashMap<String,Boolean>();
		response.put("flag", new Boolean(flag));
    	return Response.status(HttpURLConnection.HTTP_OK).entity(response).build();
    }
}
