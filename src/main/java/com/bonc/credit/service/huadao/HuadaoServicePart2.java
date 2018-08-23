package com.bonc.credit.service.huadao;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.DocHelper;
import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;

/**
 * 
 * @author zhijie.ma
 * @date 2017年5月9日
 *
 */
@Service
public class HuadaoServicePart2 extends HuadaoHelper {
	private static final Logger logger = Logger.getLogger(HuadaoServicePart2.class);

	/**
	 * "手机号码"单月话费分级
	 * 
	 * @param bizParams
	 * @return @
	 */
	public String balanceLabel(JSONObject bizParams) {
		String code = "B0001";
		String desc = "调用失败";
		String title = "手机号码单月话费分级";
		String mobile = bizParams.getString("mobile");
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String method = "unicomSinglePhoneScoreService/getScore";
		String url = baseUrl2 + method;

		String sign = MD5Builder.md5(account + mobile + month + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", mobile);
		params.put("date", month);
		params.put("account", account);
		params.put("sign", sign);

		String reqUrl = HttpRequest.getUrl(params, url);

		logger.info(reqUrl);

		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info(mobile + " >> 返回结果：" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		Element root = DocHelper.getRootElement(result);
		Map<String, Object> docMap = DocHelper.toMap(root);
		if (docMap.containsKey("result")) {
			Map<String, Object> resultMap = (Map<String, Object>) docMap.get("result");
			if (resultMap.containsKey("info")) {
				Map<String, Object> infoMap = (Map<String, Object>) resultMap.get("info");
				String score = infoMap.get("score").toString().toUpperCase();
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.get("statusDesc").toString();

				logger.info(mobile + " >> score:" + score + " | status:" + status + " | statusDesc:" + statusDesc);

				if ("0".equals(status)) {
					// 查询成功
					if ("0".equals(score)) {
						code = "A";
						desc = "A:0";
					} else if ("40".equals(score)) {
						code = "B";
						desc = "B:(0,40]";
					} else if ("80".equals(score)) {
						code = "C";
						desc = "C:(40,80]";
					} else if ("160".equals(score)) {
						code = "D";
						desc = "D:(80,160]";
					} else if ("999".equals(score)) {
						code = "E";
						desc = "E:(160,+]";
					} else {
						code = "B0003";
						desc = "未知";
					}
					isbilling = "1";
				} else if ("-1".equals(status)) {
					// 调用失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else if ("1".equals(status)) {
					// 无数据
					code = "B0002";
					desc = "无数据";
					isbilling = "0";
				} else if ("sign".equals(status)) {
					// 校验失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else {
					code = "S0006";
					desc = "解析错误";
					isbilling = "0";
				}
			}
		}
		logger.info(mobile + " >> code：" + code + " | desc:" + desc);

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", code);
		ret.put("desc", desc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码主叫通话时长
	 * 
	 * @param bizParams
	 * @return @
	 */
	public String CSM161120_1041(JSONObject bizParams) {
		String code = "B0001";
		String desc = "调用失败";
		String title = "手机号码主叫通话时长";
		String mobile = bizParams.getString("mobile");
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String method = "unicomSingleVoiceScoreService/getSingleVoiceScore";
		String url = baseUrl2 + method;

		String sign = MD5Builder.md5(account + mobile + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", mobile);
		params.put("selectMonth", month);
		params.put("account", account);
		params.put("sign", sign);

		String reqUrl = HttpRequest.getUrl(params, url);

		logger.info(reqUrl);

		String urlParams = HttpRequest.getUrlParams(params);
		String result = HttpRequest.sendPost(url, urlParams);
		logger.info(mobile + " >> 返回结果：" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		Element root = DocHelper.getRootElement(result);
		Map<String, Object> docMap = DocHelper.toMap(root);
		if (docMap.containsKey("result")) {
			Map<String, Object> resultMap = (Map<String, Object>) docMap.get("result");
			if (resultMap.containsKey("info")) {
				Map<String, Object> infoMap = (Map<String, Object>) resultMap.get("info");
				String score = infoMap.get("callerscore").toString().toUpperCase();
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.containsKey("statusdesc") ? infoMap.get("statusdesc").toString() : "";

				logger.info(mobile + " >> score:" + score + " | status:" + status + " | statusDesc:" + statusDesc);

				if ("0".equals(status)) {
					// 查询成功
					if ("0".equals(score)) {
						code = "A";
						desc = "A:0 单位：分钟";
					} else if ("30".equals(score)) {
						code = "B";
						desc = "B:(0,30] 单位：分钟";
					} else if ("90".equals(score)) {
						code = "C";
						desc = "C:(30,90] 单位：分钟";
					} else if ("270".equals(score)) {
						code = "D";
						desc = "D:(90,270] 单位：分钟";
					} else if ("999".equals(score)) {
						code = "E";
						desc = "E:(270,+] 单位：分钟";
					} else {
						code = "B0003";
						desc = "未知";
					}
					isbilling = "1";
				} else if ("-1".equals(status)) {
					// 调用失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else if ("1".equals(status)) {
					// 无数据
					code = "B0002";
					desc = "无数据";
					isbilling = "0";
				} else if ("sign".equals(status)) {
					// 校验失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else {
					code = "S0006";
					desc = "解析错误";
					isbilling = "0";
				}
			}
		}
		logger.info(mobile + " >> code：" + code + " | desc:" + desc);

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", code);
		ret.put("desc", desc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码被叫通话时长
	 * 
	 * @param bizParams
	 * @return @
	 */
	public String CSM161120_1606(JSONObject bizParams) {
		String code = "B0001";
		String desc = "调用失败";
		String title = "手机号码被叫通话时长";
		String mobile = bizParams.getString("mobile");
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String method = "unicomSingleVoiceScoreService/getSingleVoiceScore";
		String url = baseUrl2 + method;

		String sign = MD5Builder.md5(account + mobile + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", mobile);
		params.put("selectMonth", month);
		params.put("account", account);
		params.put("sign", sign);

		String reqUrl = HttpRequest.getUrl(params, url);

		logger.info(reqUrl);

		String urlParams = HttpRequest.getUrlParams(params);
		String result = HttpRequest.sendPost(url, urlParams);
		logger.info(mobile + " >> 返回结果：" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		Element root = DocHelper.getRootElement(result);
		Map<String, Object> docMap = DocHelper.toMap(root);
		if (docMap.containsKey("result")) {
			Map<String, Object> resultMap = (Map<String, Object>) docMap.get("result");
			if (resultMap.containsKey("info")) {
				Map<String, Object> infoMap = (Map<String, Object>) resultMap.get("info");
				String score = infoMap.get("calledscore").toString().toUpperCase();
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.containsKey("statusdesc") ? infoMap.get("statusdesc").toString() : "";

				logger.info(mobile + " >> score:" + score + " | status:" + status + " | statusDesc:" + statusDesc);

				if ("0".equals(status)) {
					// 查询成功
					if ("0".equals(score)) {
						code = "A";
						desc = "A:0 单位：分钟";
					} else if ("40".equals(score)) {
						code = "B";
						desc = "B:(0,40] 单位：分钟";
					} else if ("120".equals(score)) {
						code = "C";
						desc = "C:(40,120] 单位：分钟";
					} else if ("360".equals(score)) {
						code = "D";
						desc = "D:(120,360] 单位：分钟";
					} else if ("999".equals(score)) {
						code = "E";
						desc = "E:(360,+] 单位：分钟";
					} else {
						code = "B0003";
						desc = "未知";
					}
					isbilling = "1";
				} else if ("-1".equals(status)) {
					// 调用失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else if ("1".equals(status)) {
					// 无数据
					code = "B0002";
					desc = "无数据";
					isbilling = "0";
				} else if ("sign".equals(status)) {
					// 校验失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else {
					code = "S0006";
					desc = "解析错误";
					isbilling = "0";
				}
			}
		}
		logger.info(mobile + " >> code：" + code + " | desc:" + desc);

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", code);
		ret.put("desc", desc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 终端更换频率评分 2016.12.15
	 * 
	 * @param bizParams
	 * @return @
	 */
	public String getTerminalScoreUrl(JSONObject bizParams) {
		String isbilling = "1";
		String code = "B0001";
		String desc = "调用失败";
		String title = "终端更换频率评分";
		String phone = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : null;

		if (null == phone) {
			logger.info("=======================");
			JSONObject ret = new JSONObject();
			ret.put("interface", title);
			ret.put("code", "B0006");
			ret.put("desc", "参数");
			ret.put("isbilling", "0");

			return ret.toString();
		}

		String method = "unicomTerminalScoreService/getScore";

		// 用户所要请求的地址
		String url = baseUrl2 + method;

		// 用md5加密后的sign 签名
		String sign = MD5Builder.md5(account + phone + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("account", account);
		params.put("sign", sign);

		String reqUrl = HttpRequest.getUrl(params, url);

		logger.info(reqUrl);

		String result = HttpRequest.sendGet(reqUrl, null);

		logger.info(phone + " >> 返回结果：" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		// 根据xml串，获取document根节点
		Element root = DocHelper.getRootElement(result);
		Map<String, Object> docMap = DocHelper.toMap(root);

		// containsKey map中是否包含此映射
		if (docMap.containsKey("result")) {
			Map<String, Object> resultMap = (Map<String, Object>) docMap.get("result");
			if (resultMap.containsKey("info")) {
				Map<String, Object> infoMap = (Map<String, Object>) resultMap.get("info");
				String resCode = infoMap.get("result").toString();
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.get("statusDesc").toString();

				logger.info(phone + " >> code:" + resCode + " | status:" + status + " | statusDesc:" + statusDesc);

				if ("0".equals(status)) {
					// 查询成功
					if ("0".equals(resCode)) {
						code = "A";
						desc = "没有用户的终端记录";
					} else if ("90".equals(resCode)) {
						code = "B";
						desc = "B:(0,90] 单位：天";
					} else if ("180".equals(resCode)) {
						code = "C";
						desc = "C: (90,180] 单位：天";
					} else if ("360".equals(resCode)) {
						code = "D";
						desc = "D: (180,360] 单位：天";
					} else if ("999".equals(resCode)) {
						code = "E";
						desc = "E:(360, +) 单位：天";
					} else {
						code = "G";
						desc = "未知";
					}
					isbilling = "1";
				} else if ("-1".equals(status)) {
					// 调用失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else if ("1".equals(status)) {
					// 无数据
					code = "B0002";
					desc = "无数据";
					isbilling = "0";
				} else if ("sign".equals(status)) {
					// 校验失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else {
					code = "S0006";
					desc = "解析错误";
					isbilling = "0";
				}
			}
		}

		logger.info(phone + " >> code：" + code + " | desc:" + desc);

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", code);
		ret.put("desc", desc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 用户单月流量评分
	 * 
	 * @param bizParams
	 * @return @
	 */
	public String CSM170425_1455(JSONObject bizParams) {
		String code = "B0001";
		String desc = "调用失败";
		String title = "用户单月流量评分";
		String mobile = bizParams.getString("mobile");
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String method = "unicomSingleFluxScoreService/getSingleFluxScore";
		String url = baseUrl2 + method;

		String sign = MD5Builder.md5(account + mobile + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", mobile);
		params.put("selectMonth", month);
		params.put("account", account);
		params.put("sign", sign);

		String reqUrl = HttpRequest.getUrl(params, url);

		logger.info(reqUrl);

		String urlParams = HttpRequest.getUrlParams(params);
		String result = HttpRequest.sendPost(url, urlParams);
		logger.info(mobile + " >> 返回结果：" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		Element root = DocHelper.getRootElement(result);
		Map<String, Object> docMap = DocHelper.toMap(root);
		if (docMap.containsKey("result")) {
			Map<String, Object> resultMap = (Map<String, Object>) docMap.get("result");
			if (resultMap.containsKey("info")) {
				Map<String, Object> infoMap = (Map<String, Object>) resultMap.get("info");
				String score = infoMap.get("score").toString().toUpperCase();
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.containsKey("statusdesc") ? infoMap.get("statusdesc").toString() : "";

				logger.info(mobile + " >> score:" + score + " | status:" + status + " | statusDesc:" + statusDesc);

				if ("0".equals(status)) {
					// 查询成功
					if ("0".equals(score)) {
						code = "A";
						desc = "A:用户月流量为0";
					} else if ("50".equals(score)) {
						code = "B";
						desc = "B:用户月流量为(0,50]";
					} else if ("150".equals(score)) {
						code = "C";
						desc = "C:用户月流量为(50,150]";
					} else if ("600".equals(score)) {
						code = "D";
						desc = "D:用户月流量为(150,600]";
					} else if ("999".equals(score)) {
						code = "E";
						desc = "E:用户月流量为>999";
					} else {
						code = "B0003";
						desc = "未知";
					}
					isbilling = "1";
				} else if ("-1".equals(status)) {
					// 调用失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else if ("1".equals(status)) {
					// 无数据
					code = "B0002";
					desc = "无数据";
					isbilling = "0";
				} else if ("sign".equals(status)) {
					// 校验失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else {
					code = "S0006";
					desc = "解析错误";
					isbilling = "0";
				}
			}
		}
		logger.info(mobile + " >> code：" + code + " | desc:" + desc);

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", code);
		ret.put("desc", desc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码主叫/被叫通话时长评分
	 * 
	 * @param bizParams
	 * @return @
	 */
	public String CSM170425_1539(JSONObject bizParams) {
		String code = "B0001";
		String desc = "调用失败";
		String title = "手机号码主叫/被叫通话时长评分";
		String mobile = bizParams.getString("mobile");
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";

		if (null == mobile || "".equals(mobile) || null == month || "".equals(month)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String method = "unicomSingleVoiceScoreService/getSingleVoiceScore";
		String url = baseUrl2 + method;

		String sign = MD5Builder.md5(account + mobile + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", mobile);
		params.put("selectMonth", month);
		params.put("account", account);
		params.put("sign", sign);

		String reqUrl = HttpRequest.getUrl(params, url);

		logger.info(reqUrl);

		String urlParams = HttpRequest.getUrlParams(params);
		String result = HttpRequest.sendPost(url, urlParams);
		logger.info(mobile + " >> 返回结果：" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		Element root = DocHelper.getRootElement(result);
		Map<String, Object> docMap = DocHelper.toMap(root);
		if (docMap.containsKey("result")) {
			Map<String, Object> resultMap = (Map<String, Object>) docMap.get("result");
			if (resultMap.containsKey("info")) {
				Map<String, Object> infoMap = (Map<String, Object>) resultMap.get("info");
				String calledscore = infoMap.get("calledscore").toString().toUpperCase();
				String callerscore = infoMap.get("callerscore").toString().toUpperCase();
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.containsKey("statusdesc") ? infoMap.get("statusdesc").toString() : "";

				logger.info(mobile + " >> calledscore:" + calledscore + " | callerscore:" + callerscore + " | status:"
						+ status + " | statusDesc:" + statusDesc);

				if ("0".equals(status)) {
					// 查询成功
					String calledDesc = "";
					String callerDesc = "";

					JSONObject codeJson = new JSONObject();
					codeJson.put("calledscore", calledscore);
					codeJson.put("callerscore", callerscore);

					JSONObject descJson = new JSONObject();
					descJson.put("calledDesc", calledDesc);
					descJson.put("callerDesc", callerDesc);

					JSONObject ret = new JSONObject();
					ret.put("interface", title);
					ret.put("code", codeJson);
					ret.put("desc", descJson);
					ret.put("isbilling", isbilling);

					// 主叫
					if ("0".equals(callerscore)) {
						callerscore = "A";
						callerDesc = "A:用户月主叫通话量为0, 单位：分钟";
					} else if ("30".equals(callerscore)) {
						callerscore = "B";
						callerDesc = "B:用户月主叫通话量为(0,30], 单位：分钟";
					} else if ("90".equals(callerscore)) {
						callerscore = "C";
						callerDesc = "C:用户月主叫通话量为(30,90], 单位：分钟";
					} else if ("270".equals(callerscore)) {
						callerscore = "D";
						callerDesc = "D:用户月主叫通话量为(90,270], 单位：分钟";
					} else if ("999".equals(callerscore)) {
						callerscore = "E";
						callerDesc = "E:用户月主叫通话量为>270, 单位：分钟";
					} else {
						callerscore = "B0003";
						callerDesc = "未知";
					}

					// 被叫
					if ("0".equals(calledscore)) {
						calledscore = "A";
						calledDesc = "A:用户月被叫通话量为0, 单位：分钟";
					} else if ("40".equals(calledscore)) {
						calledscore = "B";
						calledDesc = "B:用户月被叫通话量为(0,40], 单位：分钟";
					} else if ("120".equals(calledscore)) {
						calledscore = "C";
						calledDesc = "C:用户月被叫通话量为(40,120], 单位：分钟";
					} else if ("360".equals(calledscore)) {
						calledscore = "D";
						calledDesc = "D:用户月被叫通话量为(120,360], 单位：分钟";
					} else if ("999".equals(calledscore)) {
						calledscore = "E";
						calledDesc = "E:用户月被叫通话量为>360, 单位：分钟";
					} else {
						calledscore = "B0003";
						calledDesc = "未知";
					}

					isbilling = "1";
					return ret.toString();
				} else if ("-1".equals(status)) {
					// 调用失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else if ("1".equals(status)) {
					// 无数据
					code = "B0002";
					desc = "无数据";
					isbilling = "0";
				} else if ("sign".equals(status)) {
					// 校验失败
					code = "B0001";
					desc = "调用失败";
					isbilling = "0";
				} else {
					code = "S0006";
					desc = "解析错误";
					isbilling = "0";
				}
			}
		}
		logger.info(mobile + " >> code：" + code + " | desc:" + desc);

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", code);
		ret.put("desc", desc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}
}
