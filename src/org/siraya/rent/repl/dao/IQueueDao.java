package org.siraya.rent.repl.dao;
import org.siraya.rent.pojo.Message;
import org.siraya.rent.pojo.QueueMeta;
import java.util.List;
import java.sql.Connection;
import org.siraya.rent.pojo.*;
public interface IQueueDao {
	
	public QueueMeta getMeta(Connection meta) throws Exception;
	
	public Connection initQueue(String queue) throws Exception;
	
	public Connection initVolumnFile(String queue, int volumn) throws Exception;

	public void resetVolumn(Connection connMeta, QueueMeta meta) throws Exception;
	
	public int insert(Connection connMeta, Connection volumn, QueueMeta meta, Message message) throws Exception;
	
	public List<Message> dump(Connection conn) throws Exception;
	
}
