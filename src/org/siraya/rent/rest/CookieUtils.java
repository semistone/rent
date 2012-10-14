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

    public String genId(){
    	return java.util.UUID.randomUUID().toString();
    }
    
    public NewCookie createDeviceCookie(Device device){

		String value= device.getId()+":"+device.getUserId();
		value = encodeUtility.encrypt(value, KEY_NAME);
		logger.debug("cookie value is "+value);
		NewCookie deviceCookie = new NewCookie("D", value, "/",
				null, 1, "no comment", NewCookie.DEFAULT_MAX_AGE, // maxAge max int value/2
				false);
		return deviceCookie;
	}
	
    public  NewCookie newDeviceCookie(String Id){    	
    	String value= Id+":";
		value = encodeUtility.encrypt(value,KEY_NAME);
		logger.debug("cookie value is "+value);
		NewCookie deviceCookie = new NewCookie("D", value, "/",
				null, 1, "no comment", NewCookie.DEFAULT_MAX_AGE , // maxAge max int value/2
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
    	String id = session.getId();
    	String ip = session.getLastLoginIp();
    	String userId = session.getUserId();
    	String deviceId = session.getDeviceId();
    	String sign = EncodeUtility.sha1(deviceId+userId);
    	if (id == null) {
    		id = this.genId();
    		session.setSession(id);
    	}

    	if (userId == null || deviceId == null ) {
    		return null;
    	} else{
    		logger.debug("user is "+userId);
    		logger.debug("device is "+deviceId);
    	}
    	String value = id + ":" + ip + ":" +sign;
		value = encodeUtility.encrypt(value, KEY_NAME);
		logger.debug("cookie value is " + value);
		NewCookie sessionCookie = new NewCookie("S", value, "/", null, 1,
				"session", 60 * 60 * 24, false);
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
    
    public Session extractSessionCookie(String cookieValue,UserAuthorizeData userAuthorizeData){
    	try{
    		cookieValue = encodeUtility.decrypt(cookieValue,KEY_NAME);
    	}catch (RentException e){
    		logger.error("decrypt session cookie fail");
    		return null;
    	} 
		try {
			logger.debug("cookie value is "+cookieValue);
			String[] strings = cookieValue.split(":");
			Session session = new Session();
			session.setId(strings[0]);
			session.setLastLoginIp(strings[1]);
			String sign = EncodeUtility.sha1(userAuthorizeData.getDeviceId()
					+ userAuthorizeData.getUserId());
			if (!sign.equals(strings[2])) {
				logger.error("device id and user id not match");
				return null;
			}
			return session;
    	}catch(Exception e){
    		logger.error("extract session fail ",e);
    		return null;
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
