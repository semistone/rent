package org.siraya.rent.repl.service;

import java.util.List;
import org.siraya.rent.pojo.*;
public interface ILocalQueueService {
	public void insert(Message message) throws Exception ;
	
	public List<Message> dump(int volumn)throws Exception;
	
	
	public QueueMeta getMeta() throws Exception;
}
