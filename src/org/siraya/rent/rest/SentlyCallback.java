<<<<<<< HEAD
package org.siraya.rent.rest;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import javax.ws.rs.Path;
import org.siraya.rent.user.service.IMobileAuthService;
import org.siraya.rent.utils.RentException.RentErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Component("sentlyCallback")
@Path("/sently_callback")
public class SentlyCallback {

	@Autowired
	private IApplicationConfig applicationConfig;

	@Autowired
	private IMobileAuthService mobileAuthService;
	private static Logger logger = LoggerFactory
			.getLogger(SentlyCallback.class);
	private static Pattern mobilePhonePattern = Pattern.compile("R:(\\d*)");

	@GET
	@Path("/{password}")
	public Response receiveMessage(@PathParam("password") String password,
			@QueryParam("from") String from, @QueryParam("text") String text)
			throws Exception {
		logger.debug("receive message " + text + " from " + from);
		if (!password.equals(applicationConfig.get("sently").get("callback_pass").toString())) {
			throw new RentException(
					RentException.RentErrorCode.ErrorPermissionDeny,
					"password not match");
		}
		Matcher matcher = mobilePhonePattern.matcher(text);
		String authCode;
		if (matcher.find(0)) {
			authCode = matcher.group(0).substring(2);
			logger.debug("auth code is " + authCode);
		} else {
			throw new RentException(RentErrorCode.ErrorInvalidParameter,
					"can not match R: + digits");
		}

		if (from.trim().substring(0, 1).equals("+")) {
			from = from.substring(1);
			logger.debug("from is " + from);
		}
		mobileAuthService.verifyAuthCodeByMobilePhone(from, authCode);
		return Response.status(Response.Status.OK).build();
	}

	public void setMobileAuthService(IMobileAuthService mobileAuthService) {
		this.mobileAuthService = mobileAuthService;
	}

	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}
}
=======
package org.siraya.rent.rest;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import javax.ws.rs.Path;
import org.siraya.rent.user.service.IMobileAuthService;
import org.siraya.rent.utils.RentException.RentErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Component("sentlyCallback")
@Path("/sently_callback")
public class SentlyCallback {

	@Autowired
	private IApplicationConfig applicationConfig;

	@Autowired
	private IMobileAuthService mobileAuthService;
	private static Logger logger = LoggerFactory
			.getLogger(SentlyCallback.class);
	private static Pattern mobilePhonePattern = Pattern.compile("R:(\\d*)");

	@GET
	@Path("/{password}")
	public Response receiveMessage(@PathParam("password") String password,
			@QueryParam("from") String from, @QueryParam("text") String text)
			throws Exception {
		logger.debug("receive message " + text + " from " + from);
		if (!password.equals(applicationConfig.get("sently").get("callback_pass").toString())) {
			throw new RentException(
					RentException.RentErrorCode.ErrorPermissionDeny,
					"password not match");
		}
		Matcher matcher = mobilePhonePattern.matcher(text);
		String authCode;
		if (matcher.find(0)) {
			authCode = matcher.group(0).substring(2);
			logger.debug("auth code is " + authCode);
		} else {
			throw new RentException(RentErrorCode.ErrorInvalidParameter,
					"can not match R: + digits");
		}

		if (from.trim().substring(0, 1).equals("+")) {
			from = from.substring(1);
			logger.debug("from is " + from);
		}
		mobileAuthService.verifyAuthCodeByMobilePhone(from, authCode);
		return Response.status(Response.Status.OK).build();
	}

	public void setMobileAuthService(IMobileAuthService mobileAuthService) {
		this.mobileAuthService = mobileAuthService;
	}

	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}
}
>>>>>>> master
