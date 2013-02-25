package org.siraya.rent.repl.service;
import java.sql.Connection;
import java.util.List;
import org.siraya.rent.pojo.*;
import org.siraya.rent.repl.dao.IQueueDao;
public interface ILocalQueueService {
	public void insert(Message message) throws Exception ;
	
	public List<Message> dump(int volumn)throws Exception;
		
	public QueueMeta getMeta() throws Exception;
		
	public IQueueDao getQueueDao();
	
	public void addEventListener(INewMessageEventListener listener);

	public void removeEventListener(INewMessageEventListener listener);

	public int getMaxEntity();
}
