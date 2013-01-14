package org.siraya.rent.pojo;

import javax.validation.constraints.NotNull;

public class MobileProvider {

	@NotNull
	private String id;
	@NotNull
	private String user;
	@NotNull
	private String password;
	private long created;
	private long modified;
	
	private String type;
    public static String ENCRYPT_KEY = "general";
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	public long getModified() {
		if (modified == 0) {
			modified=java.util.Calendar.getInstance().getTimeInMillis()/1000;
		}
		return modified;
	}
	public void setModified(long modified) {
		this.modified = modified;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
