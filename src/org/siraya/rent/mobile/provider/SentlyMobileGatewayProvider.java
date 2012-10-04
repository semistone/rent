package org.siraya.rent.mobile.provider;

import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.HashMap;
import org.siraya.rent.utils.RentException.RentErrorCode;
import org.springframework.web.client.RestOperations;;
public class SentlyMobileGatewayProvider implements IMobileGatewayService{
    @Autowired
    protected IApplicationConfig applicationConfig;
    private RestOperations restTemplate;
	static String REQUEST_URI = "http://sent.ly/command/sendsms?username={username}&password={password}&text={text}&to={to}";

    public SentlyMobileGatewayProvider(RestOperations restTemplate){
    	this.restTemplate=restTemplate;
    }
    private static Logger logger = LoggerFactory.getLogger(SentlyMobileGatewayProvider.class);

    /**
     * send SMS message.
     * 
     */
	public void sendSMS(String to,String message) throws Exception{
		Map<String,Object> setting=applicationConfig.get("sently");
		logger.info("send sms to "+to+"\nmeesage:"+message+"\n");

		if (setting.get("debug").equals(true) ) {
			logger.info("send sms in debug mode don't send anything");
			return;
		}
		Map<String, String> vars=new HashMap<String, String>();
		vars.put("username", (String)setting.get("username"));
		vars.put("password", (String)setting.get("password"));
		vars.put("to", to);
		vars.put("text", message);
		String result = restTemplate.getForObject(REQUEST_URI, String.class,vars);
		if (result.startsWith("Error:")) {
			logger.debug("sently result is "+result);
			int code = Integer.parseInt(result.substring(6, 7));
			throw new RentException(RentErrorCode.ErrorMobileGateway,"send sms fail "+this.errorMsg(code));
		}
	}
	
	/**
	 * translate error code base on 
	 * https://docs.google.com/document/d/1MuFXPTWq7zNIChwZdzIrqiQ2-Mb3_rPrWqNlOZXJNws/edit?pli=1
	 * @param code
	 * @return
	 */
	private String errorMsg(int code){
		switch(code){
		case 0:
			return "Authentication Error";
		case 1:
			return "Malformed Parameters";
		case 3:
			return "No device to send SMS";
		case 4:
			return "No credits";
		default:
			return "unknwon error code "+code;

		}
	}
}
