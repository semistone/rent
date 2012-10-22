package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.siraya.rent.pojo.Role;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository("roleDao")
public interface IRoleDao {
    @Insert("insert into ROLE values(#{userId},#{roleId})")
    public void newRole(Role role);

	@Select("select * from ROLE where USER_ID=#{userId} ")
    @ResultMap("rent.mapper.RoleResultMap")
	public List<Role> getRoleByUserId(@Param("userId")String userId);
}
