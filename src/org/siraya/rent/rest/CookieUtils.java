package org.siraya.rent.rest;

import javax.ws.rs.core.NewCookie;

import org.siraya.rent.pojo.Device;

public class CookieUtils {
	static NewCookie createDeviceCookie(Device device) {
		String value= device.getId()+":"+device.getUserId();
		NewCookie deviceCookie = new NewCookie("D", value, "/",
				null, 1, "no comment", 1073741823, // maxAge max int value/2
				false);
		return deviceCookie;
	}
	
	static NewCookie newDeviceCookie(String Id) {
		String value= Id+":";
		NewCookie deviceCookie = new NewCookie("D", value, "/",
				null, 1, "no comment", 1073741823, // maxAge max int value/2
				false);
		return deviceCookie;	
	}
}
