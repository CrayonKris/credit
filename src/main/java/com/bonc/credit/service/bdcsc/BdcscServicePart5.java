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
public class BdcscServicePart5 {
	private static final Logger logger = Logger.getLogger(BdcscServicePart5.class);

	@Autowired
	private BdcscHelper bdcscHelper;

	/**
	 * 最近3个月手机号码停机天数
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getSuspendDaysIn3MonthsLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "最近3个月手机号码停机天数";

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
		String method = "suspendDaysIn3MonthsLabel";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		if(bizParams.containsKey("province")){
			reqUrl = reqUrl + "&province=" + bizParams.getString("province");
		}
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		;
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
					retDesc = "0 单位：天";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(0,3] 单位：天";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(3,10] 单位：天";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(10,20] 单位：天";
				} else if ("e".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "(20,+] 单位：天";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// BdcscHelper.resetCachedToken();
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
	 * 座机号码-姓名二元组验证接口
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getFixedNoNameFlag(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String name = bizParams.containsKey("userName") ? bizParams.getString("userName") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "座机号码-姓名二元组验证接口";

		if (null == mobile || "".equals(mobile) || null == name || "".equals(name)) {
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
		String method = "fixedNoNameFlag";

		String nameHash = MD5Builder.md5(name);

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&nameHash=" + nameHash;
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
				} else if ("是".equalsIgnoreCase(value) || "0".equalsIgnoreCase(value)) {
					retCode = "0";
					retDesc = "0:验证一致";
				} else if ("否".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)) {
					retCode = "1";
					retDesc = "1:验证不一致";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// BdcscHelper.resetCachedToken();
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
	 * 入网时长阶梯获取（月）
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getTimeLengthForTNLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "入网时长阶梯获取（月）";

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
		String method = "timeLengthForTNLabel";

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
					retDesc = "(0,1] 单位：月";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[1,3) 单位：月";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[3,6) 单位：月";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[6,12) 单位：月";
				} else if ("e".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[12,24) 单位：月";
				} else if ("f".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[24,36) 单位：月";
				} else if ("g".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[36,+) 单位：月";
				}
			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// BdcscHelper.resetCachedToken();
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
	 * 手机号码最近一次启用时间
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getOpenDateLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码最近一次启用时间";

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
		String method = "openDateLabel";

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
				retDesc = "开通时间:" + value;
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				}

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// BdcscHelper.resetCachedToken();
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
	 * 集团客户验证
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getGroupNameFlag(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "集团客户验证";

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
		String method = "groupNameFlag";

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
				} else if ("0".equals(value)) {
					retCode = value;
					retDesc = "0：是集团用户";
				} else if ("1".equals(value)) {
					retCode = value;
					retDesc = "1：不是集团用户";
				}

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// BdcscHelper.resetCachedToken();
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
