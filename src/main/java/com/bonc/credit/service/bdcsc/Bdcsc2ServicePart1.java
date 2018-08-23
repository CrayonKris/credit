package com.bonc.credit.service.bdcsc;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;

/**
 * 电信云的账户2
 */
public class Bdcsc2ServicePart1 {
	private final static Logger logger = LoggerFactory.getLogger(Bdcsc2ServicePart1.class);
	private static final String host = "111.235.158.136";
	private static final int port = 18080;
	
	private static final String apikey = "1F0EA2EF590BAD391C095B14759A87ED";
	private static final String hashPwd = "B15A1A3F3D2A234ECC3E3A8C17124A2A";
	
	
	/**
	 * MEID号查最近一个月兴趣标签
	 * @param meid
	 * @param firstLabel
	 * @return
	 */
	public static String getHobbyLabelByMeidMonthly(JSONObject bizParams) {
		String meid = bizParams.containsKey("meid") ? bizParams.getString("meid") : "";
		String firstLabel = bizParams.containsKey("firstLabel") ? bizParams.getString("firstLabel") : "";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "MEID号查最近一个月兴趣标签";
		
		String isbilling = "1";
		try {
			String productId = "pm-label";
			String module = "hobby";
			String method = "hobbyLabelByMeidMonthly";
			
			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("meid", meid);
			reqMap.put("firstLabel", firstLabel);
			
			String reqUrl = getCommonUrl(productId, module, method, reqMap);
			logger.info(meid + " >> "+title+" | Url:" + reqUrl);
			
			//声明请求方式为  get方式  返回json串结果
			String result = HttpRequest.sendGet(reqUrl, null);
			logger.info(result);
			
//			JSONObject jsonObj = JSONObject.fromObject(result);
			JSONObject jsonObj = JSONObject.parseObject(result);
			
			if(jsonObj.containsKey("code")) {
				String code = jsonObj.getString("code");
				if("200".equals(code)) {
					String value = jsonObj.getJSONObject("data").getString("value");
					retCode = value;
					retDesc = "";
					if("mdn_not_exist".equalsIgnoreCase(value)) {
						retCode = "-1";
						retDesc = meid + "不存在";
					} else if("data_not_exist".equalsIgnoreCase(value)) {
						retCode = "-2";
						retDesc = "数据不存在";
					} else {
						retDesc = "兴趣标签和查询次数:" + value;
					}
					isbilling = "1";
					
				} else if("403".equals(code) || "401".equals(code)) {
//					resetCachedToken();
					retCode = "S0008";
					retDesc = "过于频繁，请稍候再试！";
					isbilling = "0";
				}  else {
					retCode = "B0001";
					retDesc = "调用失败";
					isbilling = "0";
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);
		
		return ret.toString();
	}
	
	/**
	 * 手机号码查最近一个月兴趣标签
	 * @param meid
	 * @param firstLabel
	 * @return
	 */
	public static String getHobbyLabelMonthly(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String firstLabel = bizParams.containsKey("firstLabel") ? bizParams.getString("firstLabel") : "";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码查最近一个月兴趣标签";
		
		String isbilling = "1";
		try {
			String productId = "pm-label";
			String module = "hobby";
			String method = "hobbyLabelMonthly";
			
			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("mdn", mobile);
			reqMap.put("firstLabel", firstLabel);
			
			String reqUrl = getCommonUrl(productId, module, method, reqMap);
			logger.info(mobile + " >> "+title+" | Url:" + reqUrl);
			
//			String result = HttpConnUtils.httpGetUrl(reqUrl);
			//声明请求方式为  get方式  返回json串结果
			String result = HttpRequest.sendGet(reqUrl, null);
			logger.info(result);
			
//			JSONObject jsonObj = JSONObject.fromObject(result);
			JSONObject jsonObj = JSONObject.parseObject(result);
			
			if(jsonObj.containsKey("code")) {
				String code = jsonObj.getString("code");
				if("200".equals(code)) {
					String value = jsonObj.getJSONObject("data").getString("value");
					retCode = value;
					retDesc = "";
					if("mdn_not_exist".equalsIgnoreCase(value)) {
						retCode = "-1";
						retDesc = mobile + "不存在";
						isbilling = "1";
					} else if("data_not_exist".equalsIgnoreCase(value)) {
						retCode = "-2";
						retDesc = "数据不存在";
						isbilling = "1";
					} else {
						retDesc = "兴趣标签和查询次数:" + value;
						isbilling = "1";
					}
					
				} else if("403".equals(code) || "401".equals(code)) {
//					resetCachedToken();
					retCode = "S0008";
					retDesc = "过于频繁，请稍候再试！";
					isbilling = "0";
				}  else {
					retCode = "B0001";
					retDesc = "调用失败";
					isbilling = "0";
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);
		
		return ret.toString();
	}
	
	public static String getCommonUrl(String productId, String module, String method, Map<String, String> bizParams) {
//		String tokenId = getToken();
		String tokenId = null;
		logger.info("token >> " + tokenId);
		if(null == tokenId || "".equals(tokenId)) {
			logger.info("获取token为空，重新获取！");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			tokenId = getToken();
		}
		
		String url = String.format("http://%s:%s/restful/%s/%s/%s/%s/%s.json", host, port,productId,module,method,apikey,tokenId);
		
		// 业务参数
		Map<String, String> params = new HashMap<String, String>();
		params.putAll(bizParams);
		params.put("type", "clear");
		
//		String queryParams = BdcscHelper.getUrlParams(params);
		String queryParams = HttpRequest.getUrlParams(params);
		String reqUrl = url;
		if(params.size() > 0) reqUrl = reqUrl + "?"+queryParams;
		
		logger.info(method + " >> " + reqUrl);
		return reqUrl;
	}
	
	private static Object lock = new Object();
	public static String refreshToken() {
		synchronized (lock) {
			// 获取公钥
//			String authCode = getPublicKey();
			String authCode = null;
			// 计算签名
			String sign = getSign(authCode);
			// 获取token
			//http://ip:18080/restful/system/token.xml?apiKey=xxx&authCode=xxx&sign=xxx
			String url = String.format("http://%s:%s/restful/system/token.json?apiKey=%s&authCode=%s&sign=%s", host, port,apikey,authCode,sign);
			//声明请求方式为  get方式  返回json串结果
			String result = HttpRequest.sendGet(url, null);
			if("error".equals(result)) {
				logger.info("访问超时, 获取token失败！");
				return null;
			}
			logger.info("刷新authCode: "+result);
			
			JSONObject jsonObj = JSONObject.parseObject(result);
			if(jsonObj.containsKey("code")) {
				String code = jsonObj.getString("code");
				if("200".equals(code) && "succeed".equalsIgnoreCase(jsonObj.getString("status"))) {
					if(jsonObj.containsKey("data")) {
						JSONObject data = jsonObj.getJSONObject("data");
						String tokenId = data.getString("token");
						return tokenId;
					}
				}
			}
			return null;
		}
	}
	
	/*public static synchronized String getToken() {
		String myKey = "bdcs2_bonc_key2_token";
		try {
			Object value = xmemcached.get(myKey);
			if(null != value && !"".equals(value)) return value.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String token = refreshToken();
		if(null != token) {
			xmemcached.set(myKey, token, 1140);
		} else {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return token;
	}
	
	/*public static synchronized void resetCachedToken() {
		String myKey = "bdcs2_bonc_key2_token";
		String token = refreshToken();
		if(null != token) {
			xmemcached.set(myKey, token, 1140);
		} 
	}*/
	
	public static String getSign(String authCode) {
		try {
			return MD5Builder.md5(apikey+hashPwd+authCode);
		} catch (Exception e) {
			logger.info("获取sign异常：" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/*public static String getPublicKey() {
		String url = String.format("http://%s:%s/restful/system/publicKey.json?apiKey=%s", host, port,apikey);
		String result = HttpConnUtils.httpGetUrl(url);
		if("error".equals(result)) {
			logger.info("访问超时, 获取公钥失败！");
			return null;
		}
		JSONObject jsonObj = JSONObject.fromObject(result);
		if(jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if("200".equals(code) && "succeed".equalsIgnoreCase(jsonObj.getString("status"))) {
				String publicKey = jsonObj.getString("data");
				return publicKey;
			}
		}
		logger.info("获取公钥失败");
		return null;
	}*/
	
	
}
