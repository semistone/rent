package org.siraya.rent.user.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.user.service.ApiClientService;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-rest.xml","classpath*:/applicationContext-*.xml"})
public class TestApiClientService extends AbstractJUnit4SpringContextTests{
	@Autowired
	ApiClientService appClientService;
	String applicationName;
	@Before
	public void setUp(){
		applicationName = "app2";
	}
	
	@Test
	public void testRequestSession(){
		appClientService.requestSession(applicationName);
	}
}
