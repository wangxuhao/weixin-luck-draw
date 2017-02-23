package com.web.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dao.impl.MemberDao;
import com.pojo.Member;
import com.util.SignUtil;
import com.web.config.WeixinConfig;
import com.web.service.WeixinMemberService;

@Controller
public class WeixinAuthTokenAction {
	private static final Logger log = Logger.getLogger(WeixinAuthTokenAction.class);
	@Autowired
	private MemberDao memberDao;

	/**
	 * 供微信调用的token校验接口 - 将token、timestamp、nonce 三个参数进行排序 -
	 * 将三个参数字符串拼成一个字符串进行sha1加密 - 加密的字符串与signature对比 - 相同则返回 echostr
	 */
	@RequestMapping(value = "/**/verifedAuthToken.action")
	public void verifiedAuthToken(HttpServletRequest request, HttpServletResponse response) {
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");
		log.info("进入token校验: signature, timestamp, nonce, echostr: " + signature + " " + timestamp + " " + nonce + " "
				+ echostr);
		try {
			// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
			String token = WeixinConfig.getToken();
			if (SignUtil.checkSignature(signature, timestamp, nonce, token)) {
				sendAttMsg(request, response, echostr);
				log.info("token校验成功 ");
			} else {
				log.info("token校验失敗 ");
			}
		} catch (IOException e) {
			log.error(e.toString());
			e.printStackTrace();
		}
		handleAttenFans(request);
	}
	
	@ResponseBody
	@RequestMapping(value = "/**/updateMenu.action")
	public String updateMenu(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException{
		log.info("更新公众菜单界面 ");
		// 更新公众菜单界面
		return	WeixinConfig.updateMenu(request);
	}

	/**
	 * 处理关注人员是否已经添加到数据库，如果数据库中没有就保存到数据库
	 * 
	 * @param request
	 */
	private void handleAttenFans(HttpServletRequest request) {
		String openId = request.getParameter("openid");
		if (openId != null) {
			Member member = WeixinMemberService.getMemberInfoByOpenId(openId);
			if (member.getWxOpenId().equals(openId)) {
				/*Member findMember = memberDao.findMemberByWxOpenId(openId);
				if (null == findMember) {
					log.info("数据库中不包含此用户，openid..." + openId);
					memberDao.save(member);
				} else {
					log.info("数据库中已包含此用户，openid..." + openId);
				}*/
				memberDao.saveMemberFromWxAtten(member, openId);
			}
		}
	}
	
	/**
	 * 发送关注消息
	 * @return
	 * @throws IOException 
	 */
	private void sendAttMsg(HttpServletRequest request, HttpServletResponse response, String echostr) throws IOException {
		PrintWriter out;
		String openId = request.getParameter("openid");
		if (openId != null) {
			String account = WeixinConfig.getAccount();
			String content = WeixinConfig.getAttMsgInfo();
			echostr = "<xml><ToUserName><![CDATA["+openId+"]]></ToUserName>"+
				    "<FromUserName><![CDATA["+account+"]]></FromUserName>"+
					"<CreateTime>"+new Date().getTime()+"</CreateTime>"+
					"<MsgType><![CDATA[text]]></MsgType>"+
					"<Content><![CDATA["+content+"]]></Content>"+
					"</xml>";
		}
		response.setHeader("content-type", "text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		out.print(echostr);
	}
}
