package org.siraya.rent.repl.service;

import java.sql.Connection;
import java.util.*;
import org.siraya.rent.pojo.*;
import org.siraya.rent.utils.IApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.siraya.rent.repl.dao.*;

public class LocalQueueService implements ILocalQueueService {
	@Autowired
	private IApplicationConfig applicationConfig;

	@Autowired
	private IQueueDao queueDao;

	private static Logger logger = LoggerFactory
			.getLogger(LocalQueueService.class);
	private Connection connMeta;
	private Connection connVolumn;
	private HashMap<String, Object> queueSettings;
	private QueueMeta meta;
	private String queue;
	public LocalQueueService() {

	}

	public LocalQueueService(String queue) throws Exception {
		init(queue);
	}

	void init(String queue) throws Exception {
		this.queue = queue;
		queueDao.init();
		HashMap<String, Object> settings = (HashMap<String, Object>) applicationConfig
				.get("repl");
		HashMap<String, Object> localQueues = (HashMap<String, Object>) settings
				.get("local_queues");

		if (!localQueues.containsKey(queue)) {
			throw new NullPointerException("queue " + queue
					+ " setting not exist");
		}
		queueSettings = (HashMap<String, Object>) localQueues.get(queue);

		connMeta = queueDao.initQueue(queue);
		meta = queueDao.getMeta(connMeta);
		connVolumn = queueDao.initVolumnFile(queue, meta.getVolumn());
	}

	/**
	 * dump volumn files
	 * @param volumn
	 * @return
	 * @throws Exception
	 */
	public List<Message> dump(int volumn)throws Exception{
		if (volumn == -1) {
			logger.info("dump current volumn");
			return queueDao.dump(connVolumn);			
		} else {
			Connection conn = queueDao.initVolumnFile(queue, volumn);
			return queueDao.dump(conn);
		}
	}
	
	public void insert(Message message) throws Exception {
		Integer maxEntity = (Integer) queueSettings.get("volumn_max_entity");
		if (maxEntity == null) {
			logger.info("set default max record 5000");
			maxEntity = 5000;
		}
		synchronized (this) {
			if ((maxEntity).equals(meta.getLastRecord())) {
				meta.setLastRecord(0);
				meta.increaseVolumn();
				queueDao.resetVolumn(connMeta, meta);
			} else {
				meta.increaseLastRecord();
			}
		}
		queueDao.insert(connMeta, connVolumn, meta, message);
	}

	public QueueMeta getMeta() throws Exception {
		return this.queueDao.getMeta(connMeta);
	}
	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public IQueueDao getQueueDao() {
		return queueDao;
	}

	public void setQueueDao(IQueueDao queueDao) {
		this.queueDao = queueDao;
	}

}
