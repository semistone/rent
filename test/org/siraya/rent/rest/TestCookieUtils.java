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
	private String deviceId ="486cc1d0-90fc-4f58-a0db-5739745785d5";
	@Test 
	public void testNewCookie(){
		NewCookie cookie = cookieUtils.newDeviceCookie("test");
	}
	@Test 	
	public void testNewCookie2()throws Exception{
		Device device = new Device();
		device.setId(deviceId);
		device.setUserId("6d6b579f-bdae-4396-946d-29431f9dd9f3");
		NewCookie cookie = cookieUtils.createDeviceCookie(device);
		String deviceCookie = cookie.getValue();
		//
		//test extract
		UserAuthorizeData userAuthorizeData=new UserAuthorizeData();
		cookieUtils.extractDeviceCookie(deviceCookie, userAuthorizeData);
		Assert.assertEquals(deviceId, userAuthorizeData.getDeviceId());
	}

}
