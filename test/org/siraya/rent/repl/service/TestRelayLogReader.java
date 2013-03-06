package org.siraya.rent.repl.service;

import java.util.HashMap;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.donttry.service.IDontTryService;
import org.siraya.rent.pojo.*;
import org.siraya.rent.repl.service.*;
import org.springframework.http.*;
import org.siraya.rent.utils.ApplicationConfig;
import org.siraya.rent.utils.IApplicationConfig;
import org.springframework.web.client.RestOperations;
public class TestRelayLogReader {
	RelayLogReader relayLogReader;
    private IApplicationConfig applicationConfig;
    private RestOperations restTemplate;
	private Mockery context;
    String name;
    Message message = new Message();
    @Before
	public void setUp() throws Exception{
		context = new JUnit4Mockery();
		restTemplate = context.mock(RestOperations.class);	
		String name = "reader1";
    	applicationConfig = new ApplicationConfig();
		relayLogReader = new RelayLogReader();
		relayLogReader.setRestTemplate(restTemplate);
		relayLogReader.setApplicationConfig(applicationConfig);
		relayLogReader.init(name);	
		message.setCmd("test");
		message.setData("test data".getBytes());
	}
	
	@Test 
	public void testConsume(){
		context.checking(new Expectations() {
			{
				one(restTemplate).postForEntity(with(any(String.class)), with(any(HttpEntity.class)), with(any(Class.class)),with(any(Object.class)));
			}
		});
		relayLogReader.consume(message);
	}
}
