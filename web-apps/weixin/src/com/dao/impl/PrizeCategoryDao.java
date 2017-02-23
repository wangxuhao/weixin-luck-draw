package com.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dao.IPrizeCategoryDao;
import com.pojo.PrizeCategory;

@Component
public class PrizeCategoryDao implements IPrizeCategoryDao {

	@Autowired
	private SqlSession sqlSession;

	@Override
	public List<PrizeCategory> getPrizeCategoryByItemId(int prizeItemId) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("prizeItemId", prizeItemId);
		List<PrizeCategory> PrizeCategoryList = sqlSession
				.selectList("com.pojo.PrizeCategoryMapper.getPrizeCategoryByItemId", params);
		return PrizeCategoryList;
	}

	@Override
	public int getPrizeCategoryCountByItemId(int prizeItemId) {
		Map<String, Integer> params = new HashMap<String, Integer>();
		params.put("prizeItemId", prizeItemId);
		int count = sqlSession.selectOne("com.pojo.PrizeCategoryMapper.getPrizeCateCountByItemId", params);
		return count;
	}

}
