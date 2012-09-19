package org.siraya.rent.pojo;

public class VerifyEvent {
	private long id;
	private String userId;
	private int status;
	private int verifyType;
	private String verifyDetail;
	private long created;
	private long modified;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getVerifyType() {
		return verifyType;
	}
	public void setVerifyType(int verifyType) {
		this.verifyType = verifyType;
	}
	public String getVerifyDetail() {
		return verifyDetail;
	}
	public void setVerifyDetail(String verifyDetail) {
		this.verifyDetail = verifyDetail;
	}
	public long getCreated() {
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public long getModified() {
		return modified;
	}
	public void setModified(long modified) {
		this.modified = modified;
	}

	
}
