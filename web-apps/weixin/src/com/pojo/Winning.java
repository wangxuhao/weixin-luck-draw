package com.pojo;

import java.io.Serializable;

public class Winning implements Serializable {

	/**
	 * 获奖对象
	 */
	private static final long serialVersionUID = 20161214002L;

	/**
	 * 中奖ID
	 */
	private int id;

	/**
	 * 中奖人ID
	 */
	private int mid;

	/**
	 * 中奖人昵称
	 */
	private String mNickName;

	/**
	 * 中奖人真实姓名
	 */
	private String mRealName;

	/**
	 * 中奖人头像
	 */
	private String mHeadimgurl;

	/**
	 * 中奖人电话
	 */
	private String mPhone;

	/**
	 * 奖项ID
	 */
	private int prizeItemId;

	/**
	 * 奖项名称
	 */
	private String prizeItemName;

	/**
	 * 奖项物品种类ID
	 */
	private int prizeCategoryId;

	/**
	 * 奖项物品种类名称
	 */
	private String prizeCategoryName;

	/**
	 * 中奖时间
	 */
	private String bidTime;

	/**
	 * 是否已兑现 1 兑现 0 没有兑现
	 */
	private int isCashed;

	/**
	 * 获奖数
	 */
	private int amount;

	/**
	 * 第几轮(摇一摇轮数：1、2、3、4、5、6)
	 */
	private int roundNo;
	
	/**
	 * 奖品信息
	 */
	private PrizeCategory prizeCategory;

	public PrizeCategory getPrizeCategory() {
		return prizeCategory;
	}

	public void setPrizeCategory(PrizeCategory prizeCategory) {
		this.prizeCategory = prizeCategory;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public String getmNickName() {
		return mNickName;
	}

	public void setmNickName(String mNickName) {
		this.mNickName = mNickName;
	}

	public String getmRealName() {
		return mRealName;
	}

	public void setmRealName(String mRealName) {
		this.mRealName = mRealName;
	}

	public String getmHeadimgurl() {
		return mHeadimgurl;
	}

	public void setmHeadimgurl(String mHeadimgurl) {
		this.mHeadimgurl = mHeadimgurl;
	}

	public String getmPhone() {
		return mPhone;
	}

	public void setmPhone(String mPhone) {
		this.mPhone = mPhone;
	}

	public int getPrizeItemId() {
		return prizeItemId;
	}

	public void setPrizeItemId(int prizeItemId) {
		this.prizeItemId = prizeItemId;
	}

	public String getPrizeItemName() {
		return prizeItemName;
	}

	public void setPrizeItemName(String prizeItemName) {
		this.prizeItemName = prizeItemName;
	}

	public int getPrizeCategoryId() {
		return prizeCategoryId;
	}

	public void setPrizeCategoryId(int prizeCategoryId) {
		this.prizeCategoryId = prizeCategoryId;
	}

	public String getPrizeCategoryName() {
		return prizeCategoryName;
	}

	public void setPrizeCategoryName(String prizeCategoryName) {
		this.prizeCategoryName = prizeCategoryName;
	}

	public String getBidTime() {
		return bidTime;
	}

	public void setBidTime(String bidTime) {
		this.bidTime = bidTime;
	}

	public int getIsCashed() {
		return isCashed;
	}

	public void setIsCashed(int isCashed) {
		this.isCashed = isCashed;
	}

	public int getRoundNo() {
		return roundNo;
	}

	public void setRoundNo(int roundno) {
		this.roundNo = roundno;
	}

}
