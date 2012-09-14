package org.siraya.rent.user.service;

public enum UserStatus {
	Init(0),
	Authed(1),
	Remove(3);
	private int status;	
	
	
	private UserStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
}
