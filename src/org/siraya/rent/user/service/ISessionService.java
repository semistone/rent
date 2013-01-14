package org.siraya.rent.user.service;
<<<<<<< HEAD
import org.siraya.rent.pojo.Role;
import org.siraya.rent.pojo.Session;
=======
import org.siraya.rent.pojo.*;
>>>>>>> master
import java.util.List;
import org.siraya.rent.pojo.Device;
public interface ISessionService {

	public void newSession(Session session);
	
	public List<Session> getSessions(Device device,int limit ,int offset);

	public List<Role> getRoles(String userId);
<<<<<<< HEAD
=======
	
	public void connect(Session session);

	public void disconnect(Session session);

    public List<String> callbacks(String userId);

    public void callbackReset(String callback);
    
    public List<UserOnlineStatus> list(List<String> ids);
>>>>>>> master
}
