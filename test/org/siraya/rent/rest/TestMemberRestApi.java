package org.siraya.rent.rest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.siraya.rent.filter.UserAuthorizeData;
import org.siraya.rent.pojo.Member;

import org.siraya.rent.user.service.IMemberService;

public class TestMemberRestApi {
	IMemberService memberService;
	MemberRestApi memberRestApi;
	private Mockery context;
	private UserAuthorizeData userAuthorizeData;
	private Member member;

	@Before
	public void setUp() {
		context = new JUnit4Mockery();
		memberRestApi = new MemberRestApi();
		memberService = context.mock(IMemberService.class);
		this.memberRestApi.setMemberService(memberService);
		member = new Member();
		member.genId();
		member.setUserId("123");

		userAuthorizeData = new UserAuthorizeData();
		userAuthorizeData.setUserId("ddd");

		this.memberRestApi.setUserAuthorizeData(userAuthorizeData);
	}

	@Test
	public void testPost() {
		context.checking(new Expectations() {
			{
				one(memberService).newMember(with(any(Member.class)));
			}
		});
		memberRestApi.post(member);
	}

	@Test
	public void testPut() {
		context.checking(new Expectations() {
			{
				one(memberService).updateMember(with(any(Member.class)));
			}
		});
		memberRestApi.put(member.getId(), member);
	}

	@Test
	public void testDelete() {
		context.checking(new Expectations() {
			{
				one(memberService).deleteMember(userAuthorizeData.getUserId(),
						member.getId());
			}
		});
		memberRestApi.delete(member.getId());
	}

}
