package org.siraya.rent.pojo;

import org.codehaus.jackson.annotate.JsonIgnore;

public class MobileAuthResponse {
	private String requestId;
	private int status;
	private long responseTime;
	private String sign;
	private String userId;

	@JsonIgnore
	private User user;
	@JsonIgnore
	private Device device;

	public String getRequestId() {
		return requestId;
	}
<<<<<<< HEAD
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getResponseTime() {
		if (responseTime == 0) {
			responseTime=java.util.Calendar.getInstance().getTimeInMillis()/1000;
		}
		return responseTime;
	}
	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}
	public String getSign() {
		return sign;
	}
=======

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getResponseTime() {
		if (responseTime == 0) {
			responseTime = java.util.Calendar.getInstance().getTimeInMillis() / 1000;
		}
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public String getSign() {
		return sign;
	}

>>>>>>> master
	public void setSign(String sign) {
		this.sign = sign;
	}

<<<<<<< HEAD
	public String toString(String token){
		return String.format("%s:%s:%d:%d:%s", 
				this.requestId, this.userId, this.responseTime,this.status,token);
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
=======
	public String toString(String token) {
		return String.format("%s:%s:%d:%d:%s", this.requestId, this.userId,
				this.responseTime, this.status, token);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

>>>>>>> master
	@JsonIgnore
	public User getUser() {
		return user;
	}
<<<<<<< HEAD
=======

>>>>>>> master
	public void setUser(User user) {
		this.userId = user.getId();
		this.user = user;
	}
<<<<<<< HEAD
	public Device getDevice() {
		return device;
	}
=======

	public Device getDevice() {
		return device;
	}

>>>>>>> master
	public void setDevice(Device device) {
		this.device = device;
	}
}
