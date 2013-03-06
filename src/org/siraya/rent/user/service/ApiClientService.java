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
		HashMap<String, Object> params = new HashMap<String, Object>();
		String deviceId = keystoreService.get(applicationName + "_device_id");
		logger.debug("device id is " + deviceId);
		String token = keystoreService.get(applicationName + "_secure_token");
		long timestamp = Calendar.getInstance().getTime().getTime() / 1000;
		String authData = ApiService.genAuthData(token, timestamp);
		// logger.debug("auth data is "+authData);
		// logger.debug("timestamp is "+timestamp);
		HttpHeaders headers = new HttpHeaders();
		headers.set("DEVICE_ID", deviceId);
		headers.setContentType(MediaType.APPLICATION_JSON);
		params.put("authData", authData);
		params.put("timestamp", timestamp);
		HttpEntity<Map> request = new HttpEntity<Map>(params, headers);
		ResponseEntity<Map> response = restTemplate.postForEntity(ca, request,
				Map.class);
		String key = applicationName+"_session_key";		
		keystoreService.insert(key, (String)response.getBody().get("sessionKey"));
	}
}
