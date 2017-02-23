package com.dao;

import java.util.List;

import com.pojo.Winning;

public interface IWinningDao {

	public List<Winning> getAllWinningByPrizeItem(int prizeItemId);

	public List<Winning> getWinningByMId(int mid);
	
	public Winning findWinningByMIdAndPrizeItem(int prizeItemId,int mid);
	
	public Winning findWinningByMIdAndRoundNo(int roundNo,int mid);

	public void save(Winning winning);
	
	public void insertWinning(Winning winning);

	public void updateByCased(int mid, int wId);
	
	public void updateAmountByMIdAndPrizeItem(int amount, int prizeItemId, int mid);
}
