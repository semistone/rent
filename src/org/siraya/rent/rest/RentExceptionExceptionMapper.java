package org.siraya.rent.rest;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
@Provider
public class RentExceptionExceptionMapper implements ExceptionMapper<RentException> {
    private static Logger logger = LoggerFactory.getLogger(RentExceptionExceptionMapper.class);
	public RentExceptionExceptionMapper(){

	}
	
	public  Response toResponse(RentException exception) {
		logger.debug("extract exception to response");
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
				exception.getMessage()).type(MediaType.APPLICATION_JSON).build();
	}
}
