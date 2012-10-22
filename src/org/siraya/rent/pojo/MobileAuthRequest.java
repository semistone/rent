package org.siraya.rent.pojo;

import java.util.Random;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Design mobile auth request form.
 * 
 * 
 * @author User
 * 
 */
public class MobileAuthRequest extends MobileAuthResponse{
	@NotNull
	@Pattern(regexp = "^[\\d]{1,3}$")
	private String countryCode;

	@NotNull
	private String sign;
	private String done;
	private String mobilePhone;
	private String authUserId;
	private boolean forceReauth;
	private String callback;
	@NotNull
	private String requestFrom;
	@NotNull
	@Size(min = 2, max = 16)
	private String requestId;
	@NotNull
	private long requestTime;
	
	private String token;
	private String authCode;
	
	public void setAuthCode(String authCode){
		this.authCode = authCode;
	}
	public String getAuthCode() {
		return this.authCode;
	}
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public long getRequestTime() {
		if (requestTime == 0) {
			requestTime = java.util.Calendar.getInstance().getTimeInMillis()/1000;
		}
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

	public String toString(String token) {
		return String.format("%s:%s:%b:%d:%b:%b:%b:%b:%b", this.requestId,
				this.requestFrom, this.done, this.requestTime,
				this.mobilePhone, this.forceReauth, this.callback,
				this.authUserId, token);
	}
	
	@JsonIgnore
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	public String genToken(){
		Random r  = new Random();
		this.token= String.format("%06d", r.nextInt(999999));
		return this.token;
	}
}
