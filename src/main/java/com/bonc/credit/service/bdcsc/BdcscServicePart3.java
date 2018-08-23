package com.bonc.credit.service.bdcsc;

import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.service.CreditService;
import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;

/**
 * 
 * @author zhijie.ma
 * @date 2017年5月3日
 *
 */
@Service
public class BdcscServicePart3 {
	private static final Logger logger = Logger.getLogger(BdcscServicePart3.class);

	@Autowired
	private CreditService creditService;

	@Autowired
	private BdcscHelper bdcscHelper;

	/**
	 * “手机号码”当前状态查询
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getPhoneNumState(JSONObject bizParams) {
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码当前状态查询";
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
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
		String module = "rp-label-status";
		String method = "state";

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
				retDesc = value;
				if ("1".equalsIgnoreCase(value)) {
					retDesc = "正常在用";
				} else if ("2".equalsIgnoreCase(value)) {
					retDesc = "停机";
				} else if ("3".equalsIgnoreCase(value)) {
					retDesc = "在网但不可用";
				} else if ("4".equalsIgnoreCase(value)) {
					retDesc = "不在网";
				} else if ("9".equalsIgnoreCase(value)) {
					retDesc = "无法查询";
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
	 * “手机号码-性别”获取
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getUserGender(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码-性别";

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
		String module = "rp-label-info";
		String method = "userGender";

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
				retDesc = value;
				if ("男".equalsIgnoreCase(value)) {
					retCode = "0";
					retDesc = "男";
				} else if ("女".equalsIgnoreCase(value)) {
					retCode = "1";
					retDesc = "女";
				} else {
					retCode = "2";
					retDesc = "未知";
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
	 * “手机号码-姓名”核验
	 * 
	 * @param mobile
	 * @param userName
	 * @return
	 *  
	 */
	public String getVerifyUserName(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String userName = bizParams.containsKey("userName") ? bizParams.get("userName").toString() : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		userName = URLDecoder.decode(userName);

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码-姓名核验";

		if (null == mobile || "".equals(mobile) || null == userName || "".equals(userName)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-status";
		String method = "_verifyUserName";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&nameHash=" + MD5Builder.md5(userName);
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
				retDesc = value;
				if ("0".equalsIgnoreCase(value)) {
					retCode = "0";
					retDesc = "验证一致";
				} else if ("1".equalsIgnoreCase(value)) {
					retCode = "1";
					retDesc = "验证不一致";
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
	 * 手机号码-会员级别
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getMembershipLevel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码-会员级别";

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
		String module = "rp-label-info";
		String method = "membershipLevel";

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
				retDesc = value;
				if ("-1".equalsIgnoreCase(value)) {
					retDesc = "未知";
				} else if ("1000".equalsIgnoreCase(value)) {
					retDesc = "钻";
				} else if ("1100".equalsIgnoreCase(value)) {
					retDesc = "金";
				} else if ("1200".equalsIgnoreCase(value)) {
					retDesc = "银";
				} else if ("9999".equalsIgnoreCase(value)) {
					retDesc = "其他";
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
	 * 手机号码-归属省市
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getProvinceCity(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码-归属省市";

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
		String module = "rp-label-info";
		String method = "provinceCity";

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
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else {
					retCode = value;
					// 根据城市代码，获取城市名称
					retDesc = creditService.getCityNameByCode(value);
					if (null == retDesc || "".equals(retDesc))
						retDesc = value;
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
	 * “手机号码”日均上网时长/分级
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getFlowTimeLengthAvgLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码日均上网时长/分级";

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
		String module = "rp-label-flow";
		String method = "flowTimeLengthAvgLabel";

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
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[0,30) 单位：分钟";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[30,60) 单位：分钟";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[60,180) 单位：分钟";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[180,+) 单位：分钟";
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
	 * 手机号码月上网时长/分级
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getFlowTimeLengthLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码月上网时长/分级";

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
		String module = "rp-label-flow";
		String method = "flowTimeLengthLabel";

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
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[0,1) 单位：小时";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[1,3) 单位：小时";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[3,10) 单位：小时";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[10,+) 单位：小时";
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
	 * 指定月份流量
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getFlowForEGameLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "指定月份流量";

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
		String module = "rp-label-flow";
		String method = "flowForEGameLabel";

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
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[0,5) 单位：MB";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[5,20) 单位：MB";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[20,100) 单位：MB";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[100,300) 单位：MB";
				} else if ("e".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[300,+) 单位：MB";
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
	 * “手机号码”最近三月通话时长的均值/分级
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getTalkTimeLengthIn3MonthsAvgLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码最近三月通话时长的均值/分级";

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
		String method = "talkTimeLengthIn3MonthsAvgLabel";

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
	 * 手机号码-指定月凌晨时段通话时长占比得分
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getTalkTimeLengthDawnPtgScore(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码-指定月凌晨时段通话时长占比得分";

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
		String method = "talkTimeLengthDawnPtgScore";

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
				retDesc = "计算规则得分:" + value;
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
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
	 * “手机号码”最近三个月漫游次数/分级
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getRoamCountIn3MonthsLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码最近三个月漫游次数/分级";

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
		String method = "roamCountIn3MonthsLabel";

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
					retDesc = "[0,1) 单位：次";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[1,10) 单位：次";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[11,50) 单位：次";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[50,+) 单位：次";
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
	 * 获取指定月份手机通话集中度得分
	 * 
	 * @param mobile
	 * @param month
	 * @return
	 *  
	 */
	public String getTalkFocusScore(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "获取指定月份手机通话集中度得分";

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
		String method = "talkFocusScore";

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
				}
				retDesc = "通话集中度得分:" + value;
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

}
