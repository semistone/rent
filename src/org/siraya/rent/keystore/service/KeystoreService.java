package org.siraya.rent.keystore.service;

import org.siraya.rent.keystore.dao.*;
import org.siraya.rent.repl.service.LocalQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.*;
@Service("keystoreService")
public class KeystoreService implements InitializingBean,IKeystoreService{

	@Autowired
	private IKeystoreDao keystoreDao;
	private static Logger logger = LoggerFactory
			.getLogger(KeystoreService.class);
	
	public void afterPropertiesSet() throws Exception{
		logger.info("init keystore");
		this.keystoreDao.create();
	}
	
	public String get(String key){
		return keystoreDao.get(key);
	}
	
	public void insert(String key, String value){
		logger.debug("insert key "+key);
		this.keystoreDao.insert(key, value);		
	}
	
	public void update(String key, String value) {
		this.keystoreDao.update(key, value);
	}
	
	public void delete(String key){
		this.keystoreDao.delete(key);
	}
}
