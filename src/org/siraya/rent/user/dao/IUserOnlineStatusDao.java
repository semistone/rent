package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;
import org.siraya.rent.pojo.UserOnlineStatus;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository("userOnlineStatusDao")
public interface IUserOnlineStatusDao {
	
    @Update("update USER_ONLINE_STATUS set ONLINE_STATUS = #{onlineStatus} where ID = #{id}")
    public int updateOnlineStatus(UserOnlineStatus user);

    @Insert("insert into USER_ONLINE_STATUS values(#{id}, #{onlineStatus})")
    public void insert(UserOnlineStatus user);


    @Select("select * from USER_ONLINE_STATUS where FIND_IN_SET(ID, #{ids}) <> 0") 
    public List<UserOnlineStatus> list(@Param("ids") String ids);
    
    @Select("select ID from USER_ONLINE_STATUS where ONLINE_STATUS='1'  limit #{limit} offset #{offset}") 
    public List<String> online(@Param("limit") int limit , @Param("offset")int offset);
    
}
