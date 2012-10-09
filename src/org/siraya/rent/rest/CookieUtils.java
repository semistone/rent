package org.siraya.rent.rest;

import javax.ws.rs.core.NewCookie;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.pojo.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.siraya.rent.utils.EncodeUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.siraya.rent.utils.RentException;
@Service("cookieUtils")
public class CookieUtils {
	@Autowired
    private EncodeUtility encodeUtility;

    private static Logger logger = LoggerFactory.getLogger(CookieUtils.class);
    private final static String keyName="cookie";

    public NewCookie createDeviceCookie(Device device){

		String value= device.getId()+":"+device.getUserId();
		value = encodeUtility.encrypt(value, keyName);
		logger.debug("cookie value is "+value);
		NewCookie deviceCookie = new NewCookie("D", value, "/",
				null, 1, "no comment", 1073741823, // maxAge max int value/2
				false);
		return deviceCookie;
	}
	
    public  NewCookie newDeviceCookie(String Id){    	
    	String value= Id+":";
		value = encodeUtility.encrypt(value,keyName);
		logger.debug("cookie value is "+value);
		NewCookie deviceCookie = new NewCookie("D", value, "/",
				null, 1, "no comment", 1073741823, // maxAge max int value/2
				false);
		return deviceCookie;	
	}
	
    public  NewCookie removeDeviceCookie(){    	
		NewCookie deviceCookie = new NewCookie("D", "", "/",
				null, 1, "remove this cookie", -1, 
				false);
		return deviceCookie;	
	} 
    
    public void extractDeviceCookie(String deviceCookie,UserAuthorizeData userAuthorizeData){
    	try{
    		deviceCookie = encodeUtility.decrypt(deviceCookie,keyName);
    	}catch (RentException e){
    		throw new RentException(RentException.RentErrorCode.ErrorCookieFormat,
    				"unknown cookie format");
    	}
    	String[] strings=deviceCookie.split(":");
	    int len = strings.length;
	    if (len > 0){
	    	logger.debug("set device id as "+strings[0]);	
		    userAuthorizeData.setDeviceId(strings[0]);

	    } 
	    if (len > 1){
	    	logger.debug("set user id as "+strings[1]);
		    userAuthorizeData.setUserId(strings[1]);
	    }
    }
}
