package org.siraya.rent.utils;

import org.junit.Test;

public class TestEncodeUtility {
	@Test 
	public void testEncode(){
		String test=EncodeUtility.sha1("test");
		System.out.println(test);
	}
}
