package org.siraya.rent.utils;

import org.junit.Test;
import org.junit.Before;
import java.util.Map;

import junit.framework.Assert;
public class TestConfig {
	ApplicationConfig config;
	
	@Before
	public void setUp() throws Exception{
		config = new ApplicationConfig();	
	}
	
	@Test 
	public void testGetMobileSetting(){
		Map<String,Object> map= (Map<String,Object>)config.get("mobile_country_code");
		Assert.assertEquals("TW", ((Map<String,Object>)map.get(new Integer(886))).get("country"));
	}
}
