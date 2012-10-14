package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.siraya.rent.pojo.Session;
import org.springframework.stereotype.Repository;

@Repository("sessionDao")
public interface ISessionDao {
	
    @Insert("insert into SESSION values(#{id},#{deviceId},#{userId},#{lastLoginIp},#{created})")
    public void newSession(Session session);
}
