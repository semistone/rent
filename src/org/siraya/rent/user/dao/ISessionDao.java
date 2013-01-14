package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.siraya.rent.pojo.Session;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository("sessionDao")
public interface ISessionDao {
	
    @Insert("insert into SESSION (ID, DEVICE_ID, USER_ID, LAST_LOGIN_IP, CITY, COUNTRY, CREATED) values(#{id},#{deviceId},#{userId},#{lastLoginIp},#{city}, #{country}, #{created})")
    public void newSession(Session session);
    
	@Select("select * from SESSION where USER_ID=#{userId} and DEVICE_ID=#{deviceId} order by CREATED desc limit #{limit} offset #{offset}")
    @ResultMap("rent.mapper.SessionResultMap")
    public List<Session> getSessions(@Param("userId")String userId,@Param("deviceId")String deviceId, @Param("limit")int limit, @Param("offset")int offset);


	@Select("select * from SESSION where ID=#{id} ")
    @ResultMap("rent.mapper.SessionResultMap")
	public Session getSession(@Param("id")String id);
	
    @Update("update SESSION set ONLINE_STATUS = #{onlineStatus}, CALLBACK = #{callback} where ID = #{id}")
    public int updateOnlineStatus(Session session);
    
    @Select("select count(*) from SESSION where ONLINE_STATUS='1' and USER_ID = #{userId}")
    public int getUserOnlineStatusFromSessions(@Param("userId")String userId);

    @Select("select distinct(CALLBACK) from SESSION where ONLINE_STATUS='1' and USER_ID = #{userId}")
    public List<String> callbacks(@Param("userId")String userId);
    
	@Update("update SESSION set ONLINE_STATUS = '0' where CALLBACK =#{callback} and ONLINE_STATUS='1'")
	public void resetOnlineStatus(@Param("callback") String callback);

	@Select("select distinct(USER_ID) from SESSION where CALLBACK=#{callback} and ONLINE_STATUS='1'")
	public List<String> getOnlineUserFilterByCallback(@Param("callback") String callback);
    
}
