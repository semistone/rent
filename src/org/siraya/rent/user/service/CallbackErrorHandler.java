package org.siraya.rent.user.service;

import java.io.IOException;

import javax.ws.rs.ext.Provider;
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Provider
@Component("callbackErrorHandler")
public class CallbackErrorHandler implements ResponseErrorHandler {
	private static Logger logger = LoggerFactory.getLogger(CallbackErrorHandler.class);

	public boolean hasError(ClientHttpResponse clientHttpResponse)
			throws IOException {
		try {
			HttpStatus status = clientHttpResponse.getStatusCode();
			if (status != HttpStatus.OK) {
				return true;
			}
			return false;
		} catch (IllegalArgumentException e) {
			throw new IOException(e.getMessage());
		}
	}

	public void handleError(ClientHttpResponse clientHttpResponse)
			throws IOException {
		logger.error("callback error");
		throw new RentException(RentException.RentErrorCode.ErrorCallback,
				clientHttpResponse.getStatusText());
	}

}
