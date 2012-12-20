package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.siraya.rent.pojo.UserOnlineStatus;
import org.springframework.stereotype.Repository;
@Repository("userOnlineStatusDao")
public interface IUserOnlineStatusDao {
	
    @Update("update USER_ONLINE_STATUS set ONLINE_STATUS = #{onlineStatus} where ID = #{id}")
    public int updateOnlineStatus(UserOnlineStatus user);

    @Insert("insert into USER_ONLINE_STATUS values(#{id}, #{onlineStatus})")
    public void insert(UserOnlineStatus user);
    

}
