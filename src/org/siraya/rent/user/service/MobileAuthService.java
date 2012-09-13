package org.siraya.rent.user.service;
import org.siraya.rent.mobile.service.IMobileGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Random;
@Service("mobileAuthService")
public class MobileAuthService implements IMobileAuthService {
    @Autowired
    private IMobileGatewayService mobileGatewayService;
    @Autowired
    private IApplicationConfig applicationConfig;
    private static Logger logger = LoggerFactory.getLogger(MobileAuthService.class);
	/**
	 * 1. check device status to prevent duplicate auth.
	 * 2. set auth code by random from 0 - 9999
	 * 3. send message.
	 */
	public void sendAuthMessage(Device device) throws Exception{
		if (device.getStatus() != DeviceStatus.Init.getStatus()) {
			throw new Exception("device status isn't init");
		}
		int retryLimit = (Integer)applicationConfig.get("general").get("auth_retry_limit");
		if (device.getAuthRetry() > retryLimit) {
			throw new Exception("auth retry too many time");
		}
		User user = device.getUser();
		if (user == null) {
			throw new java.lang.NullPointerException("user can't be null");
		}
		
		//
		// prepare message.
		//
		Random r = new Random();
		device.setToken(String.valueOf(r.nextInt(9999)));
		String phone=user.getMobilePhone();
	    ResourceBundle resource = ResourceBundle.getBundle("user",user.getLocale());
		String message = MessageFormat.format(resource.getString("mobile_auth_message"),
				device.getId(),device.getToken());
		logger.debug("message is "+message);
		//
		// send message through gateway.
		//
		mobileGatewayService.sendSMS(phone, message);
		device.setAuthRetry(device.getAuthRetry()+1);
		
	}

	@Override
	public void verifyAuthCode(String deviceId, String authCode) {
		// TODO Auto-generated method stub

	}

}
