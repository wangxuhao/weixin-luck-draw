package com.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
public class LotteryShakeAction {
	
	private static volatile Map<String, Integer> shakeConfig = new HashMap<String, Integer>();
	private static final String ROUNDNO = "ROUND_NO";
	private static final String BEINGON = "BEING_ON";
	private static final String PRIZE01NUM = "加湿器";
	private static final String PRIZE02NUM = "咖啡券";
	private static final String PRIZE03NUM = "公交卡套";
	private static final String PRIZE04NUM = "马克杯";
	private static final String PRIZE05NUM = "提莫帽子";
	private static final String PRIZE06NUM = "五元红包";
	private static final String PRIZE07NUM = "十元红包";
	private static final String PRIZE08NUM = "迷迷门票";
	private static final String PRIZE09NUM = "U型枕";
	
	static {
		shakeConfig.put(ROUNDNO, 1);/*轮数，取值为1、2、3、4、5、6*/
		shakeConfig.put(BEINGON, 0);/*0 未开始，1 进行中，2 已结束*/
		shakeConfig.put(PRIZE01NUM, 0);
		shakeConfig.put(PRIZE02NUM, 0);
		shakeConfig.put(PRIZE03NUM, 0);
		shakeConfig.put(PRIZE04NUM, 0);
		shakeConfig.put(PRIZE05NUM, 0);
		shakeConfig.put(PRIZE06NUM, 0);
		shakeConfig.put(PRIZE07NUM, 0);
		shakeConfig.put(PRIZE08NUM, 0);
		shakeConfig.put(PRIZE09NUM, 0);
	}
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private PrizeItemDao prizeItemDao;
	
	@Autowired
	private WinningDao winningDao;
	
	/**
	 * 管理员摇一摇开关
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/**/switchLotteryShake.action")
	public @ResponseBody Map<String, Object> switchShake(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		int roundNo = Integer.parseInt(request.getParameter("roundNo"));
		int status = Integer.parseInt(request.getParameter("status"));
		if(roundNo < 1 
				|| roundNo > 6
				|| status < 0
				|| status > 2){
			result.put("code", -1);
			result.put("description", "设置参数不正确");
		}else{
			result.put("code", 1);
			shakeConfig.put(ROUNDNO, roundNo);
			shakeConfig.put(BEINGON, status);
			if(status==0){
				shakeConfig.put(PRIZE01NUM, 0);
				shakeConfig.put(PRIZE02NUM, 0);
				shakeConfig.put(PRIZE03NUM, 0);
				shakeConfig.put(PRIZE04NUM, 0);
				shakeConfig.put(PRIZE05NUM, 0);
				shakeConfig.put(PRIZE06NUM, 0);
				shakeConfig.put(PRIZE07NUM, 0);
				shakeConfig.put(PRIZE08NUM, 0);
				shakeConfig.put(PRIZE09NUM, 0);
			}
			
			//轮数状态放入数据库
			prizeItemDao.updatePrizeItemStatusByRoundNo(status, roundNo);
		}
		
		return result;
	}
	
	/**
	 * 摇一摇设置页面
	 */
	@RequestMapping(value = "/**/configLotteryShake.action")
	public ModelAndView shakeConfig(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<PrizeItem>  roundList = prizeItemDao.getAllRound();
		model.addAttribute("shakeRoundList", roundList);
		return new ModelAndView("config-shake");
		
	}
	
	/**
	 * 摇一摇页面
	 */
	@RequestMapping(value = "/**/showLotteryShake.action")
	public ModelAndView shake(HttpServletRequest request, HttpServletResponse response, Model model) {	
		
		int mId = -1;
		Member currMember = WeixinMemberService.getSessionMember(request, memberDao);
		if(currMember != null){
			mId = currMember.getMid();
		}
		
		model.addAttribute("mId", mId);

		return new ModelAndView("lottery-shake");
	}
	
	/**
	 * 摇一摇计算奖项
	 * @param request
	 * @param response
	 * @return 返回奖项
	 */
	@RequestMapping(value = "/**/calcuLotteryShake.action")
	public @ResponseBody List<PrizeItem> calcuPrize(HttpServletRequest request, HttpServletResponse response) {
		List<PrizeItem> result = new ArrayList<PrizeItem>();
		
		int mId = Integer.parseInt(request.getParameter("mId"));
		int shakeStatus = shakeConfig.get(BEINGON);
		if(mId>0 && shakeStatus==1){
			PrizeItem prize = allocatePrize(shakeConfig.get(ROUNDNO), mId);
			if(prize != null){
				result.add(prize);
			}
		}else if(mId>0){
			PrizeItem prize = new PrizeItem();
			prize.setId(-1);
			if(shakeStatus==0){
				prize.setName("本轮红包雨未开始");
			}else if(shakeStatus==2){
				prize.setName("本轮红包雨已结束");
			}
			result.add(prize);
		}
		
		return result;
	}
	
	/**
	 * 摇一摇红包分配算法
	 * @param roundNo
	 * @param mId
	 * @return
	 */
	private PrizeItem allocatePrize(int roundNo, int mId) {
		//Winning w = winningDao.findWinningByMIdAndRoundNo(roundNo, mId);
		//if(w == null){
			List<PrizeItem> prizeItemList = prizeItemDao.findPrizeItemByRoundNo(roundNo);
			for(PrizeItem prizeItem : prizeItemList){
				synchronized(this){
					int currNum = shakeConfig.get(prizeItem.getName());
					if(prizeItem.getCyclebidcount() > currNum){
						
						Member member = memberDao.findMemberByMid(mId);					
						Winning winning = new Winning();
						winning.setMid(member.getMid());
						winning.setmNickName(member.getNickname());
						winning.setmPhone(member.getPhone());
						winning.setmRealName(member.getRealname());
						winning.setmHeadimgurl(member.getHeadimgurl());
						winning.setPrizeItemId(prizeItem.getId());
						winning.setPrizeItemName(prizeItem.getName());
						winning.setBidTime(DateUtil.parseTimeMillis2TimeStamp(System.currentTimeMillis()));
						winning.setRoundNo(roundNo);
						winningDao.insertWinning(winning);
						
						shakeConfig.put(prizeItem.getName(), ++currNum);
						return prizeItem;
					}
				}
				
			}
		//}
		
		return null;
	}
}
