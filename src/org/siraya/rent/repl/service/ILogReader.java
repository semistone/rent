package org.siraya.rent.repl.service;
import org.siraya.rent.pojo.Message;
/**
 * Consume message interface.
 * 
 * @author angus_chen
 *
 */
public interface ILogReader {
	public void init(String name) throws Exception;
	
	public void consume(Message message);
}