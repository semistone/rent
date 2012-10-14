package org.siraya.rent.pojo;


/**
 *  Design mobile auth request form.
 * 
 * 
 * @author User
 *
 */
public class MobileAuthRequest {
	private String sign;
	private String done;
	private String mobilePhone;
	private String authUserId;
	private boolean forceReauth;
	private String callback;

	private String requestFrom;
	private String requestId;
	private long requestTime;
	
	public long getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getDone() {
		return done;
	}
	public void setDone(String dotDone) {
		this.done = dotDone;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getAuthUserId() {
		return authUserId;
	}
	public void setAuthUserId(String authUser) {
		this.authUserId = authUser;
	}
	public boolean isForceReauth() {
		return forceReauth;
	}
	public void setForceReauth(boolean forceReauth) {
		this.forceReauth = forceReauth;
	}
	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getRequestFrom() {
		return requestFrom;
	}
	public void setRequestFrom(String requestFrom) {
		this.requestFrom = requestFrom;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String toString(String token){
		return String.format("%s:%s:%b:%d:%b:%b:%b:%b:%b", 
				this.requestId, this.requestFrom,
				this.done, this.requestTime,this.mobilePhone,this.forceReauth,this.callback,
				this.authUserId,token);
	}
}
