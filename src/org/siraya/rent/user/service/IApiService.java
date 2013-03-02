package org.siraya.rent.user.service;

import org.siraya.rent.pojo.Device;
import org.siraya.rent.pojo.Session;
public interface IApiService {
	public Device apply(String userId,String name);
	
	public void requestSession(Session session, String authData, long timestamp);
}
