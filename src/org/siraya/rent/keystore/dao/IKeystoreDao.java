package org.siraya.rent.keystore.dao;

import org.apache.ibatis.annotations.*;
import org.siraya.rent.pojo.Space;
import org.springframework.stereotype.Repository;

@Repository("keystoreDao")
public interface IKeystoreDao {
	
	@Insert("create table if not exists KEYSTORE (KEY TEXT PRIMARY KEY,VALUE TEXT)")
	public void create();
	
    @Insert("insert into KEYSTORE values (#{key}, #{value})")
    public int insert(@Param("key")String key, @Param("value")String value);
    
    
    @Select("select value from KEYSTORE where KEY=#{key}") 
    public String get(@Param("key")String key);

    @Delete("delete from KEYSTORE where  KEY=#{key}")
    public void delete(@Param("key")String key);
}
