package com.dao;

import java.util.List;

import com.pojo.PrizeItem;

public interface IPrizeItemDao {
	
	public List<PrizeItem> getAllPrizeItem();
	
	public PrizeItem findPrizeItemById(int id);
	
	public List<PrizeItem> findPrizeItemByRoundNo(int roundNo);
	
	public void updatePrizeItemStatusByRoundNo(int status, int roundNo);
	
	public List<PrizeItem> getAllRound();
}
