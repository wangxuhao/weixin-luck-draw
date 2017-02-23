package com.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dao.IWinningDao;
import com.pojo.Winning;

@Component
public class WinningDao implements IWinningDao {
	@Autowired
	private SqlSession sqlSession;

	@Override
	public List<Winning> getAllWinningByPrizeItem(int prizeItemId) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("prizeItemId", prizeItemId);
		List<Winning> winningList = sqlSession.selectList("com.pojo.WinningMapper.getWinningByPrizeItem", params);
		return winningList;
	}

	@Override
	public List<Winning> getWinningByMId(int mid) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("mid", mid);
		List<Winning> winningList = sqlSession.selectList("com.pojo.WinningMapper.getWinningByMid", params);
		return winningList;
	}

	@Override
	public void save(Winning winning) {
		sqlSession.insert("com.pojo.WinningMapper.insertWinning", winning);
		sqlSession.update("com.pojo.MemberMapper.updateMemberByBid", winning.getMid());
		sqlSession.update("com.pojo.PrizeCategoryMapper.updatePrizeCategoryById", winning.getPrizeCategoryId());
	}

	@Override
	public void insertWinning(Winning winning) {
		sqlSession.insert("com.pojo.WinningMapper.insertWinning", winning);
	}
	
	@Override
	public void updateByCased(int mid, int wId) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("mid", mid);
		params.put("id", wId);
		sqlSession.update("com.pojo.WinningMapper.updateWinningByCashed", params);
	}

	@Override
	public Winning findWinningByMIdAndPrizeItem(int prizeItemId, int mid) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("mid", mid);
		params.put("prizeItemId", prizeItemId);
		Winning winning = sqlSession.selectOne("com.pojo.WinningMapper.getWinningByMidAndPrizeItem", params);
		return winning;
	}
	
	@Override
	public Winning findWinningByMIdAndRoundNo(int roundNo,int mid) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("mid", mid);
		params.put("roundno", roundNo);
		Winning winning = sqlSession.selectOne("com.pojo.WinningMapper.getWinningByMidAndRoundNo", params);
		return winning;
	}
	@Override
	public void updateAmountByMIdAndPrizeItem(int amount, int prizeItemId, int mid) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("amount", amount);
		params.put("mid", mid);
		params.put("prizeItemId", prizeItemId);
		sqlSession.update("com.pojo.WinningMapper.updateWinningAmount", params);
	}

}
