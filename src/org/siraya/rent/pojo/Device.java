<<<<<<< HEAD
package org.siraya.rent.pojo;
import java.util.Random;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.siraya.rent.pojo.User;
import org.codehaus.jackson.annotate.JsonIgnore;
public class Device {

	@NotNull
	@Size(min = 2, max = 15)
	private String id;

	@Size(min = 2, max = 15)
	private String userId;
	@NotNull
	private String token;
	private long created;
	private long modified;
	
	private int status;
	
	@Size(min = 2, max = 15)
	private String name;
	private int authRetry;

	private String lastLoginIp;
	private String lastLoginTime;
	private User user;
    public static String ENCRYPT_KEY = "general";
    public Device(){
    	
    }
    public Device(String id, String userId) {
    	this.id= id;
    	this.userId = userId;
    }
	public String genToken(){
		Random r  = new Random();
		this.token = String.format("%06d", r.nextInt(999999));
		return this.token;
	}
	public static String genId(){
		return java.util.UUID.randomUUID().toString();
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
	
	@JsonIgnore
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public String getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public int getAuthRetry() {
		return authRetry;
	}
	public void setAuthRetry(int authRetry) {
		this.authRetry = authRetry;
	}	
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
		this.userId = user.getId();
	}
}

=======
package org.siraya.rent.pojo;
import java.util.Random;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.siraya.rent.pojo.User;
import org.codehaus.jackson.annotate.JsonIgnore;
public class Device {

	@NotNull
	@Size(min = 2, max = 15)
	private String id;

	@Size(min = 2, max = 15)
	private String userId;
	@NotNull
	private String token;
	private long created;
	private long modified;
	
	private int status;
	
	@Size(min = 2, max = 15)
	private String name;
	private int authRetry;

	private String lastLoginIp;
	private String lastLoginTime;
	private User user;
    public static String ENCRYPT_KEY = "general";
    public Device(){
    	
    }
    public Device(String id, String userId) {
    	this.id= id;
    	this.userId = userId;
    }
	public String genToken(){
		Random r  = new Random();
		this.token = String.format("%06d", r.nextInt(999999));
		return this.token;
	}
	public static String genId(){
		return java.util.UUID.randomUUID().toString();
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
	
	@JsonIgnore
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public String getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public int getAuthRetry() {
		return authRetry;
	}
	public void setAuthRetry(int authRetry) {
		this.authRetry = authRetry;
	}	
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
		if (user == null) {
			this.userId = null;
		} else {
			this.userId = user.getId();			
		}
	}
}

>>>>>>> master
