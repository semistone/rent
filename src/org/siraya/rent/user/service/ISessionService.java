package org.siraya.rent.user.service;
import org.siraya.rent.pojo.Session;
import java.util.List;
import org.siraya.rent.pojo.Device;
public interface ISessionService {

	public void newSession(Session session);
	
	public List<Session> getSessions(Device device,int limit ,int offset);
}
