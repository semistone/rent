package org.siraya.rent.user.service;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.pojo.Member;
import org.siraya.rent.pojo.User;
import org.siraya.rent.user.dao.IMemberDao;
public class TestMemberService {
	MemberService memberService;
	IMemberDao memberDao;
	private Mockery context;
	private Member member;
	@Before
	public void setUp() {
		memberService = new MemberService();
		context = new JUnit4Mockery();
		memberDao = context.mock(IMemberDao.class);
		member = new Member();
		member.genId();
		member.setUserId("123");
		
		this.memberService.setMemberDao(memberDao);
	}
	
	@Test
	public void testNew(){
		context.checking(new Expectations() {
			{
				one(memberDao).newMember(with(any(Member.class)));
			}
		});
		memberService.newMember(member);
	}

	@Test
	public void testUpdate(){
		context.checking(new Expectations() {
			{
				one(memberDao).updateMember(with(any(Member.class)));
				will(returnValue(1));
			}
		});
		memberService.updateMember(member);
	}

	@Test
	public void testDelete(){
		context.checking(new Expectations() {
			{
				one(memberDao).deleteMember(member.getUserId(),member.getId());
				will(returnValue(1));
			}
		});
		memberService.deleteMember(member.getUserId(),member.getId());
	}
}
