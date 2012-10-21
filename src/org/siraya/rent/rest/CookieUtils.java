package org.siraya.rent.rest;
import org.siraya.rent.pojo.Session;
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
    private final static String KEY_NAME ="cookie";


    public NewCookie createDeviceCookie(Device device){

		String value= device.getId()+":"+device.getUserId();
		value = encodeUtility.encrypt(value, KEY_NAME);
		logger.debug("cookie value is "+value);
		NewCookie deviceCookie = new NewCookie("D", value, "/",
				null, 1, "no comment", 1073741823, // maxAge max int value/2
				false);
		return deviceCookie;
	}
	
    public  NewCookie newDeviceCookie(String Id){    	
    	String value= Id+":";
		value = encodeUtility.encrypt(value,KEY_NAME);
		logger.debug("cookie value is "+value);
		NewCookie deviceCookie = new NewCookie("D", value, "/",
				null, 1, "no comment", 1073741823 , // maxAge max int value/2
				false);
		return deviceCookie;	
	}
    
    /**
     * session cookie 
     *  id : ip : sha1(device id + user id)
     * @param id
     * @param ip
     * @return
     */
    public  NewCookie newSessionCookie(Session session){

    	String userId = session.getUserId();
    	String deviceId = session.getDeviceId();

    	if (userId == null || deviceId == null ) {
    		return null;
    	} else{
    		if (logger.isDebugEnabled()) {
        		logger.debug("user is "+userId);
        		logger.debug("device is "+deviceId);    			
    		}
    	}

		String value = encodeUtility.encrypt(session.toString(),
				KEY_NAME);
		logger.debug("cookie value is " + value);
		NewCookie sessionCookie = new NewCookie("S", value, "/", null, 1,
				"session",NewCookie.DEFAULT_MAX_AGE , false);
		return sessionCookie;
	}
    
    
    public  NewCookie removeDeviceCookie(){    	
		NewCookie deviceCookie = new NewCookie("D", "", "/",
				null, 1, "remove this cookie", -1, 
				false);
		return deviceCookie;	
	} 
    
    public  NewCookie removeSessionCookie(){    	
		NewCookie deviceCookie = new NewCookie("S", "", "/",
				null, 1, "remove this cookie", -1, 
				false);
		return deviceCookie;	
	} 
    
    public void extractSessionCookie(String cookieValue,UserAuthorizeData userAuthorizeData){
    	try{
    		cookieValue = encodeUtility.decrypt(cookieValue,KEY_NAME);
    	}catch (RentException e){
    		logger.error("decrypt session cookie fail");
    	} 
		try {

			Session session = new Session(cookieValue);
			if (!userAuthorizeData.getUserId().equals(session.getUserId())
					|| !userAuthorizeData.getDeviceId().equals(
							session.getDeviceId())) {
				logger.error("device id and user id not match");
		
			}
			userAuthorizeData.setSession(session);
    	}catch(Exception e){
    		logger.error("extract session fail ",e);
    	}
    }
    
    public void extractDeviceCookie(String deviceCookie,UserAuthorizeData userAuthorizeData){
    	try{
    		deviceCookie = encodeUtility.decrypt(deviceCookie,KEY_NAME);
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
	public void setEncodeUtility(EncodeUtility encodeUtility) {
		this.encodeUtility = encodeUtility;
	}
}
