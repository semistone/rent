<<<<<<< HEAD
package org.siraya.rent.donttry.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.siraya.rent.pojo.DontTry;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.SelectKey;
import java.util.Date;
@Repository("dontTryDao")
public interface IDontTryDao {
	
	@Insert("INSERT INTO DONTTRY (NAME,START_DATE) values (#{name},#{startDate}) ON DUPLICATE KEY UPDATE COUNT = COUNT+1")
	@SelectKey(before=false,keyProperty="DONTTRY.id",resultType=Integer.class,statementType=StatementType.STATEMENT,statement="SELECT LAST_INSERT_ID() AS id")  
	public int newCounter(DontTry dontTry);

    @Select("select * from DONTTRY where NAME=#{name} and START_DATE=#{startDate}") 
    @ResultMap("rent.mapper.DontTryResultMap")
	public DontTry getByName(@Param("name")String name,@Param("startDate")Date startDate);
}
=======
package org.siraya.rent.donttry.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.siraya.rent.pojo.DontTry;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.SelectKey;
import java.util.Date;
@Repository("dontTryDao")
public interface IDontTryDao {
	
	@Insert("INSERT INTO DONTTRY (NAME,START_DATE) values (#{name},#{startDate}) ON DUPLICATE KEY UPDATE COUNT = COUNT+1")
	@SelectKey(before=false,keyProperty="DONTTRY.id",resultType=Integer.class,statementType=StatementType.STATEMENT,statement="SELECT LAST_INSERT_ID() AS id")  
	public int newCounter(DontTry dontTry);

    @Select("select * from DONTTRY where NAME=#{name} and START_DATE=#{startDate}") 
    @ResultMap("rent.mapper.DontTryResultMap")
	public DontTry getByName(@Param("name")String name,@Param("startDate")Date startDate);
}
>>>>>>> master
