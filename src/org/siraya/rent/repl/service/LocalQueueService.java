package org.siraya.rent.repl.service;

import java.sql.Connection;
import java.util.*;
import org.siraya.rent.pojo.*;
import org.siraya.rent.utils.IApplicationConfig;
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.siraya.rent.repl.dao.*;
import org.springframework.beans.factory.*;
public class LocalQueueService implements ILocalQueueService,BeanNameAware,InitializingBean {
	@Autowired
	private IApplicationConfig applicationConfig;
	private IQueueDao queueDao;


	private static Logger logger = LoggerFactory
			.getLogger(LocalQueueService.class);
	private Connection connVolumn;
	private HashMap<String, Object> queueSettings;
	private QueueMeta meta;
	private String queue;
	private List<INewMessageEventListener> listeners = new ArrayList<INewMessageEventListener>();
	
	public void setBeanName(String name){
		this.queue = name;
	}
		
	public void afterPropertiesSet() throws Exception {
		HashMap<String, Object> settings = (HashMap<String, Object>) applicationConfig
				.get("repl");
		HashMap<String, Object> localQueues = (HashMap<String, Object>) settings
				.get("local_queues");

		if (!localQueues.containsKey(queue)) {
			throw new NullPointerException("queue " + queue
					+ " setting not exist");
		}
		queueSettings = (HashMap<String, Object>) localQueues.get(queue);
		if (queueDao == null) {
			throw new NullPointerException("queue dao is not set yet");
		}
		queueDao.initQueue(queue);
		meta = queueDao.getMeta();
		connVolumn = queueDao.initVolumnFile(meta.getVolumn());
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
			Connection conn = queueDao.initVolumnFile(volumn);
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
				queueDao.resetVolumn(meta);
			} else {
				meta.increaseLastRecord();
			}
		}
		queueDao.insert(connVolumn, meta, message);
		this.triggerListener();
	}
	
	public void addEventListener(INewMessageEventListener listener){
		listeners.add(listener);
	}

	public void removeEventListener(INewMessageEventListener listener){
		listeners.remove(listener);
	}

	private void triggerListener(){
		for(INewMessageEventListener listener: listeners){
			listener.newMessageEvent();
		}
	}
	
	public QueueMeta getMeta() throws Exception {
		return this.queueDao.getMeta(this.queue);
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
