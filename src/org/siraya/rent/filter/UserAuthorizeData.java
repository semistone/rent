package org.siraya.rent.filter;
import org.siraya.rent.pojo.Session;
import com.sun.jersey.spi.container.ContainerRequest;
public class UserAuthorizeData {
	private String userId;
	private String deviceId;

	public ContainerRequest request;
	public Session session;
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

}
