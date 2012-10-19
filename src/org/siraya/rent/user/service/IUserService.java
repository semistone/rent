package org.siraya.rent.user.service;
/**
 * Basic user service.
 * 
 * 
 */
import java.util.List;
import org.siraya.rent.pojo.MobileAuthResponse;
import org.siraya.rent.pojo.User;
import org.siraya.rent.pojo.Device;
import org.siraya.rent.pojo.MobileAuthRequest;
public interface IUserService {
    public final static String SSO_DEVICE_ID = "SSO";

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
	public User newUserByMobileNumber(int cc,String phoneNumber);

	/**
	 * add new user device.
	 * @param device
	 * @return
	 */
	public Device newDevice(Device device) throws Exception;

	public List<Device> getUserDevices(String userId, int limit, int offset);
	
	public List<User> getDeviceUsers(String deviceId, int limit, int offset);
	 
	public List<Device> getSsoDevices();

	public String getSignatureOfMobileAuthRequest(MobileAuthRequest request);
	
	public MobileAuthResponse mobileAuthRequest(Device currentDevice, MobileAuthRequest request);
	/**
	 * update user email
	 * @param userId
	 * @param email
	 */
	public void setupEmail(User user) throws Exception;
	
	public MobileAuthRequest getMobileAuthRequest(String requestId);
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
	
}
