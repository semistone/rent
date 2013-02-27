package org.siraya.rent.rest;

import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.keystore.service.*;
import java.util.*;
@Component("keystoreApi")
@Path("/keystore")
@RolesAllowed({org.siraya.rent.filter.UserRole.ROOT})
public class KeystoreApi {
    @Autowired
    IKeystoreService keystoreService;

    @GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{key}")
    public Map<String,String> get(@PathParam("key")String key) {
    	HashMap<String,String> ret = new HashMap<String,String>();
    	ret.put("value", keystoreService.get(key));
    	return ret;

    }
    
    @PUT
	@Path("/{key}")
    public void update(@PathParam("key")String key,Map<String, String> request){
    	keystoreService.update(key, request.get("value"));
    }
    
	@Path("/{key}")
    @POST
    public void insert(@PathParam("key")String key,Map<String, String> request){
    	keystoreService.insert(key, request.get("value"));
    }
	
	@DELETE
	@Path("/{key}")
	public void delete(@PathParam("key")String key){
		this.keystoreService.delete(key);
	}
    	
}
