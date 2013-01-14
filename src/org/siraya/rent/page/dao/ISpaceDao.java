package org.siraya.rent.page.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.siraya.rent.pojo.Space;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository("spaceDao")
public interface ISpaceDao {
	
    @Select("select s.* from SPACE s, PAGE p where p.NAME=#{pageName} and p.ID=s.PAGE_ID") 
    @ResultMap("rent.mapper.SpaceResultMap")
	public List<Space> getByPageName(@Param("pageName")String pageName);
    
    @Update("update SPACE set CONTENT = #{content} where ID = #{id}")
    public int update(Space space);

}
