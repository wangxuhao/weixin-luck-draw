package com.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dao.impl.PrizeCategoryDao;
import com.dao.impl.PrizeItemDao;
import com.pojo.PrizeItem;

@Controller
public class LotteryDrawAction {
	@Autowired
	PrizeItemDao prizeItemDao;
	
	@Autowired
	PrizeCategoryDao prizeCategoryDao;
	/**
	 * 开始抽奖页面
	 */
	@RequestMapping(value = "/**/showLotteryDraw.action")
	public ModelAndView doLotterDraw(HttpServletRequest request, HttpServletResponse response, Model model) {
		int prizeItemId = Integer.parseInt(request.getParameter("prizeItemId"));
		PrizeItem prizeItem = prizeItemDao.findPrizeItemById(prizeItemId);
		String prizeItemName = prizeItem.getName();
		int cycleBidCount = prizeItem.getCyclebidcount();
		model.addAttribute("prizeItemId", prizeItemId);
		model.addAttribute("prizeItemName", prizeItemName);
		model.addAttribute("cyclebidcount", cycleBidCount);
		return new ModelAndView("lottery-draw");
	}
	
	/**
	 * 计算剩余抽奖轮数
	 * @return
	 */
	@RequestMapping(value = "/**/queryCycleCount.action")
	public @ResponseBody int calcCycleCount(HttpServletRequest request, HttpServletResponse response) {
		int effectCycleCount = 0;
		int prizeItemId = Integer.parseInt(request.getParameter("prizeItemId"));
		PrizeItem prizeItem = prizeItemDao.findPrizeItemById(prizeItemId);
		int cycleBidCount = prizeItem.getCyclebidcount();
		int allEffectNum = prizeCategoryDao.getPrizeCategoryCountByItemId(prizeItemId);
		if (cycleBidCount == 0) {
			return 0;
		}
		effectCycleCount = allEffectNum % cycleBidCount == 0 ? allEffectNum / cycleBidCount
				: allEffectNum / cycleBidCount + 1;
		return effectCycleCount;
	}
}
