package org.siraya.rent.repl.dao;

import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;	
import org.siraya.rent.pojo.Message;

@Repository("queueDao")
public interface IQueueDao {

	@Insert("insert into MESSAGE_QUEUE (USER_ID, CMD, DATA, CREATED,MODIFIED) values(#{userId}, #{cmd}, #{data}, #{created}, #{modified})")
	public int insert(Message message);

}
