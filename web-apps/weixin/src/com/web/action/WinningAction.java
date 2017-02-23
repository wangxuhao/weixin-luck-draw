package com.web.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dao.impl.MemberDao;
import com.dao.impl.PrizeItemDao;
import com.dao.impl.WinningDao;
import com.pojo.Member;
import com.pojo.PrizeItem;
import com.pojo.Winning;
import com.util.DateUtil;
import com.web.service.WeixinMemberService;

@Controller
public class WinningAction {
	@Autowired
	private WinningDao winningDao;
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private PrizeItemDao prizeItemDao; 

	@RequestMapping(value = "/**/getAllWinningByPrizeItem.action")
	public ModelAndView getAllWinningByPrizeItem(HttpServletRequest request, HttpServletResponse response, Model model) {
		int prizeItemId = Integer.parseInt(request.getParameter("prizeItemId"));
		List<Winning> winningList = winningDao.getAllWinningByPrizeItem(prizeItemId);
		model.addAttribute("winningList", winningList);
		return new ModelAndView("allwinning");
	}
	
	@RequestMapping(value = "/**/bidWinning.action")
	public void bidWinning(HttpServletRequest request, HttpServletResponse response, Model model) {
		int mid = Integer.parseInt(request.getParameter("mid"));
		int prizeItemId = Integer.parseInt(request.getParameter("prizeItemId"));
		Member member = memberDao.findMemberByMid(mid);
		PrizeItem prizeItem = prizeItemDao.findPrizeItemById(prizeItemId);
		Winning w = winningDao.findWinningByMIdAndPrizeItem(prizeItemId, mid);
		if(w == null){
			Winning winning = new Winning();
			winning.setMid(member.getMid());
			winning.setmNickName(member.getNickname());
			winning.setmPhone(member.getPhone());
			winning.setmRealName(member.getRealname());
			winning.setmHeadimgurl(member.getHeadimgurl());
			winning.setPrizeItemId(prizeItem.getId());
			winning.setPrizeItemName(prizeItem.getName());
			winning.setBidTime(DateUtil.parseTimeMillis2TimeStamp(System.currentTimeMillis()) );
			winningDao.save(winning);
		}else{
			winningDao.updateAmountByMIdAndPrizeItem(w.getAmount()+1, prizeItemId, mid);
		}
	} 
	
	/**
	 * 用户查询奖品页面
	 * 微信真实访问方式：https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx18cb813e42b9be90&
	 * redirect_uri=https://a2d45333.ngrok.io/Weixin/getWinningByMId.action
	 * &response_type=code&scope=snsapi_base&state=123#wechat_redirect
	 */
	@RequestMapping(value = "/**/getWinningByMId.action")
	public ModelAndView  getWinningByMId(HttpServletRequest request, HttpServletResponse response, Model model) {
		Member currMember = WeixinMemberService.getSessionMember(request, memberDao);
		int mid = -1;
		if(currMember != null) mid = currMember.getMid();
		List<Winning> winningList = winningDao.getWinningByMId(mid);
		
		model.addAttribute("winningList", winningList);

		return new ModelAndView("prize-query");
	}
	
	@RequestMapping(value = "/**/getReceivePrize.action")
	public ModelAndView  getReceivePrize(HttpServletRequest request, HttpServletResponse response, Model model) {
		Member currMember = WeixinMemberService.getSessionMember(request, memberDao);
		int mid = -1;
		if(currMember != null) mid = currMember.getMid();
		List<Winning> winningList = winningDao.getWinningByMId(mid);
		
		model.addAttribute("winningList", winningList);

		return new ModelAndView("prize-receive");
	}
	
	@RequestMapping(value = "/**/cashByMId.action")
	public void  cashByMId(HttpServletRequest request, HttpServletResponse response) {
		int mid = WeixinMemberService.getSessionMember(request, memberDao).getMid();
		String winningId = request.getParameter("winningId");
		winningDao.updateByCased(mid, Integer.parseInt(winningId));
	}

}
