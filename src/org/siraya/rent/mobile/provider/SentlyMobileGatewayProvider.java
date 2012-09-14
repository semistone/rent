package org.siraya.rent.mobile.provider;

import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.siraya.rent.utils.IApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.client.RestClientException;
public class SentlyMobileGatewayProvider implements IMobileGatewayService{
    @Autowired
    private IApplicationConfig applicationConfig;
    private RestTemplate restTemplate;
    
    public SentlyMobileGatewayProvider(RestTemplate restTemplate){
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
		String requestUri="http://sent.ly/command/sendsms?username={username}&password={password}&text={text}&to={to}";
		Map<String, String> vars=new <String, String>HashMap();
		vars.put("username", (String)setting.get("username"));
		vars.put("password", (String)setting.get("password"));
		vars.put("to", to);
		vars.put("text", message);
		String result = restTemplate.getForObject(requestUri, String.class,vars);
		if(result.startsWith("Error")){
			throw new RestClientException("send sms fail "+result);
		}
	}
}
