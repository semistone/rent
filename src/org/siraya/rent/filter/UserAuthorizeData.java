package org.siraya.rent.filter;
import com.sun.jersey.spi.container.ContainerRequest;
public class UserAuthorizeData {
	private String userId;
	private String deviceId;
	public String username ="test user";
	public String role;
	public ContainerRequest request;

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
		System.out.println("xxxxxxx");
		//B0559903B6F1A374BA0F1AEC8CBC6C6B287395728F6D5E4636ADFD01DB2E8392080A51D91093A2D22EF5F0829485DCC7D5B284E3DA1D83343BC86391F3C868C6
		if ("57cea91b-f8ab-4829-a858-952cf0e5747e".equals(userId)) {
			this.role = "admin";
		} else {
			this.role = "test_role";
		}
	}
	public String getRole(){
		return this.role;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}
