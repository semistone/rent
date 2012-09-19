package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.siraya.rent.pojo.VerifyEvent;
import org.springframework.stereotype.Repository;
@Repository("verifyEventDao")
public interface IVerifyEventDao {
    static final String INSERT_VERIFY_SQL = "INSERT INTO VERIFY_EVENT (USER_ID,VERIFY_TYPE,VERIFY_DETAIL," +
    		" STATUS,CREATED, MODIFIED) " +   
            " VALUES (#{userId}, #{verifyType}, #{verifyDetail}, " +
            " #{status}, #{created}, #{modified}) " ;
    
    @Insert(INSERT_VERIFY_SQL)
	public void newVerifyEvent(VerifyEvent verify);

    @Select("select * from VERIFY_EVENT where VERIFY_TYPE=#{verifyType} and VERIFY_DETAIL=#{verifyDetail}")
    @ResultMap("rent.mapper.VerifyEventResultMap")
    public VerifyEvent getEventByVerifyDetailAndType(@Param("verifyType")int verifyType, @Param("verifyDetail")String verifyDetail);

    
}
