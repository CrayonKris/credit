package com.bonc.credit.service.bdcsc;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;

/**
 * 
 * @author zhijie.ma
 * @date 2017年5月3日
 *
 */
@Service
public class BdcscServicePart4 {
	private static final Logger logger = Logger.getLogger(BdcscServicePart4.class);

	@Autowired
	private BdcscHelper bdcscHelper;

	/**
	 * 获取指定月通话时长阶梯
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getTalkTimeLengthLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "获取指定月通话时长阶梯";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-communication";
		String method = "talkTimeLengthLabel";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&month=" + month;
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				String value = jsonObj.getJSONObject("data").getString("value");
				retCode = value;
				retDesc = "";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[0,50) 单位：分钟";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[50,100) 单位：分钟";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[100,200) 单位：分钟";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[200,400) 单位：分钟";
				} else if ("e".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[400,+) 单位：分钟";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";
				isbilling = "0";
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "0";
			} else if ("224".equals(code)) {
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机最近三个月呼转次数
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getCallForwardCount3MonthsLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机最近三个月呼转次数";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-communication";
		String method = "callForwardCount3MonthsLabel";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&month=" + month;
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				String value = jsonObj.getJSONObject("data").getString("value");
				retCode = value;
				retDesc = "";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "0 单位：次";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(0,3] 单位：次";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(3,10] 单位：次";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(10,20] 单位：次";
				} else if ("e".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(20,+) 单位：次";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";
				isbilling = "0";
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "0";
			} else if ("224".equals(code)) {
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 获取指定月主动通信活跃天数
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getCallDaysCommunicate(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "获取指定月主动通信活跃天数";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-communication";
		String method = "callDaysCommunicate";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&month=" + month;
		if(bizParams.containsKey("province")){
			reqUrl = reqUrl + "&province=" + bizParams.getString("province");
		}
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				String value = jsonObj.getJSONObject("data").getString("value");
				retCode = value;
				retDesc = "";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				}
				retDesc = "活跃天数:" + value;
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";
				isbilling = "0";
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "0";
			} else if ("224".equals(code)) {
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码主叫通话时长
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getCallTimeLengthLabelV2(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码主叫通话时长";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-communication";
		String method = "callTimeLengthForEGameLabel";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&month=" + month;
		if(bizParams.containsKey("province")){
			reqUrl = reqUrl + "&province=" + bizParams.getString("province");
		}
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				String value = jsonObj.getJSONObject("data").getString("value");
				retCode = value;
				retDesc = "";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "0 单位：分钟";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(0,10] 单位：分钟";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(10,30] 单位：分钟";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(30,60] 单位：分钟";
				} else if ("e".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(60,+) 单位：分钟";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";
				isbilling = "0";
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "0";
			} else if ("224".equals(code)) {
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码被叫通话时长
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getCalledTimeLengthLabelV2(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码被叫通话时长 ";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-communication";
		String method = "calledTimeLengthForEGameLabel";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&month=" + month;
		if(bizParams.containsKey("province")){
			reqUrl = reqUrl + "&province=" + bizParams.getString("province");
		}
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				String value = jsonObj.getJSONObject("data").getString("value");
				retCode = value;
				retDesc = "";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "0 单位：分钟";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(0,10] 单位：分钟";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(10,30] 单位：分钟";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(30,60] 单位：分钟";
				} else if ("e".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(60,+) 单位：分钟";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";
				isbilling = "0";
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "0";
			} else if ("224".equals(code)) {
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码-连续三个月缴费金额均值区间
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getPaymentIn3MonthsAvgLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码-连续三个月缴费金额均值区间 ";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-consume";
		String method = "paymentIn3MonthsAvgLabel";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&month=" + month;
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				String value = jsonObj.getJSONObject("data").getString("value");
				retCode = value;
				retDesc = "";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[0,20) 单位：元";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[20,50) 单位：元";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[50,100) 单位：元";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[100,200) 单位：元";
				} else if ("e".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[200,+) 单位：元";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";
				isbilling = "0";
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "0";
			} else if ("224".equals(code)) {
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * “手机号码”欠费金额/分级
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getOverdueBillLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码欠费金额/分级";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-consume";
		String method = "overdueBillLabel";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&month=" + month;
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				String value = jsonObj.getJSONObject("data").getString("value");
				retCode = value;
				retDesc = "";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "0 单位：元";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(0,50] 单位：元";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(50,100] 单位：元";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(100,+] 单位：元";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";
				isbilling = "0";
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "0";
			} else if ("224".equals(code)) {
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码-缴费总额阶梯（月度）
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getPaymentAmountLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码-缴费总额阶梯（月度） ";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-consume";
		String method = "paymentAmountLabel";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&month=" + month;
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				String value = jsonObj.getJSONObject("data").getString("value");
				retCode = value;
				retDesc = "";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[0,20) 单位：元";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[20,50) 单位：元";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[50,100) 单位：元";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[100,200) 单位：元";
				} else if ("e".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[200,+) 单位：元";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";
				isbilling = "0";
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "0";
			} else if ("224".equals(code)) {
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 当月余额
	 * 
	 * @return
	 *  
	 */
	public String getBalanceLabelV2(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "当月余额";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-consume";
		String method = "balanceForEGameLabel";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&month=" + month;
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				String value = jsonObj.getJSONObject("data").getString("value");
				retCode = value;
				retDesc = "";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "B0002";
					retDesc = "无数据";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "B0002";
					retDesc = "无数据";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "00 单位：元";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(0:5] 单位：元";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(5:10] 单位：元";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(10:15] 单位：元";
				} else if ("e".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(15:20] 单位：元";
				} else if ("f".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(20:25] 单位：元";
				} else if ("g".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(25:30] 单位：元";
				} else if ("h".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(30:100] 单位：元";
				} else if ("i".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(100,+] 单位：元";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";
				isbilling = "0";
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "0";
			} else if ("224".equals(code)) {
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * “手机号码”换机频率查询/分级
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getTerminalChangeFrequencyLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码换机频率查询/分级";

		if (null == mobile || "".equals(mobile)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-terminal";
		String method = "terminalChangeFrequencyLabel";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				String value = jsonObj.getJSONObject("data").getString("value");
				retCode = value;
				retDesc = "";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[0,1] 单位：次";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[2,4] 单位：次";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[4,+] 单位：次";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";
				isbilling = "0";
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "0";
			} else if ("224".equals(code)) {
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * “手机号码”终端型号查询
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getTerminalModelNumber(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码终端型号查询";

		if (null == mobile || "".equals(mobile)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-terminal";
		String method = "terminalModelNumber";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				String value = jsonObj.getJSONObject("data").getString("value");
				retCode = value;
				retDesc = "";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				}
				retCode = value;
				retDesc = "终端型号:" + value;
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";
				isbilling = "0";
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				retCode = "B0002";
				retDesc = "无数据";
				isbilling = "0";
			} else if ("224".equals(code)) {
				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";
			} else {
				retCode = "B0001";
				retDesc = "调用失败";
				isbilling = "0";
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码-证件类型-证件号码核验
	 * 
	 * @param mobile
	 * @param idType
	 * @param idCard
	 * @return
	 *  
	 */
	public String verifyUserIdCardNo(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.get("mobile").toString() : "";
		String idType = bizParams.containsKey("idType") ? bizParams.get("idType").toString() : "";
		String idCard = bizParams.containsKey("idCard") ? bizParams.get("idCard").toString() : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		if (null == idType || "".equals(idType))
			idType = "idCard";

		String title = "手机号码-证件类型-证件号码核验";

		if (null == mobile || "".equals(mobile)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String productId = "rp-label";
		String module = "rp-label-status";
		String method = "_verifyUserIdCardNo";

		String idNoHash = MD5Builder.md5(idCard);
		JSONObject p1 = new JSONObject();
		JSONObject p2 = new JSONObject();
		JSONObject p3 = new JSONObject();
		if (!"idCard".equals(idType) && !"passport".equals(idType)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			p1.put("code", "S0001");
			p1.put("desc", "参数错误");

			ret.put("idNoCheck", p1.toString());
			ret.put("idTypeCheck", p1.toString());
			ret.put("nameCheck", p1.toString());
			ret.put("isbilling", "0");
			return ret.toString();
		}
		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&idType=" + idType + "&idNoHash=" + idNoHash;
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				JSONObject ret = new JSONObject();

				JSONObject data = jsonObj.getJSONObject("data");
				String idNoCheckResult = data.getString("idNoCheckResult");
				String idTypeCheckResult = data.getString("idTypeCheckResult");

				String idNoCheckResultDesc = null;
				if ("-1".equals(idNoCheckResult)) {
					idNoCheckResultDesc = "身份证件号码验证结果: 不做验证（因idType验证不一致）";
				} else if ("0".equals(idNoCheckResult)) {
					idNoCheckResultDesc = "身份证件号码验证结果: 验证一致";
				} else if ("1".equals(idNoCheckResult)) {
					idNoCheckResultDesc = "身份证件号码验证结果: 验证不一致";
				} else {
					idNoCheckResult = "B0001";
				}

				String idTypeCheckResultDesc = null;
				if ("0".equals(idTypeCheckResult)) {
					idTypeCheckResultDesc = "身份证件类型验证结果: 验证一致";
				} else if ("1".equals(idTypeCheckResult)) {
					idTypeCheckResultDesc = "身份证件类型验证结果: 验证不一致";
				} else if ("2".equals(idTypeCheckResult)) {
					idTypeCheckResultDesc = "身份证件类型验证结果: 非中国电信手机号或号码不存在";
				} else {
					idTypeCheckResult = "B0001";
				}

				ret.put("interface", title);
				//
				p1.put("code", idNoCheckResult);
				p1.put("desc", idNoCheckResultDesc);
				ret.put("idNoCheck", p1.toString());

				p2.put("code", idTypeCheckResult);
				p2.put("desc", idTypeCheckResultDesc);
				ret.put("idTypeCheck", p2.toString());
				if ("B0001".equals(idTypeCheckResultDesc)) {
					ret.put("isbilling", "0");
				} else {
					ret.put("isbilling", "1");
				}

				return ret.toString();

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				JSONObject ret = new JSONObject();
				ret.put("interface", title);
				p1.put("code", "S0008");
				p1.put("desc", "过于频繁，请稍候再试！");

				ret.put("idNoCheck", p1.toString());
				ret.put("idTypeCheck", p1.toString());
				ret.put("isbilling", "0");
				return ret.toString();
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				JSONObject ret = new JSONObject();
				ret.put("interface", title);
				String retDesc = jsonObj.getString("message");
				logger.info(retDesc);

				p1.put("code", "B0002");
				p1.put("desc", "无数据");

				ret.put("idNoCheck", p1.toString());
				ret.put("idTypeCheck", p1.toString());
				ret.put("isbilling", "0");
				return ret.toString();
			} else if ("224".equals(code)) {
				JSONObject ret = new JSONObject();
				ret.put("interface", title);
				String retDesc = jsonObj.getString("message");
				logger.info(retDesc);

				p1.put("code", "B0003");
				p1.put("desc", "未知");

				ret.put("idNoCheck", p1.toString());
				ret.put("idTypeCheck", p1.toString());
				ret.put("isbilling", "1");
				return ret.toString();
			} else {
				JSONObject ret = new JSONObject();
				ret.put("interface", title);
				String retDesc = jsonObj.getString("message");

				p1.put("code", code);
				p1.put("desc", retDesc);

				ret.put("idNoCheck", p1.toString());
				ret.put("idTypeCheck", p1.toString());
				ret.put("isbilling", "0");
				return ret.toString();
			}
		}

		JSONObject ret = new JSONObject();
		JSONObject p11 = new JSONObject();
		ret.put("interface", title);
		p11.put("code", "B0001");
		p11.put("desc", "调用失败");

		ret.put("idNoCheck", p11.toString());
		ret.put("idTypeCheck", p11.toString());
		ret.put("isbilling", "0");
		return ret.toString();
	}

	/**
	 * 手机号码-证件类型-证件号码核验(电信联通及移动的合并版本支持)
	 * 
	 * @param mobile
	 * @param idType
	 * @param idCard
	 * @return
	 *  
	 */
	public String verifyUserIdCardNoFor3Net(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.get("mobile").toString() : "";
		String idType = bizParams.containsKey("idType") ? bizParams.get("idType").toString() : "";
		String idCard = bizParams.containsKey("idCard") ? bizParams.get("idCard").toString() : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		if (null == idType || "".equals(idType))
			idType = "idCard";

		String title = "手机号码-证件类型-证件号码核验";

		if (null == mobile || "".equals(mobile)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String productId = "rp-label";
		String module = "rp-label-status";
		String method = "_verifyUserIdCardNo";

		String idNoHash = MD5Builder.md5(idCard);
		if (!"idCard".equals(idType) && !"passport".equals(idType)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}
		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&idType=" + idType + "&idNoHash=" + idNoHash;
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0001");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		// JSONObject jsonObj = JSONObject.fromObject(result);
		JSONObject jsonObj = JSONObject.parseObject(result);

		if (jsonObj.containsKey("code")) {
			String code = jsonObj.getString("code");
			if ("200".equals(code)) {
				JSONObject ret = new JSONObject();

				JSONObject data = jsonObj.getJSONObject("data");
				String idNoCheckResult = data.getString("idNoCheckResult");
				String idTypeCheckResult = data.getString("idTypeCheckResult");

				String retCode = null;
				String retDesc = null;
				if ("0".equals(idNoCheckResult) && "0".equals(idTypeCheckResult)) {
					retCode = "0"; // 验证一致
					retDesc = "一致";
				} else if ("1".equals(idNoCheckResult) && "1".equals(idTypeCheckResult)) {
					retCode = "1"; // 验证不一致
					retDesc = "不一致";
				} else if ("-1".equals(idTypeCheckResult)) {
					// 非中国电信手机号或号码不存在
					retCode = "B0002";
					retDesc = "无数据";
				} else if ("-1".equals(idNoCheckResult)) {
					// 不做验证
					retCode = "B0003";
					retDesc = "未知";
				} else if ("1".equals(idNoCheckResult) || "1".equals(idTypeCheckResult)) {
					retCode = "1"; // 验证不一致
					retDesc = "不一致";
				} else {
					retCode = "B0003";
					retDesc = "未知";
				}

				ret.put("interface", title);
				ret.put("code", retCode);
				ret.put("desc", retDesc);
				ret.put("isbilling", "1");

				return ret.toString();

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				JSONObject ret = new JSONObject();

				ret.put("interface", title);
				ret.put("code", "S0008");
				ret.put("desc", "过于频繁，请稍候再试！");
				ret.put("isbilling", "0");
				return ret.toString();
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				JSONObject ret = new JSONObject();
				String retDesc = jsonObj.getString("message");
				logger.info(retDesc);

				ret.put("interface", title);
				ret.put("code", "B0002");
				ret.put("desc", "无数据");
				ret.put("isbilling", "0");
				return ret.toString();
			} else if ("224".equals(code)) {
				JSONObject ret = new JSONObject();
				String retDesc = jsonObj.getString("message");
				logger.info(retDesc);

				ret.put("interface", title);
				ret.put("code", "B0003");
				ret.put("desc", "未知");
				ret.put("isbilling", "1");
				return ret.toString();
			} else {
				JSONObject ret = new JSONObject();
				String retDesc = jsonObj.getString("message");

				ret.put("interface", title);
				ret.put("code", code);
				ret.put("desc", retDesc);
				ret.put("isbilling", "0");
				return ret.toString();
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", "B0001");
		ret.put("desc", "调用失败");
		ret.put("isbilling", "0");
		return ret.toString();
	}

}
