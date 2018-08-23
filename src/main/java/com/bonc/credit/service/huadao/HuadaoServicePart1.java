package com.bonc.credit.service.huadao;

import java.net.URLDecoder;
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
public class HuadaoServicePart1 extends HuadaoHelper {
	private static final Logger logger = Logger.getLogger(HuadaoServicePart1.class);

	/**
	 * “手机号码-证件类型-证件号码-姓名”核验 0: 验证一致 1: 验证不一致 2: 无数据 -1: 调用失败|校验失败 -2: 不做验证
	 * 
	 * @param mobile
	 * @param idCard
	 * @param userName
	 * @return
	 *  
	 */
	@SuppressWarnings("unchecked")
	public String verifyUserIdCardInfo(String mobile, String idCard, String userName) {
		String code = "-2";
		if (null == mobile || "".equals(mobile)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", "");
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String method = "check";
		String url = baseUrl + method;
		userName = URLDecoder.decode(userName);

		String sign = MD5Builder.md5(userName + idCard + mobile + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("name", userName);
		params.put("idcode", idCard);
		params.put("type", "02");
		params.put("phone", mobile);
		params.put("account", account);
		params.put("sign", sign);

		String reqUrl = HttpRequest.getUrl(params, url);

		logger.info(reqUrl);

		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info(mobile + " >> 返回结果：" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", "“手机号码-证件类型-证件号码-姓名”核验");
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
				String res = infoMap.get("result").toString();
				String resultDesc = infoMap.get("resultDesc").toString();
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.get("statusDesc").toString();

				logger.info(mobile + " >> res:" + res + " | resultDesc:" + resultDesc + " | statusDesc:" + statusDesc);

				if ("0".equals(status)) {
					// 查询成功
					if ("1".equals(res)) {
						// 验证一致
						code = "0";
					} else if ("0".equals(res)) {
						// 验证不一致
						code = "1";
					} else {
						logger.info(mobile + " >> 与文档描述不一致!");
					}
				} else if ("-1".equals(status)) {
					// 调用失败
					code = "-1";
				} else if ("1".equals(status)) {
					// 无数据
					code = "2";
				} else if ("sign".equals(status)) {
					// 校验失败
					code = "-1";
				}
			}
		}
		return code;
	}

	/**
	 * “手机号码-证件类型-证件号码-姓名”核验
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String verifyUserIdCardInfoV2(JSONObject bizParams) {
		String mobile = bizParams.getString("mobile");
		String idCard = bizParams.getString("idCard");
		String userName = bizParams.getString("userName");

		String title = "手机号码-证件类型-证件号码-姓名核验";

		JSONObject ret = new JSONObject();
		ret.put("interface", title);

		if (null == mobile || "".equals(mobile) || null == userName || "".equals(userName)) {

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String status = verifyUserIdCardInfo(mobile, idCard, userName);
		if ("0".equals(status)) {
			ret.put("code", "0");
			ret.put("desc", "验证一致");
			ret.put("isbilling", "1");
		} else if ("1".equals(status)) {
			ret.put("code", "1");
			ret.put("desc", "验证不一致");
			ret.put("isbilling", "1");
		} else if ("2".equals(status)) {
			ret.put("code", "2");
			ret.put("desc", "无数据");
			ret.put("isbilling", "0");
		} else if ("-1".equals(status)) {
			ret.put("code", "-1");
			ret.put("desc", "调用失败");
			ret.put("isbilling", "0");
		} else if ("-2".equals(status)) {
			ret.put("code", "-2");
			ret.put("desc", "不做验证");
			ret.put("isbilling", "0");
		}

		return ret.toString();
	}

	/**
	 * “手机号码”在网时长/分级
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getTimeLengthLabel(JSONObject bizParams) {
		String code = "B0001";
		String desc = "调用失败";
		String title = "手机号码-在网时长/分级";
		String mobile = bizParams.getString("mobile");

		if (null == mobile || "".equals(mobile)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", "");
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String method = "unioncomTimeInService/vertifyTimeIn";
		String url = baseUrl2 + method;

		String sign = MD5Builder.md5(account + mobile + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", mobile);
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
				String inUseTime = infoMap.get("inUseTime").toString();
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.get("statusDesc").toString();

				logger.info(
						mobile + " >> inUseTime:" + inUseTime + " | status:" + status + " | statusDesc:" + statusDesc);

				if ("0".equals(status)) {
					// 查询成功
					if ("1".equals(inUseTime)) {
						code = "A";
						desc = "1个月及以下（用户状态正常时，含义为当月入网）";
					} else if ("2".equals(inUseTime)) {
						code = "B";
						desc = "2个月（用户状态正常时，含义为上个月入网）";
					} else if ("3".equals(inUseTime)) {
						code = "C";
						desc = "3-6个月";
					} else if ("4".equals(inUseTime)) {
						code = "D";
						desc = "7-12个月";
					} else if ("5".equals(inUseTime)) {
						code = "E";
						desc = "13-24个月";
					} else if ("6".equals(inUseTime)) {
						code = "F";
						desc = "25-36个月";
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
		logger.info(mobile + " >> code：" + code + " | desc:" + desc);

		JSONObject ret = new JSONObject();
		ret.put("interface", title);
		ret.put("code", code);
		ret.put("desc", desc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码自然人年龄查询/分级
	 * 
	 * @param mobile
	 * @return
	 *  
	 */
	public String getUserAgeLabel(JSONObject bizParams) {
		String code = "B0001";
		String desc = "调用失败";
		String title = "手机号码自然人年龄查询/分级";
		String mobile = bizParams.getString("mobile");

		if (null == mobile || "".equals(mobile)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", "");
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String method = "unioncomQueryAgeService/vertifyQueryAge";
		String url = baseUrl2 + method;

		String sign = MD5Builder.md5(account + mobile + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", mobile);
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
				String age = infoMap.containsKey("age") ? infoMap.get("age").toString().toUpperCase() : null;
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.get("statusDesc").toString();

				logger.info(mobile + " >> inUseTime:" + age + " | status:" + status + " | statusDesc:" + statusDesc);

				if ("0".equals(status)) {
					// 查询成功
					if (null == age || "".equals(age)) {
						code = "-1";
						desc = "数据不存在";
					} else if ("A".equals(age)) {
						code = "A";
						desc = "A:[0-18)";
					} else if ("B".equals(age)) {
						code = "B";
						desc = "B:[18-25)";
					} else if ("C".equals(age)) {
						code = "C";
						desc = "C:[25-32)";
					} else if ("D".equals(age)) {
						code = "D";
						desc = "D:[32-40)";
					} else if ("E".equals(age)) {
						code = "E";
						desc = "E:[40-50)";
					} else if ("F".equals(age)) {
						code = "F";
						desc = "F:[50,+)";
					} else {
						code = "B0003";
						desc = "未知";
					}
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
	 * 手机号码-姓名校验
	 * 
	 * @param mobile
	 * @param userName
	 * @return
	 *  
	 */
	public String getVerifyUserName(JSONObject bizParams) {
		String code = "B0001";
		String desc = "调用失败";
		String title = "手机号码-姓名校验";
		String mobile = bizParams.getString("mobile");
		String userName = bizParams.getString("userName");

		if (null == mobile || "".equals(mobile) || null == userName || "".equals(userName)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", "");
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

		String isbilling = "1";
		String method = "unicomNameIdentityService/nameIdentity";
		String url = baseUrl2 + method;
		userName = URLDecoder.decode(userName);

		String sign = MD5Builder.md5(account + mobile + userName + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", mobile);
		params.put("account", account);
		params.put("userName", userName);
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
				String nameCheck = infoMap.get("nameCheck").toString().toUpperCase();
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.get("statusDesc").toString();

				logger.info(
						mobile + " >> nameCheck:" + nameCheck + " | status:" + status + " | statusDesc:" + statusDesc);

				if ("0".equals(status)) {
					if ("0".equals(nameCheck)) {
						code = "0";
						desc = "一致";
					} else if ("1".equals(nameCheck)) {
						code = "1";
						desc = "不一致";
					} else {
						code = "B0002";
						desc = "无数据";
						isbilling = "0";
					}
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
	 * “手机号码”当前状态查询
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getPhoneNumState(JSONObject bizParams) {
		String code = "B0001";
		String desc = "调用失败";
		String title = "手机号码当前状态查询";
		String mobile = bizParams.getString("mobile");

		if (null == mobile || "".equals(mobile)) {
			return HuadaoHelper.paramsError();
		}

		String isbilling = "1";
		String method = "unioncomTelNumStatusService/vertifyTelNumStatus";
		String url = baseUrl2 + method;

		String sign = MD5Builder.md5(account + mobile + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", mobile);
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
				String telStatus = infoMap.get("telStatus").toString();
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.get("statusDesc").toString();

				logger.info(
						mobile + " >> telStatus:" + telStatus + " | status:" + status + " | statusDesc:" + statusDesc);

				if ("0".equals(status)) {
					// 查询成功
					isbilling = "1";
					if ("0".equals(telStatus)) {
						// 非联通
						code = "9";
						desc = "无法查询";
					} else if ("1".equals(telStatus)) {
						// 未启用
						code = "3";
						desc = "在网但不可用";
					} else if ("2".equals(telStatus)) {
						// 正常
						code = "1";
						desc = "正常在用";
					} else if ("3".equals(telStatus)) {
						// 欠费停机
						code = "2";
						desc = "停机";
					} else if ("4".equals(telStatus)) {
						// 其它停机
						code = "2";
						desc = "停机";
					} else if ("5".equals(telStatus)) {
						// 已销号
						code = "4";
						desc = "不在网";
					} else {
						code = "9";
						desc = "无法查询";
					}
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
	 * 手机号码-证件类型-证件号码核验(电信联通及移动的合并版本支持)
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String verifyUserIdCardNoFor3Net(JSONObject bizParams) {
		String code = "B0001";
		String desc = "调用失败";
		String title = "手机号码-证件类型-证件号码核验";
		String mobile = bizParams.getString("mobile");
		String idType = bizParams.containsKey("idType") ? bizParams.get("idType").toString() : "";
		String idCard = bizParams.containsKey("idCard") ? bizParams.get("idCard").toString() : "";

		if (null == idType || "".equals(idType))
			idType = "0101"; // 默认身份证
		else if ("idCard".equals(idType))
			idType = "0101"; // 身份证
		else if ("passport".equals(idType)) {
			idType = "0107"; // 护照
		} else {
			idType = "0101"; // 身份证
		}

		if (null == mobile || "".equals(mobile)) {
			return HuadaoHelper.paramsError();
		}

		String isbilling = "1";
		String method = "unioncomCertIdentityService/vertifyCertIdentity";
		String url = baseUrl2 + method;

		String sign = MD5Builder.md5(account + mobile + idType + idCard + privateKey);

		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", mobile);
		params.put("certType", idType);
		params.put("certCode", idCard);
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
				String certResult = infoMap.get("certResult").toString();
				String status = infoMap.get("status").toString();
				String statusDesc = infoMap.get("statusDesc").toString();

				logger.info(mobile + " >> certResult:" + certResult + " | status:" + status + " | statusDesc:"
						+ statusDesc);

				if ("0".equals(status)) {
					// 查询成功
					isbilling = "1";
					if ("0".equals(certResult)) {
						// 一致
						code = "0";
						desc = "一致";
					} else if ("1".equals(certResult)) {
						// 不一致
						code = "1";
						desc = "不一致";
					} else {
						code = "B0003";
						desc = "未知";
					}
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
