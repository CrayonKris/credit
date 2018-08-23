package com.bonc.credit.service.zhongsheng;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.HttpRequest;

/**
 * 
 * @author zhijie.ma
 * @date 2017年5月8日
 *
 */
@Service
public class ZhongShengServicePart1 {
	private static final Logger logger = Logger.getLogger(ZhongShengServicePart1.class);

	// public static void main(String[] args) {
	// JSONObject bizParams = new JSONObject();
	// bizParams.put("mobile", "18612198561");
	// bizParams.put("userName", "张三");
	// bizParams.put("idCard", "110105199009090018");
	//
	// String s = verifyUserName(bizParams);
	// System.out.println(s);
	// }

	@Autowired
	private ZhongShengHelper zhongShengHelper;

	/**
	 * 手机号码-证件类型-证件号码-姓名核验（本接口，仅支持移动）
	 * 
	 * @return
	 *  
	 */
	public String verifyUserIdCardInfoV2(JSONObject bizParams) {
		String title = "手机号码-证件类型-证件号码-姓名核验";
		Map<String, String> zhongShengInformation = zhongShengHelper.getZhongShengInformation();
		String token = zhongShengInformation.get("token");
		String reqUrl = zhongShengInformation.get("callbackUrl");

		String mobile = bizParams.getString("mobile");

		logger.info(mobile + " >> token:" + token + " | url:" + reqUrl);

		/* 本接口业务参数 */
		String idCard = bizParams.getString("idCard");
		String userName = bizParams.getString("userName");
		String idType = bizParams.containsKey("idType") ? bizParams.getString("idType") : "idCard";
		if ("idCard".equals(idType))
			idType = "0101";
		else {
			logger.info(mobile + " >> 本接口暂不支持其证件类型");
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "本接口暂不支持其证件类型");
			ret.put("isbilling", "0");
			return ret.toString();
		}
		if (null == mobile || "".equals(mobile) || null == userName || "".equals(userName) || null == idCard
				|| "".equals(idCard)) {
			logger.info(mobile + " >> 参数错误");
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject item = new JSONObject();
		item.put("mobile", mobile);
		item.put("name", URLDecoder.decode(userName));
		item.put("idType", idType);
		item.put("idNo", idCard);
		item.put("cid", String.valueOf(System.currentTimeMillis()));

		JSONObject msgObj = new JSONObject();
		msgObj.put(mobile, item);

		String reqMsg = ZhongShengHelper.Base64Encode(msgObj.toString());

		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("action", "mobid4");
		reqParams.put("token", token);
		reqParams.put("mid", "112");
		reqParams.put("msg", reqMsg);
		reqParams.put("f", "2");

		logger.info(mobile + " request >> " + reqUrl + " | reqParams:" + reqParams);
		// String response = HttpHelper.httPost(reqUrl, reqParams);
		String urlParams = HttpRequest.getUrlParams(reqParams);
		String response = HttpRequest.sendPost(reqUrl, urlParams);

		if (response == null || response.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject resMap = JSONObject.parseObject(response);
		String msg = resMap.getString("msg");
		String msgBody = ZhongShengHelper.Base64Decode(msg);
		String statcode = resMap.getString("statcode");

		logger.info(mobile + " response >> " + response + " | msg:" + msgBody);

		if ("100".equals(statcode)) {
			// 响应成功
			JSONObject body = JSONObject.parseObject(msgBody).getJSONObject(mobile);
			String code = body.getString("statcode");
			String desc = ZhongShengHelper.getErrorDesc(code);
			logger.info(mobile + " 响应信息 >> " + desc + " | code:" + code);

			String isbilling = "1";
			String retCode = "";
			String retDesc = "";
			if ("1500".equals(code)) {
				// 认证通过
				retCode = "0";
				retDesc = "验证一致";
				isbilling = "1";
			} else if ("1501".equals(code)) {
				// 认证未通过
				retCode = "1";
				retDesc = "验证不一致";
				isbilling = "1";
			} else if ("1502".equals(code)) {
				// 号码状态有误
				retCode = "-2";
				retDesc = "不做验证";
				isbilling = "1";
			} else if ("1503".equals(code)) {
				// 查无数据
				retCode = "2";
				retDesc = "无数据";
				isbilling = "1";
			} else if ("1505".equals(code)) {
				// 查得数据
				retCode = "-2";
				retDesc = "不做验证";
				isbilling = "1";
			} else if ("1510".equals(code)) {
				// 在网状态正常
				retCode = "-2";
				retDesc = "不做验证";
				isbilling = "1";
			} else if ("1511".equals(code)) {
				// 在网状态停机
				retCode = "-2";
				retDesc = "不做验证";
				isbilling = "1";
			} else if ("1512".equals(code)) {
				// 在网但不可用
				retCode = "-2";
				retDesc = "不做验证";
				isbilling = "1";
			} else if ("1513".equals(code)) {
				// 销号/未启用
				retCode = "2";
				retDesc = "无数据";
				isbilling = "1";
			} else if ("1590".equals(code)) {
				// 认证接口错误
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			} else if ("1591".equals(code)) {
				// 不支持查询
				retCode = "2";
				retDesc = "无数据";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}

			logger.info(mobile + " 平台应答 >> " + retCode + " | retDesc:" + retDesc);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);
			return ret.toString();

		} else {
			String desc = ZhongShengHelper.getErrorDesc(statcode);
			boolean isBilling = ZhongShengHelper.IsBilling(statcode);

			logger.info(mobile + " 响应错误信息 >> " + desc + " | isBilling:" + isBilling);

			String isBilling2 = isBilling ? "1" : "0";
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", isBilling2);
			return ret.toString();
		}
	}

	/**
	 * 手机号码-证件类型-证件号码核验(目前支持联通电信)
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String verifyUserIdCardNoFor3Net(JSONObject bizParams) {
		String title = "手机号码-证件类型-证件号码核验";
		Map<String, String> zhongShengInformation = zhongShengHelper.getZhongShengInformation();
		String token = zhongShengInformation.get("token");
		String reqUrl = zhongShengInformation.get("callbackUrl");

		String mobile = bizParams.getString("mobile");

		logger.info(mobile + " >> token:" + token + " | url:" + reqUrl);

		/* 本接口业务参数 */
		String idCard = bizParams.getString("idCard");
		String idType = bizParams.containsKey("idType") ? bizParams.getString("idType") : "idCard";
		if ("idCard".equals(idType))
			idType = "0101";
		else {
			logger.info(mobile + " >> 本接口暂不支持其证件类型");
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "本接口暂不支持其证件类型");
			ret.put("isbilling", "0");
			return ret.toString();
		}
		if (null == mobile || "".equals(mobile) || null == idCard || "".equals(idCard)) {
			logger.info(mobile + " >> 参数错误");
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject item = new JSONObject();
		item.put("mobile", mobile);
		item.put("idType", idType);
		item.put("idNo", idCard);
		item.put("cid", String.valueOf(System.currentTimeMillis()));

		JSONObject msgObj = new JSONObject();
		msgObj.put(mobile, item);

		String reqMsg = ZhongShengHelper.Base64Encode(msgObj.toString());

		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("action", "mobid");
		reqParams.put("token", token);
		reqParams.put("mid", "110");
		reqParams.put("msg", reqMsg);
		reqParams.put("f", "2");

		logger.info(mobile + " request >> " + reqUrl + " | reqParams:" + reqParams);
		String urlParams = HttpRequest.getUrlParams(reqParams);
		String response = HttpRequest.sendPost(reqUrl, urlParams);

		if (response == null || response.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject resMap = JSONObject.parseObject(response);
		String msg = resMap.getString("msg");
		String msgBody = ZhongShengHelper.Base64Decode(msg);
		String statcode = resMap.getString("statcode");

		logger.info(mobile + " response >> " + response + " | msg:" + msgBody);

		if ("100".equals(statcode)) {
			// 响应成功
			JSONObject body = JSONObject.parseObject(msgBody).getJSONObject(mobile);
			String code = body.getString("statcode");
			String desc = ZhongShengHelper.getErrorDesc(code);
			logger.info(mobile + " 响应信息 >> " + desc + " | code:" + code);

			String isbilling = "1";
			String retCode = "";
			String retDesc = "";
			if ("1500".equals(code)) {
				// 认证通过
				retCode = "0";
				retDesc = "验证一致";
				isbilling = "1";
			} else if ("1501".equals(code)) {
				// 认证未通过
				retCode = "1";
				retDesc = "验证不一致";
				isbilling = "1";
			} else if ("1502".equals(code)) {
				// 号码状态有误
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else if ("1503".equals(code)) {
				// 查无数据
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "1";
			} else if ("1505".equals(code)) {
				// 查得数据
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else if ("1510".equals(code)) {
				// 在网状态正常
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else if ("1511".equals(code)) {
				// 在网状态停机
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else if ("1512".equals(code)) {
				// 在网但不可用
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else if ("1513".equals(code)) {
				// 销号/未启用
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "1";
			} else if ("1590".equals(code)) {
				// 认证接口错误
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			} else if ("1591".equals(code)) {
				// 不支持查询
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}

			logger.info(mobile + " 平台应答 >> " + retCode + " | retDesc:" + retDesc);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);
			return ret.toString();

		} else {
			String desc = ZhongShengHelper.getErrorDesc(statcode);
			boolean isBilling = ZhongShengHelper.IsBilling(statcode);
			String isBilling2 = isBilling ? "1" : "0";
			logger.info(mobile + " 响应错误信息 >> " + desc + " | isBilling:" + isBilling);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", isBilling2);
			return ret.toString();
		}
	}

	/**
	 * “手机号码”在网时长/分级
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getTimeLengthLabel(JSONObject bizParams) {
		String title = "手机号码-在网时长/分级";
		Map<String, String> zhongShengInformation = zhongShengHelper.getZhongShengInformation();
		String token = zhongShengInformation.get("token");
		String reqUrl = zhongShengInformation.get("callbackUrl");

		String mobile = bizParams.getString("mobile");

		logger.info(mobile + " >> token:" + token + " | url:" + reqUrl);

		/* 本接口业务参数 */
		if (null == mobile || "".equals(mobile)) {
			logger.info(mobile + " >> 参数错误");
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject item = new JSONObject();
		item.put("mobile", mobile);
		item.put("cid", String.valueOf(System.currentTimeMillis()));

		JSONObject msgObj = new JSONObject();
		msgObj.put(mobile, item);

		String reqMsg = ZhongShengHelper.Base64Encode(msgObj.toString());

		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("action", "mobinnet");
		reqParams.put("token", token);
		reqParams.put("mid", "114");
		reqParams.put("msg", reqMsg);
		reqParams.put("f", "2");

		logger.info(mobile + " request >> " + reqUrl + " | reqParams:" + reqParams);
		String urlParams = HttpRequest.getUrlParams(reqParams);
		String response = HttpRequest.sendPost(reqUrl, urlParams);

		if (response == null || response.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject resMap = JSONObject.parseObject(response);
		String msg = resMap.getString("msg");
		String msgBody = ZhongShengHelper.Base64Decode(msg);
		String statcode = resMap.getString("statcode");

		logger.info(mobile + " response >> " + response + " | msg:" + msgBody);

		if ("100".equals(statcode)) {
			// 响应成功
			JSONObject body = JSONObject.parseObject(msgBody).getJSONObject(mobile);
			String code = body.getString("statcode");
			String desc = ZhongShengHelper.getErrorDesc(code);
			logger.info(mobile + " 响应信息 >> " + desc + " | code:" + code);

			String isbilling = "1";
			String retCode = "";
			String retDesc = "";
			if ("1503".equals(code)) {
				// 查无数据
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "1";
			} else if ("1505".equals(code) || "0".equals(code) || "100".equals(code)) {
				// 查得数据
				String status = body.getString("state");
				if ("A".equals(status)) {
					retCode = "A";
					retDesc = "A[0-6) 单位：月";
				} else if ("B".equals(status)) {
					retCode = "B";
					retDesc = "B[6-12) 单位：月";
				} else if ("C".equals(status)) {
					retCode = "C";
					retDesc = "C[12-24) 单位：月";
				} else if ("D".equals(status)) {
					retCode = "D";
					retDesc = "D[24-36) 单位：月";
				} else if ("E".equals(status)) {
					retCode = "E";
					retDesc = "E[36,+) 单位：月";
				} else {
					retCode = "B0003";
					retDesc = "未知";
				}
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}

			logger.info(mobile + " 平台应答 >> " + retCode + " | retDesc:" + retDesc);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);
			return ret.toString();

		} else {
			String desc = ZhongShengHelper.getErrorDesc(statcode);
			boolean isBilling = ZhongShengHelper.IsBilling(statcode);
			String isBilling2 = isBilling ? "1" : "0";
			logger.info(mobile + " 响应错误信息 >> " + desc + " | isBilling:" + isBilling);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", isBilling2);
			return ret.toString();
		}
	}

	/**
	 * 手机号码当前状态查询
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getPhoneNumState(JSONObject bizParams) {
		String title = "手机号码当前状态查询";
		Map<String, String> zhongShengInformation = zhongShengHelper.getZhongShengInformation();

		String token = zhongShengInformation.get("token");
		String reqUrl = zhongShengInformation.get("callbackUrl");

		String mobile = bizParams.getString("mobile");

		logger.info(mobile + " >> token:" + token + " | url:" + reqUrl);

		/* 本接口业务参数 */
		if (null == mobile || "".equals(mobile)) {
			logger.info(mobile + " >> 参数错误");
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject item = new JSONObject();
		item.put("mobile", mobile);
		item.put("cid", String.valueOf(System.currentTimeMillis()));

		JSONObject msgObj = new JSONObject();
		msgObj.put(mobile, item);

		String reqMsg = ZhongShengHelper.Base64Encode(msgObj.toString());

		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("action", "mobstat");
		reqParams.put("token", token);
		reqParams.put("mid", "116");
		reqParams.put("msg", reqMsg);
		reqParams.put("f", "2");

		logger.info(mobile + " request >> " + reqUrl + " | reqParams:" + reqParams);
		String urlParams = HttpRequest.getUrlParams(reqParams);
		String response = HttpRequest.sendPost(reqUrl, urlParams);

		if (response == null || response.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject resMap = JSONObject.parseObject(response);
		String msg = resMap.getString("msg");
		String msgBody = ZhongShengHelper.Base64Decode(msg);
		String statcode = resMap.getString("statcode");

		logger.info(mobile + " response >> " + response + " | msg:" + msgBody);

		if ("100".equals(statcode)) {
			// 响应成功
			JSONObject body = JSONObject.parseObject(msgBody).getJSONObject(mobile);
			String code = body.getString("statcode");
			String desc = ZhongShengHelper.getErrorDesc(code);
			logger.info(mobile + " 响应信息 >> " + desc + " | code:" + code);

			String isbilling = "1";
			String retCode = "";
			String retDesc = "";
			if ("1502".equals(code)) {
				// 号码状态有误
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "1";
			} else if ("1503".equals(code)) {
				// 查无数据
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "1";
			} else if ("1505".equals(code)) {
				// 查得数据
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else if ("1510".equals(code)) {
				// 在网状态正常
				retCode = "1";
				retDesc = "正常在用";
				isbilling = "1";
			} else if ("1511".equals(code)) {
				// 在网状态停机
				retCode = "2";
				retDesc = "停机";
				isbilling = "1";
			} else if ("1512".equals(code)) {
				// 在网但不可用
				retCode = "3";
				retDesc = "在网但不可用";
				isbilling = "1";
			} else if ("1513".equals(code)) {
				// 销号/未启用
				retCode = "4";
				retDesc = "不在网";
				isbilling = "1";
			} else if ("1590".equals(code)) {
				// 认证接口错误
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			} else if ("1591".equals(code)) {
				// 不支持查询
				retCode = "2";
				retDesc = "无数据";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}

			logger.info(mobile + " 平台应答 >> " + retCode + " | retDesc:" + retDesc);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);
			return ret.toString();

		} else {
			String desc = ZhongShengHelper.getErrorDesc(statcode);
			boolean isBilling = ZhongShengHelper.IsBilling(statcode);
			String isBilling2 = isBilling ? "1" : "0";
			logger.info(mobile + " 响应错误信息 >> " + desc + " | isBilling:" + isBilling);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", isBilling2);
			return ret.toString();
		}
	}

	/**
	 * 手机号码-姓名校验
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String verifyUserName(JSONObject bizParams) {
		String title = "手机号码-姓名校验";
		Map<String, String> zhongShengInformation = zhongShengHelper.getZhongShengInformation();
		String token = zhongShengInformation.get("token");
		String reqUrl = zhongShengInformation.get("callbackUrl");

		String mobile = bizParams.getString("mobile");

		logger.info(mobile + " >> token:" + token + " | url:" + reqUrl);

		/* 本接口业务参数 */
		String userName = bizParams.getString("userName");

		if (null == mobile || "".equals(mobile) || null == userName || "".equals(userName)) {
			logger.info(mobile + " >> 参数错误");
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject item = new JSONObject();
		item.put("mobile", mobile);
		item.put("name", URLDecoder.decode(userName));
		item.put("cid", String.valueOf(System.currentTimeMillis()));

		JSONObject msgObj = new JSONObject();
		msgObj.put(mobile, item);

		String reqMsg = ZhongShengHelper.Base64Encode(msgObj.toString());

		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("action", "mobidnm");
		reqParams.put("token", token);
		reqParams.put("mid", "104");
		reqParams.put("msg", reqMsg);
		reqParams.put("f", "2");

		logger.info(mobile + " request >> " + reqUrl + " | reqParams:" + reqParams);
		String urlParams = HttpRequest.getUrlParams(reqParams);
		String response = HttpRequest.sendPost(reqUrl, urlParams);

		if (response == null || response.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject resMap = JSONObject.parseObject(response);
		String msg = resMap.getString("msg");
		String msgBody = ZhongShengHelper.Base64Decode(msg);
		String statcode = resMap.getString("statcode");

		logger.info(mobile + " response >> " + response + " | msg:" + msgBody);

		if ("100".equals(statcode)) {
			// 响应成功
			JSONObject body = JSONObject.parseObject(msgBody).getJSONObject(mobile);
			String code = body.getString("statcode");
			String desc = ZhongShengHelper.getErrorDesc(code);
			logger.info(mobile + " 响应信息 >> " + desc + " | code:" + code);

			String isbilling = "1";
			String retCode = "";
			String retDesc = "";
			if ("1500".equals(code)) {
				// 认证通过
				retCode = "0";
				retDesc = "验证一致";
				isbilling = "1";
			} else if ("1501".equals(code)) {
				// 认证未通过
				retCode = "1";
				retDesc = "验证不一致";
				isbilling = "1";
			} else if ("1502".equals(code)) {
				// 号码状态有误
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "1";
			} else if ("1503".equals(code)) {
				// 查无数据
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "1";
			} else if ("1505".equals(code)) {
				// 查得数据
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else if ("1510".equals(code)) {
				// 在网状态正常
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else if ("1511".equals(code)) {
				// 在网状态停机
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else if ("1512".equals(code)) {
				// 在网但不可用
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else if ("1513".equals(code)) {
				// 销号/未启用
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else if ("1590".equals(code)) {
				// 认证接口错误
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			} else if ("1591".equals(code)) {
				// 不支持查询
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}

			logger.info(mobile + " 平台应答 >> " + retCode + " | retDesc:" + retDesc);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);
			return ret.toString();

		} else {
			String desc = ZhongShengHelper.getErrorDesc(statcode);
			boolean isBilling = ZhongShengHelper.IsBilling(statcode);
			String isBilling2 = isBilling ? "1" : "0";
			logger.info(mobile + " 响应错误信息 >> " + desc + " | isBilling:" + isBilling);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", isBilling2);
			return ret.toString();
		}
	}
}
