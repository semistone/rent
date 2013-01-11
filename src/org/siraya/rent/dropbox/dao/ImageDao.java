package org.siraya.rent.dropbox.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Repository;
import org.siraya.rent.pojo.Image;
@Repository("imageDao")
public interface ImageDao {
    @Insert("insert into IMAGE values(#{id}, #{imgTarget}, #{imgGroup}, #{shareUrl}, #{created}, #{modified})")
	public int insert(Image image);
    
    @Select("select * from IMAGE where ID=#{id}") 
    @ResultMap("rent.mapper.ImageResultMap")
    public Image get(@Param("id")String id);
    
    @Delete("delete from IMAGE where ID=#{id}")
    public int delete(@Param("id")String id);
}
