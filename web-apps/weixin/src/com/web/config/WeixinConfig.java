package com.web.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.util.FileUtils;
import com.util.HttpClientUtil;

/**
 * 微信配置类
 * 
 * @author wangxh
 *
 */
public class WeixinConfig {
	private static final Logger log = Logger.getLogger(WeixinConfig.class);
	// weixin appid, 需要在启动脚本中配置
	private static final String APPID = "WEIXIN_APPID";
	// weixin appsecret, 需要在启动脚本中配置
	private static final String APPSECRET = "WEIXIN_APPSECRET";
	// weixin token，需要在启动脚本中配置
	private static final String TOKEN = "WEIXIN_TOKEN";
	// weixin 消息加解密密钥, 在启动脚本中配置
	private static final String ENCODINGAESKEY = "WEIXIN_ENCODINGAESKEY";
	// weixin 微信号, 在启动脚本中配置
	private static final String ACCOUNT = "WEIXIN_ACCOUNT";
	// 记录用户access_token
	private static String accessToken;
	// 记录用户最后一次获取accesstoken时间（一次获取access_token可以使用2小时，7200s）
	private static long lastGenAccessTokenTime;

	public static String getAppId() {
		String appId = System.getenv(APPID);
		if (StringUtils.isBlank(appId)) {
			log.error("无效的appid, 请使用 环境变量 '" + APPID + "' 进行设置");
			throw new RuntimeException("无效的appid, 请使用 环境变量 '" + APPID + "' 进行设置");
		}
		return appId;
	}

	public static String getAppSecret() {
		String appSecret = System.getenv(APPSECRET);
		if (StringUtils.isBlank(appSecret)) {
			log.error("无效的appsecret, 请使用 环境变量 '" + APPSECRET + "' 进行设置");
			throw new RuntimeException("无效的appsecret, 请使用 环境变量 '" + APPSECRET + "' 进行设置");
		}
		return appSecret;
	}

	public static String getToken() {
		String token = System.getenv(TOKEN);
		if (StringUtils.isBlank(token)) {
			log.error("无效的tonken, 请使用 环境变量 '" + TOKEN + "' 进行设置");
			throw new RuntimeException("无效的tonken, 请使用 环境变量 '" + TOKEN + "' 进行设置");
		}
		return token;
	}

	public static String getEncodingAESKey() {
		String encodingAESKey = System.getenv(ENCODINGAESKEY);
		if (StringUtils.isBlank(encodingAESKey)) {
			log.error("无效的encodingAESKey, 请使用 环境变量 '" + ENCODINGAESKEY + "' 进行设置");
			throw new RuntimeException("无效的encodingAESKey, 请使用 环境变量 '" + ENCODINGAESKEY + "' 进行设置");
		}
		return encodingAESKey;
	}
	
	public static String getAccount() {
		String account = System.getenv(ACCOUNT);
		if (StringUtils.isBlank(account)) {
			log.error("无效的account, 请使用 环境变量 '" + ACCOUNT + "' 进行设置");
			throw new RuntimeException("无效的account, 请使用 环境变量 '" + ACCOUNT + "' 进行设置");
		}
		return account;
	}

	public static String getAccessToken() {
		return getAccessToken(false);
	}

	public static String getAccessToken(boolean needNew) {
		long tokenUsedTime = System.currentTimeMillis() - lastGenAccessTokenTime;
		if (StringUtils.isBlank(accessToken) || needNew || tokenUsedTime >= 7200 * 1000) {
			accessToken = genAccessToken();
		}
		return accessToken;
	}

	private static String genAccessToken() {
		lastGenAccessTokenTime = System.currentTimeMillis();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("appid", getAppId());
		params.put("secret", getAppSecret());
		String urlWithParams = WeixinRequestUrls.GEN_ACCESS_TOKEN_URL + "?grant_type=client_credential&appid="
				+ params.get("appid") + "&secret=" + params.get("secret");
		String responseInfo = HttpClientUtil.requestGet(urlWithParams);
		JSONObject obj = (JSONObject) JSONObject.parse(responseInfo);
		String accessToken = (String) obj.get("access_token");
		return accessToken;
	}

	public static String getOpenIdByCode(String code) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("appid", getAppId());
		m.put("secret", getAppSecret());
		m.put("code", code);
		m.put("grant_type", "authorization_code");
		String accessToken = HttpClientUtil.postHttp(WeixinRequestUrls.CODE_ACCESS_TOKEN_URL, m);
		String openId = JSONObject.parseObject(accessToken).get("openid").toString();
		return openId;
	}

	/**
	 * 创建更新菜单
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static String updateMenu(HttpServletRequest request) throws FileNotFoundException  {
		String accessToken = genAccessToken();
		File json_file = FileUtils.getResourceFile(WeixinConfig.class, "/web/json/Menu.json");
		log.info("获取Menu.json文件，配置菜单界面");
		InputStream is = new FileInputStream(json_file);
		String urlWithToken = WeixinRequestUrls.GEN_MENU_CREATE_URL+"?access_token="+accessToken;
		String json =  FileUtils.convertStreamToString(is);
		return HttpClientUtil.postJson(urlWithToken, json);
	}

	/**
	 * 获取用户关注后推送的消息
	 * @return
	 */
	public static String getAttMsgInfo() {
		String appid = getAppId();
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid
				+ "&redirect_uri=http://weixin.yinlibaohe.com/Weixin/web/showBidUserInfoView.action&"
				+ "response_type=code&scope=snsapi_base&state=123#wechat_redirect";
		return "您好，欢迎您关注凝聚玉湖纵览星辰。 请先<a href='" + redirectUrl + "'>信息绑定</a>,然后进行抽奖";
	}
}
