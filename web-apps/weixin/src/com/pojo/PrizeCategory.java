package com.pojo;

import java.io.Serializable;

/**
 * 奖项物品种类
 * 
 * @author wangxh
 *
 */
public class PrizeCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 20161228001L;

	/**
	 * 奖项物品种类id
	 */
	private int id;

	/**
	 * 奖项物品种类名称
	 */
	private String name;

	/**
	 * 关联奖项ID
	 */
	private int prizeItemId;

	/**
	 * 关联奖项名称
	 */
	private String prizeItemName;

	/**
	 * 物品总数量
	 */
	private int totalNum;

	/**
	 * 可用物品数量
	 */
	private int effectNum;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public int getEffectNum() {
		return effectNum;
	}

	public void setEffectNum(int effectNum) {
		this.effectNum = effectNum;
	}
}
