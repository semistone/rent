package org.siraya.rent.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.siraya.rent.pojo.User;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.user.service.IMobileAuthService;
import org.siraya.rent.user.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.siraya.rent.pojo.Device;
@Component
@Path("/user")
public class UserRestApi {
	@Autowired
	private IUserService userService;
	@Autowired
	private IMobileAuthService mobileAuthService;
    private static Logger logger = LoggerFactory.getLogger(UserRestApi.class);



	/**
     * create new device and assign a device id for it.
     * @param cc
     * @param mobilePhone
     * @return
     */
	@GET
	@Path("/new_device")
	public Response newDevice(@QueryParam("country_code") String cc ,@QueryParam("mobile_phone") String mobilePhone){
		try {
			User user =userService.newUserByMobileNumber(Integer.parseInt(cc), mobilePhone);
			Device device = new Device();
			device.setUser(user);
			userService.newDevice(device);
			return Response.status(200).entity(device.getId()).build();
		}catch(java.lang.NumberFormatException e){
			logger.error("country code or mobile number must be number",e);
			return Response.status(401).entity("cc must be number").build();						
		}catch(Exception e) {
			logger.error("exception "+e.getMessage(),e);
			return Response.status(500).entity(e.getMessage()).build();			
		}
	}
	
	/**
	 * send auth message
	 * @param deviceId
	 * @return
	 */
	@GET
	@Path("/send_mobile_auth_message")
	public Response sendMobileAuthMessage(@QueryParam("device_id") String deviceId){
		try {
			mobileAuthService.sendAuthMessage(deviceId);
			return Response.status(200).entity("OK").build();
		}catch(Exception e) {
			logger.error("exception "+e.getMessage(),e);
			return Response.status(500).entity(e.getMessage()).build();		
		}
	}
	
	void setUserService(IUserService userService) {
		this.userService = userService;
	}


	void setMobileAuthService(IMobileAuthService mobileAuthService) {
		this.mobileAuthService = mobileAuthService;
	}
}
