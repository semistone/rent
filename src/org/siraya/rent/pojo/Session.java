package org.siraya.rent.pojo;

import org.siraya.rent.filter.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
public class Session {
	private static Logger logger = LoggerFactory.getLogger(Session.class);
	private String id;
	private String session;
	private String deviceId;
	private String userId;
	private String lastLoginIp;
	private List<Integer> roles;
	private long created;
	private boolean isChange = false;
	private boolean isNew = false;
	public boolean isChange() {
		return isChange;
	}
	public Session(){
		this.isNew = true;
		this.isChange = true;
		this.roles = new java.util.ArrayList<Integer>();
	}
	public boolean isNew() {
		return isNew;
	}
	public List<Integer> getRoles() {
		return roles;
	}
	public void addRole(int role){
		this.roles.add(role);
	}
	
	public Session(String cookieValue){
		String[] strings = cookieValue.split(":");
		this.id = strings[0];
		this.deviceId = strings[1];
		this.userId = strings[2];
		this.lastLoginIp = strings[3];
		String rolesString = null;
		if (strings.length == 5) {
			rolesString = strings[4];
		}
		this.roles = new  java.util.ArrayList<Integer>();
		if (rolesString != null) {
			String[] roleArray = rolesString.split(" ");
			int size = roleArray.length;
			for(int i =0 ; i < size ; i++) {
				this.roles.add(Integer.parseInt(roleArray[i]));
			}			
		}
	}

	public void setDeviceVerified(boolean isDeviceVerified) {
		int deviceConfirmed = UserRole.UserRoleId.DEVICE_CONFIRMED.getRoleId();
		if (!this.roles.contains(deviceConfirmed)) {
			logger.debug("add role device confirmed");
			this.roles.add(deviceConfirmed);
			this.isChange = true;
		}
	}

	public boolean isUserInRole(int roleId) {
		return this.roles.contains(roleId);
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
    	String rolesString = "";
    	int size = this.roles.size();
    	for(int i = 0; i < size ; i++) {
    		rolesString+=roles.get(i)+" ";
    	}
    	return this.id + ":" + this.deviceId +":"+this.userId+":"+ this.lastLoginIp+":"+rolesString.trim();
	}
}
