package org.siraya.rent.mobile.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.siraya.rent.pojo.MobileProvider;
import org.springframework.stereotype.Repository;

@Repository("mobileProviderDao")
public interface IMobileProviderDao {

    @Insert("insert into MOBILE_PROVIDER values(#{id}, #{user}, #{password}, #{type}, #{created}, #{modified})")
	public int newProvider(MobileProvider mobileProvider);

    
    @Select("select * from MOBILE_PROVIDER where ID=#{id} and TYPE=#{type}") 
    @ResultMap("rent.mapper.MobileProviderResultMap")
    public MobileProvider get(@Param("id")String id, @Param("type")String type);

    @Update("update MOBILE_PROVIDER set USER = #{user}, PASSWORD = #{password}, MODIFIED=#{modified} where ID = #{id} and TYPE=#{type}")
    public int updateProvider(MobileProvider mobileProvider);
    
    @Delete("delete from MOBILE_PROVIDER where ID=#{id} and TYPE=#{type} ")
    public int delete(@Param("id")String id, @Param("type")String type);
}
