package com.web.service;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.dao.impl.MemberDao;
import com.pojo.Member;
import com.util.Common;
import com.util.DateUtil;
import com.util.HttpClientUtil;
import com.web.config.WeixinConfig;
import com.web.config.WeixinRequestUrls;

public class WeixinMemberService {
	private static final Logger log = Logger.getLogger(WeixinMemberService.class);

	/**
	 * 获取所有关注者，返回关注者openId
	 * 
	 * @return
	 */
	public static String getMemberListInfo() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("access_token", WeixinConfig.getAccessToken());
		String resultInfo = HttpClientUtil.postHttp(WeixinRequestUrls.GEN_ATTEN_USER_URL, map);
		JSONObject resultObj = (JSONObject) JSONObject.parse(resultInfo);
		log.info("得到所有关注者..." + resultObj.toJSONString());
		return resultObj.toJSONString();
	}

	/**
	 * 根据openId 获取用户信息
	 * 
	 * @param openId
	 * @return
	 */
	public static Member getMemberInfoByOpenId(String openId) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("lang", "zh_CN");
		map.put("access_token", WeixinConfig.getAccessToken());
		map.put("openid", openId);
		String getUrl = WeixinRequestUrls.GEN_ATTEN_MEM_INFO_URL + "?lang=zh_CN&access_token=" + map.get("access_token")
				+ "&openid=" + map.get("openid");
		String resultInfo = HttpClientUtil.requestGet(getUrl);
		JSONObject resultObj = (JSONObject) JSONObject.parse(resultInfo);
		int subscribe = resultObj.getInteger("subscribe"); // 是否关注 1关注
		Member member = new Member();
		if (subscribe == 1) {
			String nickName = resultObj.getString("nickname");
			nickName = Common.filterEmojiString(nickName);
			int sex = resultObj.getInteger("sex");
			String city = resultObj.getString("city");
			String province = resultObj.getString("province");
			String country = resultObj.getString("country");
			String headimgurl = resultObj.getString("headimgurl");
			long subscribeTime = resultObj.getLong("subscribe_time");
			String subTime = DateUtil.parseTimeMillis2TimeStamp(subscribeTime * 1000L);
			member.setWxOpenId(openId);
			member.setNickname(nickName);
			member.setSex(sex);
			member.setCity(city);
			member.setProvince(province);
			member.setCountry(country);
			member.setHeadimgurl(headimgurl);
			member.setSubscribeTime(subTime);
		}
		return member;
	}
	
	/**
	 * 获取session中的用户
	 * @param request
	 * @param code
	 * @return
	 */
	public static Member getSessionMember(HttpServletRequest request, MemberDao memberDao){
		Member m ;
		HttpSession session = request.getSession();
		Object obj = session.getAttribute("openId");
		if(obj == null){
			String code = request.getParameter("code");
			if(StringUtils.isBlank(code)) return null;
			String openId = WeixinConfig.getOpenIdByCode(code);
			m = memberDao.findMemberByWxOpenId(openId);
			if(m == null) return null;
			session.setAttribute("openId", openId);
			session.setAttribute("mId", m.getMid());
		}else{
			m = memberDao.findMemberByWxOpenId(obj.toString());
		}
		return m;
	}

}
