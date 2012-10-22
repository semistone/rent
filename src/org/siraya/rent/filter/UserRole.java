package org.siraya.rent.filter;

public class UserRole {
	public enum UserRoleId{
		ANONYMOUS(0),
		DEVICE_CONFIRMED(1),
		LOGINED(2),
		ADMIN(3),
		ROOT(4);
		private int id;
		private UserRoleId(int id){
			this.id = id;
		}
		public int getRoleId(){
			return id;
		}

		public static UserRoleId find(int role) {
			switch (role) {
			case 0:
				return ANONYMOUS;
			case 1:
				return DEVICE_CONFIRMED;
			case 2:
				return LOGINED;
			case 3:
				return ADMIN;
			case 4:
				return ROOT;
			default:
				return null;
			}
		}

	};
	public final static String ANONYMOUS = "anonymous";
	public final static String DEVICE_CONFIRMED = "deviceConfirmed";
	public final static String LOGINED = "logined";
	public final static String ADMIN = "admin";
	public final static String ROOT = "root";
	public static int getRoleId(String role){
		if (role.equals(ANONYMOUS)) {
			return 0;
		} else if (role.equals(DEVICE_CONFIRMED)) {
			return 1;
		} else if (role.equals(LOGINED)) {
			return 2;
		} else if (role.equals(ADMIN)) {
			return 3;
		} else if (role.equals(ROOT)) {
			return 4;
		} else {
			return 0;
		}
	}

}
