package org.siraya.rent.user.service;
/**
 * Basic user service.
 * 
 * 
 */
import org.siraya.rent.pojo.User;
import org.siraya.rent.pojo.Device;
public interface IUserService {
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
	public void newDevice(Device device) throws Exception;

	/**
	 * verify user
	 * @param deviceId
	 * @param authCode
	 */
	public void verifyMobile(String deviceId, String authCode);

	/**
	 * update user email
	 * @param userId
	 * @param email
	 */
	public void updateEmail(String userId, String email);
	
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
}
