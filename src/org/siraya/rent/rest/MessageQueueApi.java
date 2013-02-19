package org.siraya.rent.rest;

import java.io.InputStream;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.servlet.http.*;
import org.siraya.rent.repl.service.ILocalQueueService;
import org.siraya.rent.utils.RentException;
import org.siraya.rent.filter.UserAuthorizeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.siraya.rent.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * prove of concept repl implement. put message into queue.
 * 
 * @author angus_chen
 * 
 */
@RolesAllowed({ org.siraya.rent.filter.UserRole.DEVICE_CONFIRMED })
@Component("messageWriterApi")
@Path("/msg_queue")
public class MessageQueueApi {
	@Autowired
	private UserAuthorizeData userAuthorizeData;
	private static Logger logger = LoggerFactory.getLogger(MessageQueueApi.class);
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{queue}/{cmd}")
	public void post(@Context HttpServletRequest req,
			@PathParam("queue") String queue, @PathParam("cmd") String cmd,
			InputStream requestBodyStream) throws Exception {
		logger.debug("post queue:"+queue+" cmd:"+cmd);
		HttpSession ses = req.getSession(true);
		WebApplicationContext ctxt = WebApplicationContextUtils
				.getWebApplicationContext(ses.getServletContext());
		ILocalQueueService localQueueService = (ILocalQueueService) ctxt
				.getBean(queue);
		if (localQueueService == null) {
			throw new RentException(
					RentException.RentErrorCode.ErrorInvalidParameter,
					"queue not exist");
		}
		Message message = new Message();
		message.setUserId(userAuthorizeData.getUserId());
		message.setCmd(cmd);
		//
		// copy to byte array.
		//
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int n ;
		byte[] buffer = new byte[1024]; // you can configure the buffer size
		while ( (n = requestBodyStream.read(buffer,0, buffer.length)) != -1) {
			bos.write(buffer, 0, n ); // copy streams
		}
		message.setData(bos.toByteArray());
		localQueueService.insert(message);
	}

}
