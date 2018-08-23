package com.bonc.credit.service.bdcsc;

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
public class BdcscServicePart2 {
	private static final Logger logger = Logger.getLogger(BdcscServicePart2.class);

	@Autowired
	private CreditService creditService;

	@Autowired
	private BdcscHelper bdcscHelper;

	/**
	 * 异常交往指数得分（主动/被动联系黑名单号码池）
	 * 
	 * @param mobile
	 * @return
	 * 
	 */
	public String getAbnormalContactScore(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "异常交往指数得分（主动/被动联系黑名单号码池）";

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
		String method = "abnormalContactScore";

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
				retDesc = value;
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
	 * 手机号码漫游城市列表
	 * 
	 * @param mobile
	 * @return
	 * 
	 */
	public String getRoamCityList(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码漫游城市列表";

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
		String method = "roamCityList";

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
				retDesc = value;
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else {
					String[] array = retCode.split(",");
					StringBuffer buff = new StringBuffer();
					for (String s : array) {
						String cityName = creditService.getCityNameByCode(s);
						buff.append(cityName).append(",");
					}
					String s = buff.toString();
					if (s.endsWith(",")) {
						s = s.substring(0, s.length() - 1);
						retDesc = s;
					}

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
	 * 获取通话时长在前20%联系人的平均被叫时长
	 * 
	 * @param mobile
	 * @return
	 * 
	 */
	public String getCalledTimeLengthAvg20Ptg(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "获取通话时长在前20%联系人的平均被叫时长";

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
		String method = "calledTimeLengthAvg20Ptg";

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
				retDesc = value + "分钟";
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
	 * 获取指定月通话天数最多的城市
	 * 
	 * @param mobile
	 * @return
	 * 
	 */
	public String getCallMaxDaysCity(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "获取指定月通话天数最多的城市";

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
		String method = "callMaxDaysCity";

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
				retDesc = value;
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else {
					retDesc = creditService.getCityNameByCode(value);
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
	 * 获取TOP5联系人主叫通话次数
	 * 
	 * @param mobile
	 * @return
	 * 
	 */
	public String getCallCountTop5Label(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "获取TOP5联系人主叫通话次数";

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
		String method = "callCountTop5Label";

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
				retDesc = value;
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retDesc = "[0, 6)";
				} else if ("b".equalsIgnoreCase(value)) {
					retDesc = "[6, 10)";
				} else if ("c".equalsIgnoreCase(value)) {
					retDesc = "[10, +)";
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
	 * 最近3个月手机号码停机次数
	 * 
	 * @param mobile
	 * @return
	 * 
	 */
	public String getSuspendTimesIn3MonthsLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "最近3个月手机号码停机次数";

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
		String method = "suspendTimesIn3MonthsLabel";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
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
				retDesc = value;
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retDesc = "0";
				} else if ("b".equalsIgnoreCase(value)) {
					retDesc = " (0,3]";
				} else if ("c".equalsIgnoreCase(value)) {
					retDesc = "(3,5]";
				} else if ("d".equalsIgnoreCase(value)) {
					retDesc = "(5,+ ]";
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
	 * 最后一次通话所在城市
	 * 
	 * @param mobile
	 * @return
	 * 
	 */
	public String getLastTalkCity(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "最后一次通话所在城市";

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
		String method = "lastTalkCity";

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
				retDesc = value;
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else {
					retDesc = creditService.getCityNameByCode(value);
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
	 * “手机号码-证件类型-证件号码-姓名”核验
	 * 
	 * @param mobile
	 * @param idType
	 * @param idCard
	 * @param userName
	 * @return
	 * 
	 */
	public String verifyUserIdCardInfo(String mobile, String idType, String idCard, String userName,String type) {
		String title = "手机号码-证件类型-证件号码-姓名核验";

		String isbilling = "1";
		String productId = "rp-label";
		String module = "rp-label-status";
		String method = "_verifyUserIdCardInfo";

		String idNoHash = MD5Builder.md5(idCard);
		String nameHash = MD5Builder.md5(userName);
		JSONObject p1 = new JSONObject();
		JSONObject p2 = new JSONObject();
		JSONObject p3 = new JSONObject();
		if (!"idCard".equals(idType) && !"passport".equals(idType)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			p1.put("code", "S0001");
			p1.put("desc", "参数错误");
			ret.put("isbilling", "0");

			ret.put("idNoCheck", p1.toString());
			ret.put("idTypeCheck", p1.toString());
			ret.put("nameCheck", p1.toString());
			return ret.toString();
		}
		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&idType=" + idType + "&idNoHash=" + idNoHash + "&nameHash=" + nameHash;
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
				String nameCheckResult = data.getString("nameCheckResult");

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

				String nameCheckResultDesc = null;
				if ("-1".equals(nameCheckResult)) {
					nameCheckResultDesc = "姓名验证结果: 不做验证（因idType或idNo验证不一致）";
				} else if ("0".equals(nameCheckResult)) {
					nameCheckResultDesc = "姓名验证结果: 验证一致";
				} else if ("1".equals(nameCheckResult)) {
					nameCheckResultDesc = "姓名验证结果: 验证不一致";
				} else {
					nameCheckResult = "B0001";
				}

				ret.put("interface", title);
				ret.put("isbilling", "1");
				//
				p1.put("code", idNoCheckResult);
				p1.put("desc", idNoCheckResultDesc);
				ret.put("idNoCheck", p1.toString());

				p2.put("code", idTypeCheckResult);
				p2.put("desc", idTypeCheckResultDesc);
				ret.put("idTypeCheck", p2.toString());

				p3.put("code", nameCheckResult);
				p3.put("desc", nameCheckResultDesc);
				ret.put("nameCheck", p3.toString());

				return ret.toString();

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				JSONObject ret = new JSONObject();

				p1.put("code", "S0008");
				p1.put("desc", "过于频繁，请稍候再试！");

				ret.put("idNoCheck", p1.toString());
				ret.put("idTypeCheck", p1.toString());
				ret.put("nameCheck", p1.toString());
				ret.put("isbilling", "0");
				return ret.toString();
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				JSONObject ret = new JSONObject();
				String retDesc = jsonObj.getString("message");
				logger.info(retDesc);

				p1.put("code", "B0002");
				p1.put("desc", "无数据");
				ret.put("isbilling", "0");

				ret.put("idNoCheck", p1.toString());
				ret.put("idTypeCheck", p1.toString());
				ret.put("nameCheck", p1.toString());
				return ret.toString();
			} else if ("224".equals(code)) {
				JSONObject ret = new JSONObject();
				String retDesc = jsonObj.getString("message");
				logger.info(retDesc);

				p1.put("code", "B0003");
				p1.put("desc", "未知");
				ret.put("isbilling", "1");

				ret.put("idNoCheck", p1.toString());
				ret.put("idTypeCheck", p1.toString());
				ret.put("nameCheck", p1.toString());
				return ret.toString();
			} else {
				JSONObject ret = new JSONObject();
				String retDesc = jsonObj.getString("message");
				logger.info(retDesc);

				p1.put("code", "B0001");
				p1.put("desc", "调用失败");
				ret.put("isbilling", "0");

				ret.put("idNoCheck", p1.toString());
				ret.put("idTypeCheck", p1.toString());
				ret.put("nameCheck", p1.toString());
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
		ret.put("nameCheck", p11.toString());
		ret.put("isbilling", "0");
		return ret.toString();
	}

	/**
	 * “手机号码-证件类型-证件号码-姓名”核验
	 * 
	 * @param mobile
	 * @param idCard
	 *            身份证
	 * @return
	 * 
	 */
	public String verifyUserIdCardInfo(String mobile, String idCard, String userName,String type) {
		return verifyUserIdCardInfo(mobile, "idCard", idCard, userName, type);
	}

	/**
	 * “手机号码”在网时长/分级
	 * 
	 * @param mobile
	 * @return
	 * 
	 */
	public String getTimeLengthLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码-在网时长/分级";

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
		String method = "timeLengthLabel";

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
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[0-6) 单位：月";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[6-12) 单位：月";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[12-24) 单位：月";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[24-36) 单位：月";
				} else if ("e".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[36,+) 单位：月";
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
	 * “手机号码”自然人接入号码个数/分级
	 * 
	 * @param mobile
	 * @return
	 * 
	 */
	public String getAccessNumberCountLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码自然人接入号码个数/分级";

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
		String method = "accessNumberCountLabel";

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
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
				} else if ("0".equalsIgnoreCase(value)) {
					retDesc = "大于2人";
				} else if ("1".equalsIgnoreCase(value)) {
					retDesc = "不大于2人";
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

}
