package org.siraya.rent.user.dao;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.siraya.rent.pojo.Member;
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml","classpath*:/applicationContext-mybatis.xml"})

public class TestMemberDao extends AbstractJUnit4SpringContextTests{
    @Autowired
    private IMemberDao memberDao;
    private Member member;
	long time=java.util.Calendar.getInstance().getTimeInMillis();

	@Before
    public void setUp(){
    	member = new Member();
    	member.setId("i"+time);
    	member.setCreated(time/1000);
    	member.setMemberId("m"+time);
    	member.setModified(time/1000);
    	member.setUserId("u"+time);
    	member.setMemberUserId("mu"+time);
    }
    @Test   
    public void testCRUD(){
    	this.memberDao.newMember(member);
    	
    	Member member2=this.memberDao.get(member.getId());
    	Assert.assertEquals(member.getCreated(), member2.getCreated());
    }
}