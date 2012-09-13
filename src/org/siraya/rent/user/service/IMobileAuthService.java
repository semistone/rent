package org.siraya.rent.user.service;

import org.siraya.rent.pojo.Device;
public interface IMobileAuthService {

	public void sendAuthMessage(Device device) throws Exception;
	
	public void verifyAuthCode(String deviceId, String authCode);
}
