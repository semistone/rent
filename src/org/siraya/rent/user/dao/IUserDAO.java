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
            " ,TIMEZONE,NATIONAL_ID, CC, LANG,STATUS,CREATED, MODIFIED) " +   
            " VALUES (#{id}, #{mobilePhone}, #{email}, #{loginId}, #{loginType}, #{password}, " +
            "#{timezone}, #{nationalId}, #{cc}, #{lang}, #{status}, " +
            "#{created}, #{modified})";

    static final String UPDATE_USER_SQL ="UPDATE USER SET EMAIL=#{email}, MODIFIED=#{modified} WHERE ID=#{id}";

    @Insert(INSERT_USER_SQL)
    public void newUser(User user);
    
    
    @Select("select * from USER where LOGIN_ID=#{loginId} and LOGIN_TYPE=#{loginType}")
    @ResultMap("rent.mapper.UserResultMap")
    public User getUserByLoginIdAndLoginType(@Param("loginId")String loginId, @Param("loginType")int loginType);

    @Select("select * from USER where MOBILE_PHONE=#{mobilePhone}")
    @ResultMap("rent.mapper.UserResultMap")
    public User getUserByMobilePhone(@Param("mobilePhone")String mobilePhone);

    @Update(UPDATE_USER_SQL)
    public void updateUserEmail(User user);
    
}
