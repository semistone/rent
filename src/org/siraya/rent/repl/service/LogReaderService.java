package org.siraya.rent.repl.service;
import java.sql.Connection;
import org.springframework.beans.factory.*;
import org.siraya.rent.pojo.*;
import org.siraya.rent.repl.dao.IQueueDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Runnable;
import java.util.List;

/**
 * 
 * @author angus_chen
 *
 */
public class LogReaderService implements BeanNameAware,Runnable,INewMessageEventListener,ILogReaderService,InitializingBean{
	/**
	 * which local queue it listen to 
	 */
	private ILocalQueueService localQueueService;
	private static Logger logger = LoggerFactory
			.getLogger(LogReaderService.class);
	private Connection connVolumn;
	private String name;
	private IQueueDao queueDao;
	private QueueMeta meta; 
	private String queue;
	private ILogReader logReader;
	private int writeInterval = 1;
	private boolean isShutdown = false;


	public void afterPropertiesSet() throws Exception {
		meta = this.queueDao.getMeta(name);
	}
	
	public void run(){
		try {
			if (logReader == null) {
				throw new NullPointerException("log reader is null");
			}
			Connection conn = queueDao.initVolumnFile(meta.getVolumn());
			int count = 0;
			while(!isShutdown) {
				List<Message> messages = this.queueDao.dump(conn, meta.getLastRecord()+1, 5000);			
				for (Message message: messages) {				
					count ++;
					logReader.consume(message);
					meta.increaseLastRecord();
					if (count % writeInterval == 0) {
						queueDao.updateReaderMeta(meta);
					}
				}				
				if (messages.size() == 0) {
					// no more data
					this.noMoreMessageAndWait();
				}
			}
		}catch(Exception e){
			logger.debug("error",e);
		}
	}

	private synchronized void noMoreMessageAndWait() throws Exception{
		logger.debug("no more task and wait");
		this.localQueueService.addEventListener(this);
		this.wait();
	}
	
	public synchronized void newMessageEvent(){
		logger.debug("wake up");
		this.localQueueService.removeEventListener(this);
		this.notify();
	}
	
	
	public void setLocalQueueService(ILocalQueueService localQueueService) throws Exception {
		logger.debug("setup local queue service in reader");
		this.localQueueService = localQueueService;
		this.queueDao = localQueueService.getQueueDao();
		this.queue = localQueueService.getMeta().getId();

		
	}
	
	public ILogReader getLogReader() {
		return logReader;
	}

	public void setLogReader(ILogReader logReader) {
		this.logReader = logReader;
	}

	public void setBeanName(String name){
		this.name = name;
	}
	
	public ILocalQueueService getLocalQueueService() {
		return localQueueService;
	}
	public boolean isShutdown() {
		return isShutdown;
	}

	public void setShutdown(boolean isShutdown) {
		this.isShutdown = isShutdown;
	}
}
