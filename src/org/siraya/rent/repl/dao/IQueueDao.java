package org.siraya.rent.repl.dao;
import org.siraya.rent.pojo.Message;
import org.siraya.rent.pojo.QueueMeta;
import java.util.List;
import java.sql.Connection;
import org.siraya.rent.pojo.*;
public interface IQueueDao {
	
	public void initQueue(String queue) throws Exception;
	
	public Connection initVolumnFile(int volumn) throws Exception;

	public void resetVolumn(QueueMeta meta) throws Exception;
	
	public int insert(Connection volumn, QueueMeta meta, Message message) throws Exception;
	
	public List<Message> dump(Connection conn) throws Exception;

	public List<Message> dump(Connection conn, int offset, int limit) throws Exception;
	
	public QueueMeta getMeta(String id ) throws Exception;
	
	public QueueMeta getMeta() throws Exception;
	
	public void updateReaderMeta(QueueMeta meta) throws Exception;
	
}
