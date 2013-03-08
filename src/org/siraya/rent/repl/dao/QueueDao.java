package org.siraya.rent.repl.dao;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.siraya.rent.pojo.Message;
import org.siraya.rent.pojo.QueueMeta;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.sql.*;
import java.util.*;

import org.siraya.rent.pojo.*;
/**
 * volumn file in $tmp/repl/$queue/volumnN.db meta file in
 * $tmp/repl/$queue/meta.db
 * 
 * not support multiple writer process.
 * 
 * @author angus_chen
 * 
 */
@Scope("prototype")
public class QueueDao implements IQueueDao,InitializingBean  {
	@Autowired
	private IApplicationConfig applicationConfig;
	private static Logger logger = LoggerFactory.getLogger(QueueDao.class);

	private String volHome;
	private Connection connMeta;
	private String queue;

	public void afterPropertiesSet() throws Exception {
		//
		// use sqlite as local storage
		//
		Class.forName("org.sqlite.JDBC");

		volHome = (String) applicationConfig.get("general").get("tmp_dir");
		if (volHome == null) {
			throw new java.lang.NullPointerException(
					"general/tmp_dir can't be null");
		}
		volHome += "/repl/";
	}

	public Connection  initVolumnFile(int volumn) throws Exception {
		Connection conn = createConnection(this.queue, volumn);
		this.initVolumnFile(conn);
		return conn;
	}

	void initVolumnFile(Connection conn) throws Exception {
		logger.debug("create volumn");
		conn.createStatement().execute("create table if not exists QUEUE_VOLUMN (ID INTEGER PRIMARY KEY ASC,CMD TEXT,CONTENT_TYPE TEXT, USER_ID TEXT,DATA TEXT, CREATED INTEGER)");
	}
	
	
	/**
	 * dump message.
	 * @param conn
	 * @param offset
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	public List<Message> dump(Connection conn, int offset, int limit) throws Exception{
		StringBuffer sb = new StringBuffer("select * from QUEUE_VOLUMN");
		sb.append(" limit " + limit);
		sb.append(" offset " + offset);
		ResultSet rs =conn.createStatement().executeQuery(sb.toString());
		ArrayList<Message> ret = new ArrayList<Message>();
		while(rs.next()) {
			Message msg = new Message();
			msg.setId(rs.getLong(1));
			msg.setCmd(rs.getString(2));
			msg.setContentType(rs.getString(3));
			msg.setUserId(rs.getString(4));
			msg.setData(rs.getBytes(5));				
			msg.setCreated(rs.getLong(6));
			ret.add(msg);
		}
		return ret;
			
	}
	
	
	public List<QueueMeta>getMetaList() throws Exception{
		ResultSet rs =connMeta.createStatement().executeQuery("select * from QUEUE_META");
		ArrayList<QueueMeta> ret = new ArrayList<QueueMeta>();
		while(rs.next()) {
			QueueMeta meta = new QueueMeta();
			meta.setId(rs.getString(1));
			meta.setVolumn(rs.getInt(2));
			meta.setLastRecord(rs.getInt(3));
			ret.add(meta);
		}
		return ret;			
	}
	
	
	public List<Message> dump(Connection conn) throws Exception{
		return this.dump(conn,0, 50000);
	}
	
	
	public void initQueue(String queue) throws Exception {
		if (volHome == null || queue == null) {
			this.afterPropertiesSet();
		}
		String volDir = volHome + queue;
		//
		// create queue dir
		//
		File queueDir = new File(volHome + queue);
		logger.debug("check "+queueDir);
		if (!queueDir.exists()) {
			queueDir.mkdirs();
		}

		if (!queueDir.isDirectory()) {
			throw new RentException(RentException.RentErrorCode.ErrorGeneral,
					volDir + " is not a directory");
		}
		//
		// find meta file
		//
		this.queue = queue;
		this.connMeta = createMetaConnection(queue);
		this.initMeta(connMeta);
		
		
	}
	
	void initMeta(Connection conn)throws Exception{
		//
		// if meta connection not setup yet, then use current connection.
		//
		if (this.connMeta == null) this.connMeta = conn;
		//
		// create meta table
		//
		logger.debug("create meta");
		String CREATE_META = "create table if not exists QUEUE_META (ID text PRIMARY KEY,VOLUMN int,LAST_RECORD int)";
		conn.createStatement().execute(CREATE_META);
		
	}

	public QueueMeta getMeta() throws Exception{
		return this.getMeta(this.queue);
	}
	
	public QueueMeta getMeta(String id) throws Exception{
		PreparedStatement ps1 = connMeta.prepareStatement("select * from QUEUE_META where id = ?");
		ps1.setString(1, id);		
		ResultSet rs= ps1.executeQuery();
		QueueMeta ret = new QueueMeta();
		ret.setId(id);
		if (rs.next()) {
			ret.setVolumn(rs.getInt(2));
			ret.setLastRecord(rs.getInt(3));			
			logger.info("meta from db, volumn is "+ret.getVolumn());
			logger.info("meta from db, last record is "+ret.getLastRecord());
		} else {
			logger.debug("insert meta reader record");
			PreparedStatement ps=connMeta.prepareStatement("insert into QUEUE_META values(?, ?, 0)");
			ps.setString(1, id);
			if (id.equals(queue)) { // if get queue's meta, then set volumn = 0
				ps.setInt(2, 0);
			} else {  // get volumn from writer
				ps.setInt(2, this.getMeta().getVolumn());								
			}
			ps.execute();
		}
		return ret;
	}
	
	private Connection createMetaConnection(String queue) throws Exception {
		String volDir = volHome + queue;
		String connString = "jdbc:sqlite:" + volDir + "/meta.db";
		logger.info("open connection to " + connString);
		Connection conn = DriverManager.getConnection(connString);
		conn.setAutoCommit(true);
		return conn;
	}

	private Connection createConnection(String queue, int volumn)
			throws Exception {
		String volDir = volHome + queue;

		String connString = "jdbc:sqlite:" + volDir + "/volumn" + volumn + ".db";
		logger.info("open connection to " + connString);
		Connection conn = DriverManager.getConnection(connString);
		conn.setAutoCommit(true);
		return conn;
	}


	/**
	 * 
	 * @return 1 success
	 */
	public int insert(Connection volumn, QueueMeta meta, Message message) throws Exception {
		logger.debug("update last record +1");
		PreparedStatement ps1= connMeta.prepareStatement("update QUEUE_META set LAST_RECORD=LAST_RECORD+1 where ID=?");
		ps1.setString(1, meta.getId());
		ps1.execute();
		logger.debug("insert last message");
		PreparedStatement ps = volumn.prepareStatement("insert into QUEUE_VOLUMN (CMD, CONTENT_TYPE,USER_ID,DATA,  CREATED) values (?, ?, ?, ?, ?)");
		ps.setString(1, message.getCmd());
		ps.setString(2, message.getContentType());
		ps.setString(3, message.getUserId());
		if (!message.isBinary()) {
			ps.setString(4, message.getStringData());			
		} else {
			ps.setBytes(4, message.getData());
		}
		ps.setLong(5, message.getCreated());
		ps.executeUpdate();
		int id ;
		ResultSet rs = ps.getGeneratedKeys();
		rs.next();
		id = rs.getInt(1);		
		return id;
	}

	public void resetVolumn(QueueMeta meta) throws Exception{
		logger.info("reset meta");
		PreparedStatement ps= connMeta.prepareStatement("update QUEUE_META  set LAST_RECORD = 0, VOLUMN = VOLUMN+1 where ID=?");
		ps.setString(1, meta.getId());
		ps.execute();

	}
	
	public void updateReaderMeta(QueueMeta meta) throws Exception{
		logger.debug("update reader meta last record:"+meta.getLastRecord()+" id:"+meta.getId());
		String UPDATE = "update QUEUE_META set VOLUMN=? , LAST_RECORD=? where ID=? ";
		PreparedStatement ps = connMeta.prepareStatement(UPDATE);
		ps.setInt(1, meta.getVolumn());
		ps.setInt(2, meta.getLastRecord());
		ps.setString(3, meta.getId());
		ps.execute();
	}
	
	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}
	
	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

}
