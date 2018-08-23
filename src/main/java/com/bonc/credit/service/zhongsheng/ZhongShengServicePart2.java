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
public class ZhongShengServicePart2 {
	private static final Logger logger = Logger.getLogger(ZhongShengServicePart2.class);

	@Autowired
	private ZhongShengHelper zhongShengHelper;

	// public static void main(String[] args) {
	// JSONObject bizParams = new JSONObject();
	// bizParams.put("mobile", "18612198561");
	// bizParams.put("userName", "张三");
	// bizParams.put("idCard", "110105199009090018");
	//
	// String s = getCSM1611301635(bizParams);
	// System.out.println(s);
	// }

	/**
	 * 手机号码最近1个月内的平均消费区间(目前仅支持移动 2016.11.30)
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getCSM1611301635(JSONObject bizParams) {
		String title = "手机号码最近1个月内的平均消费区间";
		int rangeCode = 1;

		return getCSM1611301635(bizParams, title, rangeCode);
	}

	/**
	 * 手机号码最近3个月内的平均消费区间(目前仅支持移动 2016.11.30)
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getCSM161130_1456(JSONObject bizParams) {
		String title = "手机号码最近3个月内的平均消费区间";
		int rangeCode = 3;

		return getCSM1611301635(bizParams, title, rangeCode);
	}

	/**
	 * 手机号码最近6个月内的平均消费区间(目前仅支持移动 2016.11.30)
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getCSM161130_1518(JSONObject bizParams) {
		String title = "手机号码最近6个月内的平均消费区间";
		int rangeCode = 6;

		return getCSM1611301635(bizParams, title, rangeCode);
	}

	/**
	 * 手机号码最近12个月内的平均消费区间(目前仅支持移动 2016.11.30)
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getCSM161130_1532(JSONObject bizParams) {
		String title = "手机号码最近12个月内的平均消费区间";
		int rangeCode = 12;

		return getCSM1611301635(bizParams, title, rangeCode);
	}

	/**
	 * 手机号码平均消费区间(目前仅支持移动 2016.11.30)
	 * 
	 * @param bizParams
	 * @param title
	 * @param rangeCode
	 * @return
	 *  
	 */
	public String getCSM1611301635(JSONObject bizParams, String title, int rangeCode) {
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
		item.put("rangeCode", String.valueOf(rangeCode));
		item.put("cid", String.valueOf(System.currentTimeMillis()));

		JSONObject msgObj = new JSONObject();
		msgObj.put(mobile, item);

		String reqMsg = ZhongShengHelper.Base64Encode(msgObj.toString());

		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("action", "mobconsump");
		reqParams.put("token", token);
		reqParams.put("mid", "117");
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
				String status = body.getString("state");

				if ("A".equals(status)) {
					retCode = "A";
					retDesc = "A[0-50) 单位：元";
				} else if ("B".equals(status)) {
					retCode = "B";
					retDesc = "B[50-100) 单位：元";
				} else if ("C".equals(status)) {
					retCode = "C";
					retDesc = "C[100-150) 单位：元";
				} else if ("D".equals(status)) {
					retCode = "D";
					retDesc = "D[150-200) 单位：元";
				} else if ("E".equals(status)) {
					retCode = "E";
					retDesc = "E[200,250) 单位：元";
				} else if ("F".equals(status)) {
					retCode = "F";
					retDesc = "F[250,300) 单位：元";
				} else if ("G".equals(status)) {
					retCode = "G";
					retDesc = "G[300,+) 单位：元";
				} else {
					retCode = "B0003";
					retDesc = "未知";
				}
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

	/**
	 * 银行卡验证接口 返回信息粗略版本
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getCSM170123_1427(JSONObject bizParams) {
		String title = "银行卡验证接口";
		Map<String, String> zhongShengInformation = zhongShengHelper.getZhongShengInformation();
		String token = zhongShengInformation.get("token");
		String reqUrl = zhongShengInformation.get("callbackUrl");

		String idCardName = bizParams.getString("idCardName");
		String idCard = bizParams.getString("idCard");
		String bankCardNum = bizParams.getString("bankCardNum");
		String mobile = bizParams.getString("mobile");

		logger.info("*****手机号码****" + mobile + " >> token:" + token + " | url:" + reqUrl);
		logger.info("*****用户信息参数****" + bankCardNum + " >> 持卡人姓名:" + idCardName + " | 证件号码:" + idCard);

		/* 本接口业务参数 */
		if (null == mobile || "".equals(mobile) || null == idCardName || "".equals(idCardName) || null == idCard
				|| "".equals(idCard) || null == bankCardNum || "".equals(bankCardNum)) {
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
		item.put("idCardName", idCardName);
		item.put("idCard", idCard);
		item.put("bankCardNum", bankCardNum);
		item.put("account", ZhongShengHelper.account);
		item.put("mid", "61");
		item.put("action", "bkdprivate");
		item.put("sessionId", "");
		item.put("version", "1.0");
		item.put("idCardType", "00");
		item.put("bankCardType", "1");
		item.put("cid", String.valueOf(System.currentTimeMillis()));

		JSONObject msgObj = new JSONObject();
		// msgObj.put(mobile, item);
		// msgObj.put(idCardName, item);
		// msgObj.put(idCard, item);
		msgObj.put(bankCardNum, item);

		// 将msg的json串进行base64加密
		String reqMsg = ZhongShengHelper.Base64Encode(msgObj.toString());

		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("action", "bkdprivate");
		reqParams.put("token", token);
		reqParams.put("mid", "61");
		reqParams.put("msg", reqMsg);
		reqParams.put("f", "2"); // ?????

		logger.info("******请求的路径和json串******" + mobile + " request >> " + reqUrl + " | reqParams:" + reqParams);
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
			JSONObject body = JSONObject.parseObject(msgBody).getJSONObject(bankCardNum);
			String code = body.getString("statcode");
			String desc = ZhongShengHelper.getErrorDesc(code);
			logger.info("********" + mobile + " 响应信息 >> " + desc + " | code:" + code);

			String isbilling = "0";
			String retCode = "";
			String retDesc = "";
			if ("9100".equals(code)) {
				// 余额不足
				retCode = "B0004";
				retDesc = "其他错误";

			} else if ("9110".equals(code)) {
				// 无模块操作权限
				retCode = "S0007";
				retDesc = "接口未授权";

			} else if ("9200".equals(code)) {
				// 账户不存在
				retCode = "B0004";
				retDesc = "其他错误";

			} else if ("9201".equals(code)) {
				// 密码不正确
				retCode = "B0004";
				retDesc = "其他错误";

			} else if ("9202".equals(code)) {
				// 无权限查询(IP)限制
				retCode = "S0003";
				retDesc = "IP验证失败";

			} else if ("9300".equals(code)) {
				// 无效token
				retCode = "B0004";
				retDesc = "其他错误";

			} else if ("9301".equals(code)) {
				// 请求token太频繁
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试";

			} else if ("1200".equals(code)) {
				// 银行卡核查一致
				retCode = "0";
				retDesc = "验证一致";
				isbilling = "1";

			} else if ("1201".equals(code)) {
				// 银行卡核查不一致
				retCode = "1";
				retDesc = "验证不一致";
				isbilling = "1";

			} else if ("1203".equals(code)) {
				// 卡状态错误
				retCode = "B0004";
				retDesc = "其他错误";
				isbilling = "1";

			} else if ("1206".equals(code)) {
				// 验证失败
				retCode = "B0004";
				retDesc = "其他错误";
				isbilling = "2";

			} else if ("1290".equals(code)) {
				// 卡验证接口错误
				retCode = "B0004";
				retDesc = "其他错误";
				isbilling = "2";

			} else if ("1902".equals(code)) {
				// 无效请求MapKey
				retCode = "S0002";
				retDesc = "appKey不存在或已失效";

			} else if ("1903".equals(code)) {
				// 无效身份证号码
				retCode = "B0004";
				retDesc = "其他错误";

			} else if ("1904".equals(code)) {
				// 姓名不正确
				retCode = "B0004";
				retDesc = "其他错误";

			} else if ("1905".equals(code)) {
				// 无效相片
				retCode = "B0004";
				retDesc = "其他错误";

			} else if ("1906".equals(code)) {
				// 业务类型不正确
				retCode = "B0004";
				retDesc = "其他错误";

			} else if ("1907".equals(code)) {
				// 无效发生地
				retCode = "B0004";
				retDesc = "其他错误";

			} else if ("1910".equals(code)) {
				// 无效取值
				retCode = "B0004";
				retDesc = "其他错误";

			} else if ("1913".equals(code)) {
				// 无效手机号码
				retCode = "B0004";
				retDesc = "其他错误";

			} else if ("1920".equals(code)) {
				// 银行账号不正确
				retCode = "B0004";
				retDesc = "其他错误";

			} else {

				retCode = "B0001";
				retDesc = "调用失败,code不在范围内";

			}

			logger.info(mobile + " 平台应答 >> " + retCode + " | retDesc:" + retDesc);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);
			return ret.toString();

		} else if ("1000".equals(statcode)) {
			// 请求格式错误的响应码
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "1000");
			ret.put("desc", "请求格式错误的响应码");
			ret.put("isbilling", "0");
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
	 * 银行卡验证接口 返回信息详细版本
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getCSM170123_1427_2(JSONObject bizParams) {
		String title = "银行卡验证接口";
		Map<String, String> zhongShengInformation = zhongShengHelper.getZhongShengInformation();
		String token = zhongShengInformation.get("token");
		String reqUrl = zhongShengInformation.get("callbackUrl");

		String idCardName = bizParams.getString("idCardName");
		String idCard = bizParams.getString("idCard");
		String bankCardNum = bizParams.getString("bankCardNum");
		String mobile = bizParams.getString("mobile");

		logger.info("*****手机号码****" + mobile + " >> token:" + token + " | url:" + reqUrl);
		logger.info("*****用户信息参数****" + bankCardNum + " >> 持卡人姓名:" + idCardName + " | 证件号码:" + idCard);

		/* 本接口业务参数 */
		if (null == mobile || "".equals(mobile) || null == idCardName || "".equals(idCardName) || null == idCard
				|| "".equals(idCard) || null == bankCardNum || "".equals(bankCardNum)) {
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
		item.put("idCardName", idCardName);
		item.put("idCard", idCard);
		item.put("bankCardNum", bankCardNum);
		item.put("account", ZhongShengHelper.account);
		item.put("mid", "61");
		item.put("action", "bkdprivate");
		item.put("sessionId", "");
		item.put("version", "1.0");
		item.put("idCardType", "00");
		item.put("bankCardType", "1");
		item.put("cid", String.valueOf(System.currentTimeMillis()));

		JSONObject msgObj = new JSONObject();
		// msgObj.put(mobile, item);
		// msgObj.put(idCardName, item);
		// msgObj.put(idCard, item);
		msgObj.put(bankCardNum, item);

		// 将msg的json串进行base64加密
		String reqMsg = ZhongShengHelper.Base64Encode(msgObj.toString());

		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("action", "bkdprivate");
		reqParams.put("token", token);
		reqParams.put("mid", "61");
		reqParams.put("msg", reqMsg);
		reqParams.put("f", "2"); // ?????

		logger.info("******请求的路径和json串******" + mobile + " request >> " + reqUrl + " | reqParams:" + reqParams);
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
			JSONObject body = JSONObject.parseObject(msgBody).getJSONObject(bankCardNum);
			String code = body.getString("statcode");
			String desc = ZhongShengHelper.getErrorDesc(code);
			logger.info("********" + mobile + " 响应信息 >> " + desc + " | code:" + code);

			String isbilling = "0";
			String retCode = "";
			String retDesc = "";
			if ("9100".equals(code)) {
				// 余额不足
				retCode = "9100";
				retDesc = "余额不足";

			} else if ("9110".equals(code)) {
				// 无模块操作权限
				retCode = "9110";
				retDesc = "无模块操作权限";

			} else if ("9200".equals(code)) {
				// 账户不存在
				retCode = "9200";
				retDesc = "账户不存在";

			} else if ("9201".equals(code)) {
				// 密码不正确
				retCode = "9201";
				retDesc = "密码不正确";

			} else if ("9202".equals(code)) {
				// 无权限查询(IP)限制
				retCode = "9202";
				retDesc = "无权限查询(IP)限制";

			} else if ("9300".equals(code)) {
				// 无效token
				retCode = "9300";
				retDesc = "无效token";

			} else if ("9301".equals(code)) {
				// 请求token太频繁
				retCode = "9301";
				retDesc = "请求token太频繁";

			} else if ("1200".equals(code)) {
				// 银行卡核查一致
				retCode = "1200";
				retDesc = "银行卡核查一致";
				isbilling = "1";

			} else if ("1201".equals(code)) {
				// 银行卡核查不一致
				retCode = "1201";
				retDesc = "银行卡核查不一致";
				isbilling = "1";

			} else if ("1203".equals(code)) {
				// 卡状态错误
				retCode = "1203";
				retDesc = "卡状态错误";
				isbilling = "1";

			} else if ("1206".equals(code)) {
				// 验证失败
				retCode = "1206";
				retDesc = "验证失败";
				isbilling = "2";

			} else if ("1290".equals(code)) {
				// 卡验证接口错误
				retCode = "1290";
				retDesc = "卡验证接口错误";
				isbilling = "2";

			} else if ("1902".equals(code)) {
				// 无效请求MapKey
				retCode = "1902";
				retDesc = "无效请求MapKey";

			} else if ("1903".equals(code)) {
				// 无效身份证号码
				retCode = "1903";
				retDesc = "无效身份证号码";

			} else if ("1904".equals(code)) {
				// 姓名不正确
				retCode = "1904";
				retDesc = "其他错误";

			} else if ("1905".equals(code)) {
				// 无效相片
				retCode = "1905";
				retDesc = "其他错误";

			} else if ("1906".equals(code)) {
				// 业务类型不正确
				retCode = "1906";
				retDesc = "业务类型不正确";

			} else if ("1907".equals(code)) {
				// 无效发生地
				retCode = "1907";
				retDesc = "无效发生地";

			} else if ("1910".equals(code)) {
				// 无效取值
				retCode = "1910";
				retDesc = "无效取值";

			} else if ("1913".equals(code)) {
				// 无效手机号码
				retCode = "1913";
				retDesc = "无效手机号码";

			} else if ("1920".equals(code)) {
				// 银行账号不正确
				retCode = "1920";
				retDesc = "银行账号不正确";

			} else {

				retCode = "B0001";
				retDesc = "调用失败,code不在范围内";

			}

			logger.info(mobile + " 平台应答 >> " + retCode + " | retDesc:" + retDesc);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);
			return ret.toString();

		} else if ("1000".equals(statcode)) {
			// 请求格式错误的响应码
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "1000");
			ret.put("desc", "请求格式错误的响应码");
			ret.put("isbilling", "0");
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
	 * 银联个人评分
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String unionPayPersonal(JSONObject bizParams) {
		String title = "银联个人评分";
		Map<String, String> zhongShengInformation = zhongShengHelper.getZhongShengInformation();
		String token = zhongShengInformation.get("token");
		String reqUrl = zhongShengInformation.get("callbackUrl");

		String mobile = bizParams.getString("mobile");

		logger.info(mobile + " >> token:" + token + " | url:" + reqUrl);

		/* 本接口业务参数 */
		String bankNo = bizParams.getString("bankNo");
		String idNo = bizParams.getString("idNo");
		String idName = bizParams.getString("idName");

		logger.info("本接口业务参数为：银行卡号 >> " + bankNo + ",身份证号码>> " + idNo + ",姓名>> " + idName + ",手机号码>> " + mobile);

		if (null == mobile || "".equals(mobile) || null == bankNo || "".equals(bankNo) || null == idNo
				|| "".equals(idNo) || null == idName || "".equals(idName)) {
			logger.info(mobile + " >> 参数错误");
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		JSONObject item = new JSONObject();
		item.put("phone", mobile);
		item.put("idName", URLDecoder.decode(idName));
		item.put("bankNo", bankNo);
		item.put("idNo", idNo);
		item.put("scoreFieldsType", "2");
		item.put("scoreFields", "");
		item.put("cid", String.valueOf(System.currentTimeMillis()));

		JSONObject msgObj = new JSONObject();
		msgObj.put(bankNo, item);

		String reqMsg = ZhongShengHelper.Base64Encode(msgObj.toString());

		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("action", "upaperson");
		reqParams.put("token", token);
		reqParams.put("mid", "90");
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
			JSONObject body = JSONObject.parseObject(msgBody).getJSONObject(bankNo);
			String code = body.getString("statcode");
			String desc = ZhongShengHelper.getErrorDesc(code);
			logger.info(mobile + " 响应信息 >> " + desc + " | code:" + code);

			String isbilling = "0";
			String retCode = "";
			String retDesc = desc;

			logger.info(mobile + " 平台应答 >> " + retCode + " | retDesc:" + retDesc);

			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", body.toString());
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
}
