package org.siraya.rent.dropbox.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.siraya.rent.pojo.ImageGroup;
import org.springframework.stereotype.Repository;

@Repository("imageGroupDao")
public interface ImageGroupDao {
    
    @Insert("insert into IMAGE_GROUP (ID,USER_ID,PATH,STATUS,CREATED,MODIFIED) values(#{id},#{userId}, #{path},#{status}, #{created}, #{modified})")
	public int insert(ImageGroup imageGroup);
	
	@Select("select * from IMAGE_GROUP where ID=#{id}") 
    @ResultMap("rent.mapper.ImageGroupResultMap")
    public ImageGroup get(@Param("id")String id);

	@Select("select * from IMAGE_GROUP where USER_ID=#{userId} and PATH=#{path}") 
    @ResultMap("rent.mapper.ImageGroupResultMap")
    public ImageGroup getByUserAndPath(@Param("userId")String userId,@Param("path")String path);

}
