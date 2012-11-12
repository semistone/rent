package org.siraya.rent.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.siraya.rent.pojo.Member;
import org.springframework.stereotype.Repository;

@Repository("memberDao")
public interface IMemberDao {

	@Insert("insert into MEMBER (ID, MEMBER_ID, MEMBER_USER_ID, USER_ID,NAME, FB_ACCOUNT,CREATED,MODIFIED) values (#{id},#{memberId},#{memberUserId},#{userId},#{name},#{fbAccount},#{created},#{modified})")
    public void newMember(Member member);
	
    @Select("select * from MEMBER where ID=#{id}") 
    @ResultMap("rent.mapper.MemberResultMap")
	public Member get(@Param("id")String id);
    
    @Select("select * from MEMBER where USER_ID=#{userId} and MEMBER_USER_ID=#{memberUserId}") 
    @ResultMap("rent.mapper.MemberResultMap")
    public Member getByMemberUserId(@Param("userId")String userId,@Param("memberUserId")String memberUserId);
}
