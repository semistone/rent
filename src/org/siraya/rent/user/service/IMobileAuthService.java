package org.siraya.rent.user.service;

public interface IMobileAuthService {

	public void sendAuthMessage(String deviceId,String userId) throws Exception;
	
	public void verifyAuthCode(String deviceId,String userId,String authCode) throws Exception;
	
	public void verifyAuthCodeByMobilePhone(String mobilePhone,String authCode);
	
}
