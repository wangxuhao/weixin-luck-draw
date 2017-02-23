package com.dao;

import java.util.List;

import com.pojo.PrizeCategory;

public interface IPrizeCategoryDao {
	
	public List<PrizeCategory> getPrizeCategoryByItemId(int prizeItemId);
	
	public int getPrizeCategoryCountByItemId(int prizeItemId);
}
