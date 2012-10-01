package org.siraya.rent.rest;

import javax.ws.rs.core.NewCookie;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.utils.IApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.siraya.rent.utils.EncodeUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
@Service("cookieUtils")
public class CookieUtils {
    @Autowired
    private IApplicationConfig applicationConfig;
    private EncodeUtility encodeUtility;
    private boolean isInit = false;
    private static Logger logger = LoggerFactory.getLogger(CookieUtils.class);

    private void init(){
    	if (this.isInit) {
    		return;
    	}
    	Map<String,Object> setting = applicationConfig.get("keydb");
    	String key=(String)setting.get("cookie");
    	encodeUtility = new EncodeUtility(key);
    	this.isInit= true;
    }
    
    public NewCookie createDeviceCookie(Device device){
    	init();
		String value= device.getId()+":"+device.getUserId();
		value = encodeUtility.encrypt(value);
		logger.debug("cookie value is "+value);
		NewCookie deviceCookie = new NewCookie("D", value, "/",
				null, 1, "no comment", 1073741823, // maxAge max int value/2
				false);
		return deviceCookie;
	}
	
    public  NewCookie newDeviceCookie(String Id){    	
    	init(); // temp solution
    	String value= Id+":";
		value = encodeUtility.encrypt(value);
		logger.debug("cookie value is "+value);
		NewCookie deviceCookie = new NewCookie("D", value, "/",
				null, 1, "no comment", 1073741823, // maxAge max int value/2
				false);
		return deviceCookie;	
	}
    
    public void extractDeviceCookie(String deviceCookie,UserAuthorizeData userAuthorizeData){
    	init(); // temp solution
    	deviceCookie = encodeUtility.decrypt(deviceCookie);
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
