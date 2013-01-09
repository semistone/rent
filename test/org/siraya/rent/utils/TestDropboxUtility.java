package org.siraya.rent.utils;

import org.junit.Before;
import org.junit.Test;

public class TestDropboxUtility {
	DropboxUtility test = new DropboxUtility();
	@Before
	public void setUp() throws Exception{
		test.setApplicationConfig(new ApplicationConfig());
	}
	
	
	@Test 
	public void testDoLink() throws Exception{
		String link = test.doLink();
		System.out.println("link is "+link);
	}
}
