package org.siraya.rent.user.service;
/**
 * Basic user service.
 * 
 * 
 */
import org.siraya.rent.pojo.User;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.user.dao.IUserDAO;
import org.siraya.rent.user.dao.IDeviceDao;
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
