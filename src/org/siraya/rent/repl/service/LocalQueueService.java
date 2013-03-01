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
	@Autowired
	private IQueueDao queueDao;


	private static Logger logger = LoggerFactory
			.getLogger(LocalQueueService.class);
	private Connection connVolumn;
	private HashMap<String, Object> queueSettings;
	private QueueMeta meta;
	private String queue;
	private int currentVolumn = 0 ;
	private List<INewMessageEventListener> listeners = new ArrayList<INewMessageEventListener>();
	private int maxEntity;

	
	public void setBeanName(String name){
		this.queue = name;
	}
		
	public void afterPropertiesSet() throws Exception {
		HashMap<String, Object> settings = (HashMap<String, Object>) applicationConfig
				.get("repl");
		HashMap<String, Object> localQueues = (HashMap<String, Object>) settings
				.get("log_writers");

		if (!localQueues.containsKey(queue)) {
			throw new NullPointerException("queue " + queue
					+ " setting not exist");
		}
		queueSettings = (HashMap<String, Object>) localQueues.get(queue);
		if (queueDao == null) {
			logger.info("new queue dao");
			QueueDao tmp = new QueueDao();
			tmp.setApplicationConfig(applicationConfig);
			tmp.setQueue(queue);
			this.queueDao = tmp;
		}
		queueDao.initQueue(queue);
		meta = queueDao.getMeta();
		this.currentVolumn = meta.getVolumn();
		connVolumn = queueDao.initVolumnFile(meta.getVolumn());
	}
	
	
	public List<QueueMeta> getMetaList() throws Exception{
		
		return this.queueDao.getMetaList();
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
		if (maxEntity == 0) {
			this.getMaxEntity();
		}
		int lastRecord = meta.getLastRecord();
		logger.debug("last record is "+lastRecord +" max entity is "+maxEntity);
		int tmpVolumn = this.currentVolumn;
		if (meta.getLastRecord() >= maxEntity - 5) { // last 5 record
			synchronized (this) {
				if (maxEntity == meta.getLastRecord()) {
					// if current insert in last record, only first thread will go to first 
					// block, else will just increase last record.
					if (tmpVolumn == meta.getVolumn()) {
						meta.setLastRecord(1);
						meta.increaseVolumn();
						queueDao.resetVolumn(meta);
						this.connVolumn.close();
						this.connVolumn = queueDao.initVolumnFile(meta.getVolumn());
						currentVolumn = meta.getVolumn();
						logger.info("update volumn to "+this.currentVolumn);
					} else { 
						// already change to next volumn.
						meta.increaseLastRecord();					
					}
				} else {
					// still not the last record, just ++
					meta.increaseLastRecord();					
				}
			}
		} else {
			meta.increaseLastRecord();			
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
		INewMessageEventListener[] array = listeners.toArray(new INewMessageEventListener[0]);
		int size = array.length;
		for(int i = 0 ; i < size ; i++){
			array[i].newMessageEvent();
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
	
	public int getMaxEntity() {
		if (maxEntity != 0 ) {
			return this.maxEntity;
		}
		if (queueSettings.containsKey("volumn_max_entity")) {
			maxEntity = (int) queueSettings.get("volumn_max_entity");	
		}else {
			maxEntity = 5000;
		}	
		logger.info("max entity is "+maxEntity);
		return maxEntity;
	}

	public void setMaxEntity(int maxEntity) {
		this.maxEntity = maxEntity;
	}

}
