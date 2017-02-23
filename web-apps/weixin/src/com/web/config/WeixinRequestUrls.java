package com.web.config;

public class WeixinRequestUrls {
	/**
	 * 获得用户ACCESS_TOKEN请求地址 ex:
	 * https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential
	 * &appid=wx53815fce4930ae44 &secret=a98759708ee61a453ac3f518fcb76a53
	 */
	public static final String GEN_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

	/**
	 * 获得关注用户列表，返回关注者的OpenID
	 * ex:https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID
	 */
	public static final String GEN_ATTEN_USER_URL = "https://api.weixin.qq.com/cgi-bin/user/get";

	/**
	 * 获取用户基本信息,返回用户基本信息
	 * ex:https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
	 */
	public static final String GEN_ATTEN_MEM_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info";
	
	/**
	 * 通过code换取网页授权access_token
	 */
	public static final String CODE_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
	
	/**
	 * 更新创新公众号菜单
	 */
	public static final String GEN_MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create";
}
