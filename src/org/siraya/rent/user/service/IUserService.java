package org.siraya.rent.user.service;
/**
 * Basic user service.
 * 
 * 
 */
import java.util.List;

import org.siraya.rent.pojo.User;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.user.dao.IDeviceDao;
import org.siraya.rent.pojo.MobileAuthRequest;
public interface IUserService {
	
	/**
	 * remove device
	 * @param device
	 */
	public void removeDevice(Device device) throws Exception;
	
	/**
	 * get device 
	 * @param device
	 */
	 public Device getDevice(Device device);
	/**
	 * add new user by mobile number.
	 * create one user, must unique mobile number.
	 * @param phoneNumber
	 * @return String userId
	 */
	public User newUserByMobileNumber(int cc,String phoneNumber) throws Exception;

	/**
	 * add new user device.
	 * @param device
	 * @return
	 */
	public Device newDevice(Device device) throws Exception;

	public List<Device> getUserDevices(String userId, int limit, int offset);
	
	public List<User> getDeviceUsers(String deviceId, int limit, int offset);
	 
	public Device mobileAuthRequest(MobileAuthRequest request);
	/**
	 * update user email
	 * @param userId
	 * @param email
	 */
	public void setupEmail(User user) throws Exception;
	
	/**
	 * 
	 * @param loginId
	 * @param password
	 */
	public void updateLoginIdAndPassowrd(User user) throws Exception;
	/**
	 * verify email.
	 * @param userId
	 * @param authCode
	 */
	public void verifyEmail(String userId, String authCode);

	/**
	 * update other user information.
	 * @param user
	 */
	public void updateUserInfo(User user);
	
	public void nameDevice(Device device);
	
	public void setUserDao(IUserDAO dao);

	public void setDeviceDao(IDeviceDao dao);
}
