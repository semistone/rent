package org.siraya.rent.repl.service.example;

import org.siraya.rent.repl.service.ILogReader;
import org.siraya.rent.pojo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class LogReader1 implements ILogReader{
	private static Logger logger = LoggerFactory.getLogger(LogReader1.class);
	public void init(String name) throws Exception{
		
	}
	public void consume(Message message) {
		//
		// do nothing haha.
		//
		logger.info("consume message "+message.getId());
	}
}
