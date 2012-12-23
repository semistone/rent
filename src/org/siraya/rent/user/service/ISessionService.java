package org.siraya.rent.user.service;
import org.siraya.rent.pojo.*;
import java.util.List;
import org.siraya.rent.pojo.Device;
public interface ISessionService {

	public void newSession(Session session);
	
	public List<Session> getSessions(Device device,int limit ,int offset);

	public List<Role> getRoles(String userId);
	
	public void connect(Session session);

	public void disconnect(Session session);

    public List<String> callbacks(String userId);

    public List<UserOnlineStatus> list(String[] ids);
}
