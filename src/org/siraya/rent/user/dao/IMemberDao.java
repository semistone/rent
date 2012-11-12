package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.siraya.rent.pojo.Member;
import org.springframework.stereotype.Repository;

@Repository("memberDao")
public interface IMemberDao {

	@Insert("insert into MEMBER (ID, MEMBER_ID, MEMBER_USER_ID, USER_ID,NAME, FB_ACCOUNT,CREATED,MODIFIED) values (#{id},#{memberId},#{memberUserId},#{userId},#{name},#{fbAccount},#{created},#{modified})")
    public void newMember(Member member);
	
    @Select("select * from MEMBER where ID=#{id} and USER_ID=#{userId}") 
    @ResultMap("rent.mapper.MemberResultMap")
	public Member get(@Param("userId")String userId, @Param("id")String id);
    
    @Select("select * from MEMBER where USER_ID=#{userId} and MEMBER_USER_ID=#{memberUserId}") 
    @ResultMap("rent.mapper.MemberResultMap")
    public Member getByMemberUserId(@Param("userId")String userId,@Param("memberUserId")String memberUserId);

    @Update("update MEMBER set NAME=#{name}, EMAIL=#{email}, FB_ACCOUNT=#{fbAccount}, MEMBER_ID=#{memberId},MOBILE_PHONE=#{mobilePhone}, MODIFIED=#{modified} where ID = #{id} and USER_ID=#{userId}")
    public int updateMember(Member member);
    
    @Delete("delete from MEMBER where USER_ID=#{userId} and ID=#{id}")
    public int deleteMember(@Param("userId")String userId, @Param("id")String id);

}
