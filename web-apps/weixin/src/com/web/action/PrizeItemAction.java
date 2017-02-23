package com.web.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dao.impl.PrizeCategoryDao;
import com.dao.impl.PrizeItemDao;
import com.pojo.PrizeItem;

@Controller
public class PrizeItemAction {
	@Autowired
	private PrizeItemDao prizeItemDao;
	
	@Autowired
	private PrizeCategoryDao prizeCategoryDao;

	/**
	 * 获取所有奖项
	 */
	@RequestMapping(value = "/**/getAllPrizeItem.action")
	public @ResponseBody List<PrizeItem> getAllPrizeItem(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		List<PrizeItem> prizeItemList = prizeItemDao.getAllPrizeItem();
		return prizeItemList;
	}

	/**
	 * 获取奖项奖品数量
	 */
	@RequestMapping(value = "/**/getPrizeCateByItemId.action")
	public @ResponseBody int getPrizeCategoryCountByItemId(HttpServletRequest request, HttpServletResponse response) {
		int prizeItemId = Integer.parseInt( request.getParameter("prizeItemId") );
		int count = prizeCategoryDao.getPrizeCategoryCountByItemId(prizeItemId);
		return count;
	}

}
