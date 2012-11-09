package org.siraya.rent.pojo;

public class Member {
	private String id;

	private String name;
	private String fbAccount;
	private String userId;
	private String memberUserId;
	private long created;
	private long modified;
	private String memberId;
	
	public String genId(){
		this.id = java.util.UUID.randomUUID().toString();
		return id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMemberUserId() {
		return memberUserId;
	}
	public void setMemberUserId(String memberUserId) {
		this.memberUserId = memberUserId;
	}
	public long getCreated() {
		return created;
	}
	public void setCreated(long created) {
		if (created == 0) {
			created=java.util.Calendar.getInstance().getTimeInMillis()/1000;
			modified= created;
		}
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
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFbAccount() {
		return fbAccount;
	}
	public void setFbAccount(String fbAccount) {
		this.fbAccount = fbAccount;
	}
}
