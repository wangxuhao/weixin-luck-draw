package com.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dao.IMemberDao;
import com.pojo.Member;

@Component
public class MemberDao implements IMemberDao {

	@Autowired
	private SqlSession sqlSession;

	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void save(Member member) {
		sqlSession.insert("com.pojo.MemberMapper.insertMember", member);
		logger.info("成功保存该用户,openid..." + member.getWxOpenId());
	}
	
	@Override
	public Member findMemberByWxOpenId(String openId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("wxOpenId", openId);
		logger.info("判断数据库中是否保存有此用户,openId..." + openId);
		Member member = sqlSession.selectOne("com.pojo.MemberMapper.getMemberByWxOpenId", params);
		return member;
	}

	@Override
	public List<Member> findAllMember() {
		List<Member> memberList = sqlSession.selectList("com.pojo.MemberMapper.getAllMember");
		return memberList;
	}
	
	@Override
	public void saveMemberFromWxAtten(Member member, String openId) {
		Member findMember = this.findMemberByWxOpenId(openId);
		if (null == findMember) {
			logger.info("数据库中不包含此用户，openid..." + openId);
			this.save(member);
		} else {
			logger.info("数据库中已包含此用户，openid..." + openId);
		}
	}

	@Override
	public Member findMemberByMid(int mid) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("mid", mid);
		Member member = sqlSession.selectOne("com.pojo.MemberMapper.getMemberById", params);
		return member;
	}

	@Override
	public void updateMemberByBid(int mid) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("mid", mid);
		sqlSession.update("com.pojo.MemberMapper.updateMemberByBid", params);
		
	}

	@Override
	public void updateMember(Member member) {
		sqlSession.update("com.pojo.MemberMapper.updateMember" , member);
	}

	@Override
	public List<Member> findAllMember4Draw() {
		List<Member> memberList = sqlSession.selectList("com.pojo.MemberMapper.getAllMember4Draw");
		return memberList;
	}
	
	@Override
	public List<Member> findOtherMember4Draw() {
		List<Member> memberList = sqlSession.selectList("com.pojo.MemberMapper.getOtherMember4Draw");
		return memberList;
	}

	@Override
	public List<Member> findWinMembers(int prizeItemId, int num) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("prizeItemId", prizeItemId);
		params.put("num", num);
		List<Member> mIds = sqlSession.selectList("com.pojo.MemberMapper.getWinMembers",params);
		return mIds;
	}

	@Override
	public List<Member> findAllValidMember() {
		List<Member> memberList = sqlSession.selectList("com.pojo.MemberMapper.getAllValidMember");
		return memberList;
	}

}
