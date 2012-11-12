package org.siraya.rent.user.service;

import java.util.List;

import org.siraya.rent.pojo.Member;

public interface IMemberService {
	
	public void createMembers(String userId, List<Member> members);
	
	public void newMember(Member member);
	
	public void updateMember(Member member);
	
	public void deleteMember(String userId, String id);
	
	public Member getMember(String userId, String id);
}
