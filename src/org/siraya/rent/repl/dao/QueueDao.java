package org.siraya.rent.repl.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
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
@Repository("queueDao")
public class QueueDao implements IQueueDao {
	@Autowired
	private IApplicationConfig applicationConfig;
	private static Logger logger = LoggerFactory.getLogger(QueueDao.class);

	private String volHome;

	public QueueDao() {

	}

	public void init() throws Exception {
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

	public Connection  initVolumnFile(String queue, int volumn) throws Exception {
		Connection conn = createConnection(queue, volumn);
		this.initVolumnFile(conn);
		return conn;
	}

	void initVolumnFile(Connection conn) throws Exception {
		logger.debug("create volumn");
		conn.createStatement().execute("create table if not exists QUEUE_VOLUMN (ID INTEGER PRIMARY KEY ASC,CMD TEXT,USER_ID TEXT,DATA TEXT, CREATED INTEGER)");

	}
	public QueueMeta getMeta(Connection conn) throws Exception{
		ResultSet rs=conn.createStatement().executeQuery("select * from QUEUE_META");
		QueueMeta ret = new QueueMeta();
		if (rs.next()) {
			ret.setVolumn(rs.getInt(1));
			ret.setLastRecord(rs.getInt(2));
			logger.info("meta from db, volumn is "+ret.getVolumn());
			logger.info("meta from db, last record is "+ret.getLastRecord());
		} else {
			logger.debug("insert meta record");
			conn.createStatement().execute("insert into QUEUE_META values(0,0)");
		}
		return ret;
	}
	
	public List<Message> dump(Connection conn) throws Exception{
		ResultSet rs =conn.createStatement().executeQuery("select * from QUEUE_VOLUMN");
		ArrayList<Message> ret = new ArrayList<Message>();
		while(rs.next()) {
			Message msg = new Message();
			msg.setId(rs.getLong(1));
			msg.setCmd(rs.getString(2));
			msg.setUserId(rs.getString(3));
			msg.setData(rs.getBytes(4));
			msg.setCreated(rs.getLong(5));
			ret.add(msg);
		}
		return ret;
		
	}
	public Connection initQueue(String queue) throws Exception {
		if (volHome == null || queue == null) {
			this.init();
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
		Connection conn = createMetaConnection(queue);
		this.initMeta(conn);
		return conn;

	}
	
	void initMeta(Connection conn)throws Exception{
		
		//
		// create meta table
		//
		logger.debug("create meta");
		String CREATE_META = "create table if not exists QUEUE_META (VOLUMN int,LAST_RECORD int)";
		conn.createStatement().execute(CREATE_META);
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
	public int insert(Connection connMeta, Connection volumn, QueueMeta meta, Message message) throws Exception {
		logger.debug("update last record +1");
		connMeta.createStatement().execute("update QUEUE_META set LAST_RECORD=LAST_RECORD+1");
		logger.debug("insert last message");
		PreparedStatement ps = volumn.prepareStatement("insert into QUEUE_VOLUMN (CMD, USER_ID,DATA,  CREATED) values (?, ?, ?, ?)");
		ps.setString(1, message.getCmd());
		ps.setString(2, message.getUserId());
		if (!message.isBinary()) {
			ps.setString(3, new String(message.getData()));			
		}		
		ps.setLong(4, message.getCreated());
		return ps.executeUpdate();
	}

	public void resetVolumn(Connection connMeta, QueueMeta meta) throws Exception{
		logger.info("reset meta");
		connMeta.createStatement().execute("update QUEUE_META set LAST_RECORD = 0, VOLUMN = VOLUMN+1 ");
		
	}
	
	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}
}
