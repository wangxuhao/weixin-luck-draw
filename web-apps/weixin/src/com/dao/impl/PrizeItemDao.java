package com.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dao.IPrizeItemDao;
import com.pojo.PrizeItem;

@Component
public class PrizeItemDao implements IPrizeItemDao {
	@Autowired
	private SqlSession sqlSession;

	@Override
	public List<PrizeItem> getAllPrizeItem() {
		List<PrizeItem> prizeItemList = sqlSession.selectList("com.pojo.PrizeItemMapper.getAllPrizeItem");
		return prizeItemList;
	}
	@Override
	public PrizeItem findPrizeItemById(int id){
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("id", id);
		return sqlSession.selectOne("com.pojo.PrizeItemMapper.findPrizeItemById", params);
	}
	@Override
	public List<PrizeItem> findPrizeItemByRoundNo(int roundNo){
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("roundno", roundNo);
		return sqlSession.selectList("com.pojo.PrizeItemMapper.findPrizeItemByRoundNo", params);
	}
	@Override
	public void updatePrizeItemStatusByRoundNo(int status, int roundNo) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("status", status);
		params.put("roundno", roundNo);
		sqlSession.update("com.pojo.PrizeItemMapper.updatePrizeItemStatusByRoundNo", params);
	}
	@Override
	public List<PrizeItem> getAllRound() {
		List<PrizeItem> roundList = sqlSession.selectList("com.pojo.PrizeItemMapper.getAllRound");
		return roundList;
	}
}
