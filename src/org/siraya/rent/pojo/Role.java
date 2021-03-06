package org.siraya.rent.pojo;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.siraya.rent.filter.UserRole.UserRoleId;
public class Role {
	private String userId;
	@JsonIgnore
	private UserRoleId role;
	public Role (String userId, UserRoleId role) {
		this.userId = userId;
		this.role = role;
	}
	public Role(){
		
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@JsonIgnore
	public UserRoleId getRole() {
		return role;
	}
	public void setRole(UserRoleId role) {
		this.role = role;
	}
	public void setRoleId(int role){
		this.role = UserRoleId.find(role);
	}
	public int getRoleId(){
		return role.getRoleId();
	}
}
