package org.siraya.rent.user.service;

import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.user.dao.IUserDAO;
public interface IMobileAuthService {

	public void sendAuthMessage(String deviceId,String userId) throws Exception;
	
	public void verifyAuthCode(String deviceId,String userId,String authCode) throws Exception;
	
	void setDeviceDao(IDeviceDao deviceDao);

	void setUserDao(IUserDAO userDao);
}
