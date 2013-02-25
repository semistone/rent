package org.siraya.rent.repl.service;

import org.siraya.rent.pojo.Message;
import org.siraya.rent.utils.IApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestOperations;
import org.springframework.beans.factory.*;
import java.util.*;
import org.springframework.http.*;
@Scope("prototype")
public class RelayLogReader implements ILogReader {
	@Autowired
	private RestOperations restTemplate;

	@Autowired
	private IApplicationConfig applicationConfig;
	private static Logger logger = LoggerFactory
			.getLogger(RelayLogReader.class);
	private String name;
	private Map<String, Object> settings;

	private String relayTo;
	public void init(String name) throws Exception {
		logger.info("init relay reader with name "+name);
		this.name = name;
		Object tmp = ((Map<String, Object>) applicationConfig.get("repl").get(
				"log_readers")).get(name);
		if (tmp == null) {
			throw new Exception("setting for " + name + " is not exist");
		}
		settings = (Map<String, Object>) tmp;
		this.relayTo = (String)settings.get("relay_to");
		logger.info("relay to "+this.relayTo);
	}

	

	public void consume(Message message) {
		HashMap<String,String> args = new HashMap<String,String>();
		StringBuffer url = new StringBuffer(this.relayTo);
		url.append("/");
		url.append(message.getCmd());
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(new String(message.getData()), headers);
		logger.debug("url is "+url);
		restTemplate.postForEntity(url.toString(), entity,String.class);
	}
	
	public RestOperations getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
