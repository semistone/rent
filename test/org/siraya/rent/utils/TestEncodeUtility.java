package org.siraya.rent.utils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TestEncodeUtility {
	EncodeUtility test;
	@Before
	public void setUp() throws Exception{
		test = new EncodeUtility();
		test.setApplicationConfig(new ApplicationConfig());
	}
	@Test 
	public void testEncode(){
		String test=EncodeUtility.sha1("test");
		//System.out.println(test);
	}
	
	@Test 
	public void testEncrypt() throws Exception{

		String msg = "testmsgccccccccccccccccccccccccccccccccccccc";
		String encrypt = test.encrypt(msg,"cookie");
		String msg2 = test.decrypt(encrypt,"cookie");
		Assert.assertEquals(msg, msg2);
	}
}
