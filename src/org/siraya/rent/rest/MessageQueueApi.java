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
import java.util.*;
/**
 * prove of concept repl implement. put message into queue.
 * 
 * @author angus_chen
 * 
 */
// only allow api auth 
@RolesAllowed({org.siraya.rent.filter.UserRole.API_AUTH})
@Component("messageWriterApi")
@Path("/repl")
public class MessageQueueApi {
	@Autowired
	private UserAuthorizeData userAuthorizeData;
	private static Logger logger = LoggerFactory.getLogger(MessageQueueApi.class);
	private Map<String,String> ret;
	public MessageQueueApi(){
		ret = new HashMap<String,String> ();
		ret.put("status", "SUCCESS");
	}
	
	private ILocalQueueService getQueueService(HttpServletRequest req,String queue){
		HttpSession ses = req.getSession(true);
		WebApplicationContext ctxt = WebApplicationContextUtils
				.getWebApplicationContext(ses.getServletContext());
		ILocalQueueService localQueueService = (ILocalQueueService) ctxt
				.getBean(queue);		
		return localQueueService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{queue}/dump/{volumn}")
	public List<Message> dump(@Context HttpServletRequest req,
			@PathParam("queue") String queue,
			@PathParam("volumn") Integer volumn) throws Exception {
		ILocalQueueService localQueueService = getQueueService(req, queue);
		return localQueueService.dump(volumn.intValue());
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{queue}/meta")
	public List<QueueMeta> dump(@Context HttpServletRequest req,
			@PathParam("queue") String queue) throws Exception {
		ILocalQueueService localQueueService = getQueueService(req, queue);
		return localQueueService.getMetaList();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{queue}/{cmd}")
	public Map post(@Context HttpServletRequest req,
			@PathParam("queue") String queue, @PathParam("cmd") String cmd,
			InputStream requestBodyStream) throws Exception {
		logger.debug("post queue:"+queue+" cmd:"+cmd);
		ILocalQueueService localQueueService = getQueueService(req, queue);
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
		return ret;
	}

}
