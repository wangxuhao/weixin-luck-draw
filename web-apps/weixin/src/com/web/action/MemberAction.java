package com.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dao.impl.MemberDao;
import com.dao.impl.PrizeCategoryDao;
import com.dao.impl.PrizeItemDao;
import com.dao.impl.WinningDao;
import com.pojo.Member;
import com.pojo.PrizeCategory;
import com.pojo.PrizeItem;
import com.pojo.Winning;
import com.util.DateUtil;
import com.util.RandomUtil;
import com.web.service.WeixinMemberService;

@Controller
public class MemberAction {
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private PrizeItemDao prizeItemDao;
	
	@Autowired
	private PrizeCategoryDao prizeCategoryDao;
	
	@Autowired
	private WinningDao winningDao;
	/**
	 * 获得所有的用户 
	 */
	@RequestMapping(value = "/**/getAllMemberInfo.action")
	public ModelAndView getAllMemberInfo(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Member> memberList = memberDao.findAllMember();
		model.addAttribute("memberList", memberList);
		return new ModelAndView("alluser");
	}
	
	/**
	 * 获取所有有效用户
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/**/showAllMemberView.action")
	public ModelAndView getAllMember4ShowView(HttpServletRequest request, HttpServletResponse response, Model model) {
		//List<Member> memberList = memberDao.findAllValidMember();
		//model.addAttribute("memberList", memberList);
		return new ModelAndView("alluser");
	}
	
	@RequestMapping(value = "/**/showAllMember.action")
	public @ResponseBody List<Member> getAllMember4Show(HttpServletRequest request, HttpServletResponse response, Model model) {
		List<Member> memberList = memberDao.findAllValidMember();
		return memberList;
	}

	/**
	 * 获取所有参与抽奖的人员
	 * 条件：用户信息完整（真实姓名、手机号必填），没有中过奖
	 */
	@RequestMapping(value = "/**/getAllMemberInfo4Draw.action")
	public @ResponseBody List<Member> getAllMemberInfo4Draw(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		List<Member> memberList = memberDao.findAllMember4Draw();
		return memberList;
	}
	
	/**
	 * 随机获取一条奖项物品
	 * 
	 * @param prizeItemId
	 * @return
	 */
	private PrizeCategory randomPrizeCategory(int prizeItemId) {
		List<PrizeCategory> prizeCategoryList = prizeCategoryDao.getPrizeCategoryByItemId(prizeItemId);
		int[] idx = RandomUtil.getLotteryArray(0, prizeCategoryList.size() - 1, 1);
		return prizeCategoryList.get(idx[0]);
	}
	
	/**
	 * 生成中奖名单
	 * 参数：奖项ID
	 */
	@RequestMapping(value = "/**/buildMemberInfo4Draw.action")
	public @ResponseBody List<Member> buildMemberInfo4Draw(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		int prizeItemId = Integer.parseInt(request.getParameter("prizeItemId"));
		List<Member> bidMemberList = new ArrayList<Member>();
		PrizeItem prizeItem = prizeItemDao.findPrizeItemById(prizeItemId);
		int allEffectNum = prizeCategoryDao.getPrizeCategoryCountByItemId(prizeItemId);
		int cycleBidCount = prizeItem.getCyclebidcount();
		List<Integer> mIds = findWinMembers(prizeItemId, Math.min(cycleBidCount, allEffectNum));
		for (int idx : mIds) {
			PrizeCategory prizeCategory = randomPrizeCategory(prizeItemId);
			Member bidMember = memberDao.findMemberByMid(idx);
			Winning winning = new Winning();
			winning.setMid(bidMember.getMid());
			winning.setmNickName(bidMember.getNickname());
			winning.setmPhone(bidMember.getPhone());
			winning.setmRealName(bidMember.getRealname());
			winning.setmHeadimgurl(bidMember.getHeadimgurl());
			winning.setPrizeItemId(prizeItem.getId());
			winning.setPrizeItemName(prizeItem.getName());
			winning.setBidTime(DateUtil.parseTimeMillis2Time(System.currentTimeMillis()) );
			winning.setPrizeCategoryId(prizeCategory.getId());
			winning.setPrizeCategoryName(prizeCategory.getName());
			winningDao.save(winning);
			bidMemberList.add(bidMember);
		}
		return bidMemberList;
	}
	
	/**
	 * 跳转到绑定用户页面
	 */
	@RequestMapping(value = "/**/showBidUserInfoView.action")
	public ModelAndView showBidUserInfoView(HttpServletRequest request, HttpServletResponse response,
			Model model) {
		Member mb = WeixinMemberService.getSessionMember(request, memberDao);
		model.addAttribute("member", mb);
		return new ModelAndView("bid-userinfo");
	}
	
	/**
	 * 绑定用户信息
	 */
	@RequestMapping(value = "/**/bindUserInfo.action")
	public void bindUserInfo(HttpServletRequest request, HttpServletResponse response,
			Model model){
		String name = request.getParameter("name");
		String mobilePhone = request.getParameter("mobilePhone");
		Member mb = WeixinMemberService.getSessionMember(request, memberDao);
		mb.setRealname(name);
		mb.setPhone(mobilePhone);
		memberDao.updateMember(mb);
	}
	
	/**
	 * 根据奖项获取中奖人
	 * @param prizeItemId
	 * @param num
	 * @return
	 */
	private List<Integer> findWinMembers(int prizeItemId, int num){
		List<Integer> mIds = new ArrayList<Integer>();
		//伪
		List<Member> members = memberDao.findWinMembers(prizeItemId, num);
		for(Member m: members){
			mIds.add(m.getMid());
		}
		//真
		if(mIds.size() < num){
			List<Member> memberList = memberDao.findOtherMember4Draw();
			int[] bidIdx = RandomUtil.getLotteryArray(0, memberList.size() - 1, Math.min(num - mIds.size(), memberList.size()));
			for(int i : bidIdx){
				mIds.add(memberList.get(i).getMid());
			}
		}
		return mIds;
	}
}
