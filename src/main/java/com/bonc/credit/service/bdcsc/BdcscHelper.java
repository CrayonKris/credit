package com.bonc.credit.service.bdcsc;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bonc.redis.RedisUtil;
import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;
import com.bonc.util.ProjectErrorInformation;

/**
 * 
 * @author zhijie.ma
 * @date 2017年5月3日
 *
 */
@Service
public class BdcscHelper {
	private static final Logger logger = Logger.getLogger(BdcscHelper.class);
	public static final String HOST = "111.235.158.136";
	public static final int PORT = 18080;
	public static final String TYPE = "json";
	public static final String APIKEY = "58872D9257706537585B61FEAF492CA3";
	public static final String HASHPWD = "F742F25E75163256DD738AF3BC84C653";

	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 按照验证过程进行请求接口
	 * 
	 * @param productId
	 * @param module
	 * @param method
	 * @param mobile
	 * @param type
	 * @return
	 * @
	 */
	public String getRuquestUrl(String productId, String module, String method, String mobile, String type) {

		String tokenId = redisUtil.getString("bdcscTokenId");

		if (null == tokenId || "".equals(tokenId)) {
			logger.info("获取token为空，重新获取！");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tokenId = getToken();
			redisUtil.setStringTime("bdcscTokenId", tokenId, 10 * 60);

		}

		logger.info("获取到的token为：" + tokenId);

		String reqUrl = String.format("http://%s:%s/restful/%s/%s/%s/%s/%s.json", HOST, PORT, productId, module, method,
				APIKEY, tokenId);

		Map<String, String> params = new HashMap<String, String>();
		params.put("mdn", mobile); // 13388010005
		params.put("type", type);

		String queryParams = HttpRequest.getUrlParams(params);

		if (params.size() > 0)
			reqUrl = reqUrl + "?" + queryParams;

		logger.info(mobile + " >> " + reqUrl);
		return reqUrl;
	}

	/**
	 * 获取到的token，进行接口调用
	 * 
	 * @return
	 * @
	 */
	public String getToken() {
		String url = "http://111.235.158.136:18080/restful/system/publicKey.json";
		String param = "apiKey=" + APIKEY;
		String authCode = HttpRequest.sendGet(url, param);

		logger.info("***获取authCode***" + authCode);

		// JSONObject fromObject = JSONObject.fromObject(authCode);
		JSONObject fromObject = JSONObject.parseObject(authCode);

		String data = fromObject.getString("data");

		String sign = APIKEY + HASHPWD + data;

		String signMd5 = MD5Builder.md5(sign);

		String url2 = "http://111.235.158.136:18080/restful/system/token.json";
		String param2 = "apiKey=" + APIKEY + "&authCode=" + data + "&sign=" + signMd5;

		String tokenId = HttpRequest.sendGet(url2, param2);

		// JSONObject fromObject2 = JSONObject.fromObject(tokenId);
		JSONObject fromObject2 = JSONObject.parseObject(tokenId);
		JSONObject jsonObject = fromObject2.getJSONObject("data");
		String token = jsonObject.getString("token");

		return token;
	}

}
