package org.siraya.rent.user.service;

import java.util.List;

import org.siraya.rent.pojo.Member;
import org.siraya.rent.user.dao.IMemberDao;
import org.siraya.rent.utils.RentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Service("memberService")
public class MemberService implements IMemberService {
	@Autowired
	private IMemberDao memberDao;

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public void createMembers(String userId, List<Member> members) {
		for (Member member : members) {
			try {
				member.setUserId(userId);
				member.genId();
				this.memberDao.newMember(member);
			} catch (org.springframework.dao.DuplicateKeyException e) {
				// skip duplicate member
			}
		}
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public void newMember(Member member) {
		member.genId();
		try {
			this.memberDao.newMember(member);
		}catch(org.springframework.dao.DuplicateKeyException e){
			throw new RentException(RentException.RentErrorCode.ErrorDuplicate,e.getMessage());
		}
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public void updateMember(Member member) {
		int ret = this.memberDao.updateMember(member);
		if (ret != 1) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"update member fail");
		}
	}

	@Transactional(value = "rentTxManager", propagation = Propagation.SUPPORTS, readOnly = false, rollbackFor = java.lang.Throwable.class)
	public void deleteMember(String userId, String id) {
		int ret = this.memberDao.deleteMember(userId, id);
		if (ret != 1) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"update member fail");
		}
	}

	@Transactional(value = "rentTxManager", readOnly = true)
	public Member getMember(String userId, String id) {
		Member member = this.memberDao.get(userId, id);
		if (member == null) {
			throw new RentException(RentException.RentErrorCode.ErrorNotFound,
					"get member fail");
		}
		return member;
	}

	public IMemberDao getMemberDao() {
		return memberDao;
	}

	public void setMemberDao(IMemberDao memberDao) {
		this.memberDao = memberDao;
	}

}
