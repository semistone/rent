package org.siraya.rent.pojo;

import org.siraya.rent.filter.UserRole;
import org.siraya.rent.utils.EncodeUtility;

public class Session {
	private String id;
	private String session;
	private String deviceId;
	private String userId;
	private String lastLoginIp;
	private int roleId;
	private long created;
	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public void setDeviceVerified(boolean isDeviceVerified) {
		int deviceVerified = UserRole.UserRoleId.DEVICE_CONFIRMED.getRoleId();
		if (this.roleId <= deviceVerified) {
			this.roleId = deviceVerified;
		}
	}

	
    public void genId(){
    	this.id=java.util.UUID.randomUUID().toString();
    }
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public long getCreated() {
		if (created == 0) {
			created=java.util.Calendar.getInstance().getTimeInMillis()/1000;
		}
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	
	public String toString(){
    	if (id == null) {
    		this.genId();
    	}
    	String sign = EncodeUtility.sha1(deviceId+userId);
    	return id + ":" + lastLoginIp + ":" +sign;
	}
}
