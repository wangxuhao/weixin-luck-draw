package com.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient工具类，暂时只提供post请求方式
 * 
 * @author wangxh
 *
 */
public class HttpClientUtil {
	private static final Log log = LogFactory.getLog(HttpClientUtil.class);

	private static final int socketTimeout = 5000;// 请求超时时间
	private static final int connectTimeout = 5000;// 传输超时时间

	/**
	 * get请求方法
	 * 
	 * @param urlWithParams
	 * @return
	 * @throws Exception
	 */
	public static String requestGet(String urlWithParams) {
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		HttpGet httpget = new HttpGet(urlWithParams);
		// 配置请求的超时设置
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
				.setConnectTimeout(connectTimeout).build();
		httpget.setConfig(requestConfig);

		CloseableHttpResponse response;
		String responseInfo = "";
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			responseInfo = EntityUtils.toString(entity, "UTF-8");
		} catch (ClientProtocolException e) {
			log.error("getHttp error in the HTTP protocol ... " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("getHttp error in the IO ... " + e.getMessage());
			e.printStackTrace();
		} finally {
			httpget.releaseConnection();
		}
		return responseInfo;
	}

	/**
	 * 请求服务方法
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @return 返回内容
	 */
	public static String postHttp(String url, Map<String, String> params) {
		CloseableHttpClient httpClient = getHttpClient();
		HttpPost httpPost = postForm(url, params);
		CloseableHttpResponse httpResponse = null;
		String resultInfo = "";
		try {
			httpResponse = httpClient.execute(httpPost);
			resultInfo = parseResponse(httpResponse);
		} catch (ClientProtocolException e) {
			log.error("postHttp error in the HTTP protocol ... " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("postHttp error in the IO ... " + e.getMessage());
			e.printStackTrace();
		} finally {
			closeHttpClient(httpClient);
		}
		return resultInfo;
	}
	
	/**
	 * 传送json数据请求服务
	 * @param url
	 * @param json
	 * @return
	 */
	public static String postJson(String url, String json){
		CloseableHttpClient httpClient = getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = new StringEntity(json, "utf-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		CloseableHttpResponse httpResponse = null;
		String resultInfo = "";
		try {
			httpResponse = httpClient.execute(httpPost);
			resultInfo = parseResponse(httpResponse);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultInfo;
	}

	private static CloseableHttpClient getHttpClient() {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
		return closeableHttpClient;
	}

	/**
	 * HTTP form请求方式
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	private static HttpPost postForm(String url, Map<String, String> params) {
		HttpPost post = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error("post form error ... " + e.getMessage());
			e.printStackTrace();
		}
		return post;
	}

	/**
	 * 获取返回值
	 * 
	 * @param response
	 * @return
	 */
	private static String parseResponse(CloseableHttpResponse response) {
		HttpEntity entity = response.getEntity();
		String resultInfo = "";
		System.out.println("响应状态码:" + response.getStatusLine());
		try {
			resultInfo = EntityUtils.toString(entity).replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		} catch (Exception e) {
			log.error("parseResponse error ... " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					log.error("parseResponse error in the IO ... " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return resultInfo;
	}

	/**
	 * 关闭连接
	 * 
	 * @param client
	 */
	private static void closeHttpClient(CloseableHttpClient client) {
		if (client != null) {
			try {
				client.close();
			} catch (IOException e) {
				log.error("closeHttpClient error in the IO ... " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
