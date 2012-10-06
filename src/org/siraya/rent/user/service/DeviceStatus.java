package org.siraya.rent.user.service;

public enum DeviceStatus {
	Init(0),
	Authing(1),
	Authed(2),
	Removed(3),
	Suspend(4),
	ApiKeyOnly(5);

	private int status;	
	
	
	private DeviceStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
}
