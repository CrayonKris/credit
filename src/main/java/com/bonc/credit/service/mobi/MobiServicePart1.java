package com.bonc.credit.service.mobi;

import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author zhijie.ma
 * @date 2017年5月9日
 *
 */
@Service
public class MobiServicePart1 {
	private static Logger logger = Logger.getLogger(MobiServicePart1.class);

	/**
	 * 三元素验证 透传运营商结果，针对魔比需求 响应格式： {
	 * 
	 * “status”: “0”, “result” : "1" }
	 * 
	 * 代码status含义： 0:请求成功 1:调用失败
	 * 
	 * 代码result含义要求： 验证状态如下： 0 手机号无记录 1 手机号姓名一致，手机号身份证一致 2 手机号姓名一致，手机号身份证不一致 3
	 * 手机号姓名不一致，手机号身份证一致 4 手机号姓名不一致，手机号身份证不一 致 5 手机号非实名制
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String verifyUserIdCardInfoV2(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String idType = bizParams.containsKey("idType") ? bizParams.get("idType").toString() : "";
		String idCard = bizParams.containsKey("idCard") ? bizParams.get("idCard").toString() : "";
		if (null == idType || "".equals(idType))
			idType = "idCard";
		if (!"idCard".equals(idType)) {
			logger.info(mobile + " >> 暂不支持该类型");
			JSONObject ret = new JSONObject();
			ret.put("status", "1");
			ret.put("result", "暂不支持该类型");
			ret.put("isbilling", "0");

			ret.toString();
		}

		String userName = bizParams.containsKey("userName") ? bizParams.get("userName").toString() : "";
		userName = URLDecoder.decode(userName);

		if (null == mobile || "".equals(mobile) || "".equals(userName)) {
			logger.info(mobile + " >> 参数验证失败");

			JSONObject ret = new JSONObject();
			ret.put("status", "1");
			ret.put("result", "参数验证失败");
			ret.put("isbilling", "0");

			ret.toString();
		}

		String method = "queryIdNameMobile";

		long timestamp = System.currentTimeMillis();
		String random = String.valueOf(Math.random());

		String sign = MobiHelper.buildSign(timestamp, random);

		JSONObject reqParams = new JSONObject();
		reqParams.put("account", MobiHelper.account);
		reqParams.put("timestamp", timestamp);
		reqParams.put("random", random);
		reqParams.put("sign", sign);

		JSONObject data = new JSONObject();
		data.put("name", userName);
		data.put("id", idCard);
		data.put("phone", mobile);

		reqParams.put("data", data);

		// Map<String, String> params = new HashMap<String, String>();
		// params.put("data", reqParams.toString());

		String result = MobiHelper.httpPost(method, "data=" + reqParams.toString());
		logger.info(mobile + " >> 返回结果：" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", "三元素验证");
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		if ("error".equals(result)) {
			logger.info(mobile + " >> 请求超时");

			JSONObject ret = new JSONObject();
			ret.put("status", "1");
			ret.put("result", "error");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject jsonObj = JSONObject.parseObject(result);
		String status = jsonObj.getString("status");
		if ("0".equals(status)) {
			// 请求成功

			JSONObject ret = new JSONObject();
			String isbilling = "1";
			String retCode = "";

			String code = jsonObj.getString("result");
			if ("0".equals(code)) {
				// 手机号无记录，不收费
				isbilling = "0";
				retCode = code;
			} else if ("1".equals(code) || "2".equals(code) || "3".equals(code) || "4".equals(code)
					|| "5".equals(code)) {
				isbilling = "1"; // 收费
				retCode = code;
			} else {
				isbilling = "0"; // 不收费
				retCode = code;
			}

			ret.put("status", "0");
			ret.put("result", retCode);
			ret.put("isbilling", isbilling);
			return ret.toString();
		} else {
			// 失败，不收费
			String retCode = jsonObj.containsKey("result") ? jsonObj.getString("result") : "";
			JSONObject ret = new JSONObject();
			ret.put("status", "1");
			ret.put("result", "失败，不收费");
			ret.put("isbilling", "0");
			return ret.toString();
		}

	}

}
