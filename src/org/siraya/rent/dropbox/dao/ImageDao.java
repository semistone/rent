package org.siraya.rent.dropbox.dao;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import org.siraya.rent.pojo.Image;
import org.apache.ibatis.annotations.Select;
@Repository("imageDao")
public interface ImageDao {
    @Insert("insert into IMAGE (ID,USER_ID,IMG_TARGET,IMG_GROUP,SHARE_URL,STATUS,CREATED,MODIFIED) values(#{id},#{userId}, #{imgTarget}, #{imgGroup}, #{shareUrl},#{status}, #{created}, #{modified})")
	public int insert(Image image);
    
    @Select("select * from IMAGE where ID=#{id}") 
    @ResultMap("rent.mapper.ImageResultMap")
    public Image get(@Param("id")String id);
    
    @Delete("delete from IMAGE where ID=#{id} and USER_ID=#{userId}")
    public int delete(@Param("id")String id,@Param("userId")String userId);
    
    @Select("select * from IMAGE where STATUS='0' limit 100")
    @ResultMap("rent.mapper.ImageResultMap")
    public List<Image> fetchImgNeedSync();
    
    @Update("update IMAGE set IMG_TARGET = #{imgTarget}, SHARE_URL= #{shareUrl}, MODIFIED=#{modified} ,STATUS=#{status} where ID = #{id} and USER_ID=#{userId}")
    public int update(Image imag);
    
    @Select("select * from IMAGE where IMG_GROUP=#{imgGroup}")
    @ResultMap("rent.mapper.ImageResultMap")
    public List<Image> getImage(@Param("imgGroup")String imgGroup);
    
    @Select("select count(*) from IMAGE where IMG_GROUP=#{imgGroup}")
    public int groupCount();
    
 
}