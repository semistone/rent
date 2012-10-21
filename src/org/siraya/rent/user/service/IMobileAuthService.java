package org.siraya.rent.user.service;
import org.siraya.rent.pojo.MobileAuthRequest;
import org.siraya.rent.pojo.MobileAuthResponse;
public interface IMobileAuthService {

	public void sendAuthMessage(String deviceId,String userId);
	
	public void sendAuthMessage(MobileAuthRequest request,MobileAuthResponse response);
	
	public void verifyAuthCode(String deviceId,String userId,String authCode) throws Exception;
	
	public void verifyAuthCodeByMobilePhone(String mobilePhone,String authCode);
	
	public MobileAuthResponse verifyMobileAuthRequestCode(MobileAuthRequest request);
	
}
