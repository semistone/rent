package org.siraya.rent.user.service;

import org.siraya.rent.keystore.service.IKeystoreService;
import org.siraya.rent.utils.IApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import java.util.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * 
 * @author angus_chen
 * 
 */
@Service("apiClientService")
public class ApiClientService {
	@Autowired
	private IApplicationConfig applicationConfig;
	@Autowired
	IKeystoreService keystoreService;
	@Autowired
	private RestOperations restTemplate;

	private static Logger logger = LoggerFactory
			.getLogger(ApiClientService.class);

	public void requestSession(String applicationName) {
		String ca = (String) applicationConfig.get("general").get("ca_host");
		logger.debug("ca host is " + ca);
		//
		// setup auth data
		//
		HttpEntity<Map> request = this.setupAuthData(applicationName);
		ResponseEntity<Map> response = restTemplate.postForEntity(ca, request,
				Map.class);
		String key = applicationName+"_session_key";		
		keystoreService.insert(key, (String)response.getBody().get("sessionKey"));
	}
	
	public void updateSession(String applicationName) {
		String ca = (String) applicationConfig.get("general").get("ca_host");
		logger.debug("ca host is " + ca);
		//
		// setup auth data
		//
		HttpEntity<Map> request = this.setupAuthData(applicationName);
		//
		// set session key
		//
		String key = applicationName+"_session_key";		
		String sessionKey = keystoreService.get(key);
		request.getBody().put("sessionKey", sessionKey);
		restTemplate.exchange(ca, HttpMethod.PUT, request, Map.class);
		ResponseEntity<Map> response = restTemplate.postForEntity(ca, request,
				Map.class);
		keystoreService.update(key, (String)response.getBody().get("sessionKey"));
		
	}
	
	public void deviceAuth(String applicationName, HttpEntity request) {		
		//
		// set session key
		//
		String key = applicationName+"_session_key";		
		String sessionKey = keystoreService.get(key);
		request.getHeaders().add("SESSIONKEY", sessionKey);
	}
	
	private HttpEntity<Map>  setupAuthData(String applicationName) {
		HttpHeaders headers = new HttpHeaders();
		//
		// setup auth data in body
		//
		HashMap<String, Object> params = new HashMap<String, Object>();
		String token = keystoreService.get(applicationName + "_secure_token");
		long timestamp = Calendar.getInstance().getTime().getTime() / 1000;
		String authData = ApiService.genAuthData(token, timestamp);
		// logger.debug("auth data is "+authData);
		// logger.debug("timestamp is "+timestamp);
		params.put("authData", authData);
		params.put("timestamp", timestamp);
		//
		// set device id and content type
		//
		String deviceId = keystoreService.get(applicationName + "_device_id");
		logger.debug("device id is " + deviceId);
		headers.set("DEVICEID", deviceId);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map> request = new HttpEntity<Map>(params, headers);
		return request;
	}
}
