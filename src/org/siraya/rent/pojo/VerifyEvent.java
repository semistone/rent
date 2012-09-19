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
		if (created == 0) {
			created=java.util.Calendar.getInstance().getTimeInMillis()/1000;
			modified= created;
		}		
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public long getModified() {
		if (modified == 0) {
			modified=java.util.Calendar.getInstance().getTimeInMillis()/1000;
		}
		return modified;
	}
	public void setModified(long modified) {
		this.modified = modified;
	}

	public enum VerifyType {
		Email(0);
		private int type;	
		
		private VerifyType(int type) {
			this.type = type;
		}
		
		public int getType() {
			return type;
		}
	}	
	
	public enum VerifyStatus {
		Init(0),
		Authing(1),
		Authed(2);
		private int status;	
		
		private VerifyStatus(int status) {
			this.status = status;
		}
		
		public int getStatus() {
			return status;
		}
	}	
}
