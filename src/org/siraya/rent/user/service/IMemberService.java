package org.siraya.rent.user.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.siraya.rent.pojo.Member;

public interface IMemberService {
	
	public void createMembers(String userId, List<Member> members);
	
	public void newMember(Member member);
	
	public void updateMember(Member member);
	
	public void deleteMember(String userId, String id);
	
	public Member getMember(String userId, String id);
	
	public List<Member> search(String userId, String name, int limit,int offset);
	
	public int searchCount(String userId, String name);
}
