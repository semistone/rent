package org.siraya.rent.rest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.siraya.rent.filter.UserAuthorizeData;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import javax.ws.rs.core.NewCookie;

import junit.framework.Assert;

import org.siraya.rent.pojo.Device;
@ContextConfiguration(locations = {"classpath*:/applicationContext*.xml"})
public class TestCookieUtils  extends AbstractJUnit4SpringContextTests{
	@Autowired
	CookieUtils cookieUtils;
	private String deviceId ="test new device";
	@Test 
	public void testNewCookie(){
		NewCookie cookie = cookieUtils.newDeviceCookie("test");
	}
	
	@Test 	
	public void testNewCookie2()throws Exception{
		Device device = new Device();
		device.setId(deviceId);
		device.setUserId("test list devices");
		NewCookie cookie = cookieUtils.createDeviceCookie(device);
		String deviceCookie = cookie.getValue();
		//
		//test extract
		UserAuthorizeData userAuthorizeData=new UserAuthorizeData();
		cookieUtils.extractDeviceCookie(deviceCookie, userAuthorizeData);
		Assert.assertEquals(deviceId, userAuthorizeData.getDeviceId());
	}
	
	@Test
	public void testRemoveCookie(){
		NewCookie cookie =cookieUtils.removeDeviceCookie();
		Assert.assertEquals(-1, cookie.getMaxAge());
	}

}
