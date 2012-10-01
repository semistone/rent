package org.siraya.rent.utils;

import junit.framework.Assert;

import org.junit.Test;

public class TestEncodeUtility {
	@Test 
	public void testEncode(){
		String test=EncodeUtility.sha1("test");
		//System.out.println(test);
	}
	
	@Test 
	public void testEncrypt() throws Exception{
		EncodeUtility test = new EncodeUtility("thebestsecretkey");
		String msg = "testmsgccccccccccccccccccccccccccccccccccccc";
		String encrypt = test.encrypt(msg);
		String msg2 = test.decrypt(encrypt);
		Assert.assertEquals(msg, msg2);
	}
}
