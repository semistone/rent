package org.siraya.rent.filter;
import org.siraya.rent.pojo.Session;
import com.sun.jersey.spi.container.ContainerRequest;
public class UserAuthorizeData {
	private String userId;
	private String deviceId;
	private boolean isBrower = true;


	private ContainerRequest request;
	private Session session;
	private boolean isNewDevice = false;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public boolean isNewDevice() {
		return isNewDevice;
	}

	public void setNewDevice(boolean isNewDevice) {
		this.isNewDevice = isNewDevice;
	}

	public ContainerRequest getRequest() {
		return request;
	}

	public void setRequest(ContainerRequest request) {
		this.request = request;
	}
	
	public void signOff() {
		this.isNewDevice = true;
	}
	public boolean isBrower() {
		return isBrower;
	}

	public void setBrower(boolean isBrower) {
		this.isBrower = isBrower;
	}
}
