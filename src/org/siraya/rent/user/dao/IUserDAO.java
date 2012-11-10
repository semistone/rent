package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.siraya.rent.pojo.User;
import org.springframework.stereotype.Repository;
@Repository("userDao")
public interface IUserDAO {
    static final String INSERT_USER_SQL = "INSERT INTO USER (ID,MOBILE_PHONE,EMAIL,LOGIN_ID,LOGIN_TYPE,PASSWORD"+
            " ,TIMEZONE,NATIONAL_ID, CC, LANG, STATUS,NAME, CREATED, MODIFIED) " +   
            " VALUES (#{id}, #{mobilePhone}, #{email}, #{loginId}, #{loginType}, #{password}, " +
            "#{timezone}, #{nationalId}, #{cc}, #{lang}, #{status}, " +
            "#{name}, #{created}, #{modified})";

    @Insert(INSERT_USER_SQL)
    public void newUser(User user);
    
    
    @Select("select * from USER where LOGIN_ID=#{loginId} and LOGIN_TYPE=#{loginType}")
    @ResultMap("rent.mapper.UserResultMap")
    public User getUserByLoginIdAndLoginType(@Param("loginId")String loginId, @Param("loginType")String loginType);

    @Select("select * from USER where MOBILE_PHONE=#{mobilePhone}")
    @ResultMap("rent.mapper.UserResultMap")
    public User getUserByMobilePhone(@Param("mobilePhone")String mobilePhone);

    @Select("select * from USER where ID=#{id}")
    @ResultMap("rent.mapper.UserResultMap")
    public User getUserByUserId(@Param("id")String id);
    
    @Update("UPDATE USER SET EMAIL=#{email}, MODIFIED=#{modified} WHERE ID=#{id}")
    public void updateUserEmail(User user);
    
    @Update("update USER set STATUS = #{newStatus}, MODIFIED=#{modified} where ID = #{id} and STATUS= #{oldStatus}")
    public int updateUserStatus(@Param("id")String id,@Param("newStatus")int newStatus,@Param("oldStatus")int oldStatus,@Param("modified")long modified);
    
    @Update("update USER set LOGIN_ID=#{loginId}, PASSWORD=#{password}, MODIFIED=#{modified} where ID = #{id} and LOGIN_ID is null")    
    public int updateUserLoginIdAndPassword(User user);
    
    @Update("update USER set LOGIN_ID=#{loginId}, LOGIN_TYPE=#{loginType}, NAME=#{name}, MODIFIED=#{modified} where ID = #{id} and LOGIN_TYPE is null and LOGIN_ID is null")
    public int initLoginIdAndType(User user);
}
