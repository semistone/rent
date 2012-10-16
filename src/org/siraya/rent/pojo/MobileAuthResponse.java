package org.siraya.rent.pojo;

public class MobileAuthResponse {
	private String requestId;
	private int status;
	private long responseTime;
	private String sign;
	public String getRequestId() {
		return requestId;
	}
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
		return responseTime;
	}
	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}

	public String toString(String token){
		return String.format("%s:%d:%d:%s", 
				this.requestId, this.responseTime,this.status,token);
	}
}
