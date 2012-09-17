package org.siraya.rent.user.service;

import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.user.dao.IUserDAO;
public interface IMobileAuthService {

	public void sendAuthMessage(String deviceId) throws Exception;
	
	public void verifyAuthCode(String deviceId, String authCode) throws Exception;
	
	void setDeviceDao(IDeviceDao deviceDao);

	void setUserDao(IUserDAO userDao);
}
