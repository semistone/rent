package org.siraya.rent.mobile.provider;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;



public class SentlyMobileGatewayErrorHandler implements ResponseErrorHandler {

    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        try {
            HttpStatus status = clientHttpResponse.getStatusCode();
            if (status != HttpStatus.OK) {
                return true;
            }
            return false;

        } catch (IllegalArgumentException e) {
            //if status code is 490, it will throw IllegalArgumentException
            //restTemplate will throw exception, and can't go into handleError() method
            //so we must extract FaultInfo here
            HttpHeaders headers = clientHttpResponse.getHeaders();
            String faultMessage = headers.get("FaultInfo").get(0);
            throw new IOException(e.getMessage());
        }
    }


    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        throw new RestClientException(clientHttpResponse.getStatusText());
    }
}
