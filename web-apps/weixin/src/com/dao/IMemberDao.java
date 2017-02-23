package com.dao;

import java.util.List;

import com.pojo.Member;

public interface IMemberDao {

	public void save(Member member);
	
	public Member findMemberByWxOpenId(String openId);

	public List<Member> findAllMember();
	
	public List<Member> findAllValidMember();
	
	public List<Member> findAllMember4Draw();
	
	public List<Member> findWinMembers(int prizeItemId, int num);
	
	/**
	 * 用户关注保存用户
	 */
	public void saveMemberFromWxAtten(Member member, String openId);
	
	public Member findMemberByMid(int mid);
	
	public void updateMemberByBid(int mid);
	
	public void updateMember(Member member);

	List<Member> findOtherMember4Draw();
}
