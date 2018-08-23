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
public class BdcscServicePart1 {
	private static final Logger logger = Logger.getLogger(BdcscServicePart1.class);

	@Autowired
	private CreditService creditService;

	@Autowired
	private BdcscHelper bdcscHelper;

	@Autowired
	private BdcscServicePart6 bdcscServicePart6;
	@Autowired
	private BdcscServicePart5 bdcscServicePart5;
	@Autowired
	private BdcscServicePart4 bdcscServicePart4;
	@Autowired
	private BdcscServicePart3 bdcscServicePart3;
	@Autowired
	private BdcscServicePart2 bdcscServicePart2;
	@Autowired
	private BdcscServicePart1 bdcscServicePart1;

	/**
	 * 手机号码-证件类型-证件号码-姓名核验 适用于多个运营商的整合 0: 验证一致 1: 验证不一致 2: 无数据 -1: 调用失败|校验失败 -2:
	 * 不做验证
	 * 
	 * @param bizParams
	 * @return
	 * @
	 */
	public String verifyUserIdCardInfoV2(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String idType = bizParams.containsKey("idType") ? bizParams.get("idType").toString() : "";
		String idCard = bizParams.containsKey("idCard") ? bizParams.get("idCard").toString() : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		if (null == idType || "".equals(idType))
			idType = "idCard";
		String userName = bizParams.containsKey("userName") ? bizParams.get("userName").toString() : "";
		userName = URLDecoder.decode(userName);

		String title = "手机号码-证件类型-证件号码-姓名核验";
		if (null == mobile || "".equals(mobile) || null == userName || "".equals(userName)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}
		String productId = "rp-label";
		String module = "rp-label-status";
		String method = "_verifyUserIdCardInfo";

		String idNoHash = MD5Builder.md5(idCard);
		String nameHash = MD5Builder.md5(userName);
		if (!"idCard".equals(idType) && !"passport".equals(idType)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}

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

				String status = "-2";
				String desc = "";
				String isbilling = "1";
				if ("0".equals(idNoCheckResult) && "0".equals(idTypeCheckResult) && "0".equals(nameCheckResult)) {
					status = "0";
					desc = "验证一致";
					isbilling = "1";
				} else {
					if ("1".equals(idNoCheckResult) || "1".equals(idTypeCheckResult) || "1".equals(nameCheckResult)) {
						status = "1";
						desc = "验证不一致";
						isbilling = "1";
					} else if ("-1".equals(idNoCheckResult) || "-1".equals(nameCheckResult)) {
						status = "-2";
						desc = "不做验证";
						isbilling = "1";
					} else if ("-1".equals(idTypeCheckResult)) {
						status = "2";
						desc = "无数据";
						isbilling = "1";
					} else {
						status = "-1";
						desc = "调用失败";
						isbilling = "0";
					}
				}

				ret.put("interface", title);
				ret.put("code", status);
				ret.put("desc", desc);
				ret.put("isbilling", isbilling);

				return ret.toString();

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {
				// bdcscHelper.resetCachedToken();
				JSONObject ret = new JSONObject();
				ret.put("interface", title);
				ret.put("code", "S0008");
				ret.put("desc", "过于频繁，请稍候再试");
				ret.put("isbilling", "0");
				return ret.toString();
			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {
				JSONObject ret = new JSONObject();
				ret.put("interface", title);
				ret.put("code", "B0002");
				ret.put("desc", "无数据");
				ret.put("isbilling", "0");
				return ret.toString();
			} else if ("224".equals(code)) {
				JSONObject ret = new JSONObject();
				ret.put("interface", title);
				ret.put("code", "B0003");
				ret.put("desc", "未知");
				ret.put("isbilling", "1");
				return ret.toString();
			} else {
				JSONObject ret = new JSONObject();
				String retDesc = jsonObj.getString("message");

				logger.info(mobile + " >> " + retDesc);

				ret.put("interface", title);
				ret.put("code", "-1");
				ret.put("desc", "调用失败");
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

	/**
	 * “手机号码-证件类型-证件号码-姓名”核验
	 * 
	 * @param mobile
	 * @param idType
	 * @param idCard
	 * @param userName
	 * @return
	 * @
	 */
	public String verifyUserIdCardInfo(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.get("mobile").toString() : "";
		String idType = bizParams.containsKey("idType") ? bizParams.get("idType").toString() : "";
		String idCard = bizParams.containsKey("idCard") ? bizParams.get("idCard").toString() : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		if (null == idType || "".equals(idType))
			idType = "idCard";
		String userName = bizParams.containsKey("userName") ? bizParams.get("userName").toString() : "";
		userName = URLDecoder.decode(userName);

		String title = "手机号码-证件类型-证件号码-姓名核验";
		if (null == mobile || "".equals(mobile) || null == userName || "".equals(userName)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}
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

			ret.put("idNoCheck", p1.toString());
			ret.put("idTypeCheck", p1.toString());
			ret.put("nameCheck", p1.toString());
			ret.put("isbilling", "0");
			return ret.toString();
		}
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
				}

				String idTypeCheckResultDesc = null;
				if ("0".equals(idTypeCheckResult)) {
					idTypeCheckResultDesc = "身份证件类型验证结果: 验证一致";
				} else if ("1".equals(idTypeCheckResult)) {
					idTypeCheckResultDesc = "身份证件类型验证结果: 验证不一致";
				} else if ("2".equals(idTypeCheckResult)) {
					idTypeCheckResultDesc = "身份证件类型验证结果: 非中国电信手机号或号码不存在";
				}

				String nameCheckResultDesc = null;
				if ("-1".equals(nameCheckResult)) {
					nameCheckResultDesc = "姓名验证结果: 不做验证（因idType或idNo验证不一致）";
				} else if ("0".equals(nameCheckResult)) {
					nameCheckResultDesc = "姓名验证结果: 验证一致";
				} else if ("1".equals(nameCheckResult)) {
					nameCheckResultDesc = "姓名验证结果: 验证不一致";
				}

				ret.put("interface", title);
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
				ret.put("isbilling", "1");

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

				p1.put("code", "B0002");
				p1.put("desc", "无数据");

				ret.put("idNoCheck", "");
				ret.put("idTypeCheck", "");
				ret.put("nameCheck", "");
				ret.put("isbilling", "0");
				return ret.toString();
			} else if ("224".equals(code)) {
				JSONObject ret = new JSONObject();

				p1.put("code", "B0003");
				p1.put("desc", "未知");

				ret.put("idNoCheck", "");
				ret.put("idTypeCheck", "");
				ret.put("nameCheck", "");
				ret.put("isbilling", "1");
				return ret.toString();
			} else {
				JSONObject ret = new JSONObject();

				p1.put("code", "B0001");
				p1.put("desc", "调用失败");

				ret.put("idNoCheck", "");
				ret.put("idTypeCheck", "");
				ret.put("nameCheck", "");
				ret.put("isbilling", "0");
				return ret.toString();
			}
		}

		JSONObject ret = new JSONObject();
		JSONObject p11 = new JSONObject();
		ret.put("interface", title);
		p11.put("code", "B0001");
		p11.put("desc", "调用失败");
		ret.put("isbilling", "0");

		ret.put("idNoCheck", p11.toString());
		ret.put("idTypeCheck", p11.toString());
		ret.put("nameCheck", p11.toString());
		return ret.toString();
	}

	/**
	 * “手机号码”实名制核验
	 * 
	 * @param bizParams
	 * @return
	 * @
	 */
	public String getRealnameFlag(JSONObject bizParams) {
		String mobile = bizParams.getString("mobile");
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码实名制核验";
		String isbilling = "1";
		if (null == mobile || "".equals(mobile)) {
			JSONObject ret = new JSONObject();

			ret.put("interface", title);
			ret.put("code", "S0001");
			ret.put("desc", "参数错误");
			ret.put("isbilling", "0");
			return ret.toString();
		}
		String productId = "rp-label";
		String module = "rp-label-info";
		String method = "realnameFlag";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info(mobile + " >> " + result);

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
				if ("0".equals(value)) {
					retCode = "0";
					retDesc = "实名制用户";
					isbilling = "1";
				} else if ("1".equals(value)) {
					retCode = "1";
					retDesc = "非实名制用户";
					isbilling = "1";
				} else if ("mdn_not_exist".equalsIgnoreCase(value) || "data_not_exist".equalsIgnoreCase(value)) {
					retCode = "B0002";
					retDesc = "无数据";
					isbilling = "1";
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

	public String userBIP3(JSONObject bizParams) {

		// “手机号码”自然人年龄查询/分级
		String ual = bdcscServicePart1.getUserAgeLabel(bizParams);
		// JSONObject ualObj = JSONObject.fromObject(ual);
		JSONObject ualObj = JSONObject.parseObject(ual);
		String ageLabelCode = ualObj.getString("code");
		String ageLabelDesc = ualObj.getString("desc");

		// 手机号码-性别
		String ug = bdcscServicePart3.getUserGender(bizParams);
		// JSONObject ugObj = JSONObject.fromObject(ug);
		JSONObject ugObj = JSONObject.parseObject(ug);
		String sexCode = ugObj.getString("code");
		String sexDesc = ugObj.getString("desc");

		// 手机号码-姓名校验
		String vun = bdcscServicePart3.getVerifyUserName(bizParams);
		// JSONObject vunObj = JSONObject.fromObject(vun);
		JSONObject vunObj = JSONObject.parseObject(vun);
		String nameValCode = vunObj.getString("code");
		String nameValDesc = vunObj.getString("desc");

		JSONObject ret = new JSONObject();
		ret.put("interface", "手机号码-年龄分级-性别-姓名校验");
		ret.put("ageLabelCode", ageLabelCode);
		ret.put("ageLabelDesc", ageLabelDesc);
		ret.put("sexCode", sexCode);
		ret.put("sexDesc", sexDesc);
		ret.put("nameValCode", nameValCode);
		ret.put("nameValDesc", nameValDesc);
		ret.put("isbilling", "1");

		return ret.toString();
	}

	/**
	 * 手机号归属省
	 * 
	 * @param mobile
	 * @return
	 * @
	 */
	public String getProvince(JSONObject bizParams) {
		String mobile = bizParams.getString("mobile");
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号归属省";
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
		String method = "province";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		logger.info(mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info(mobile + " >> " + result);

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
				if ("mdn_not_exist".equalsIgnoreCase(value) || "data_not_exist".equalsIgnoreCase(value)) {
					retCode = "B0002";
					retDesc = "无数据";
					isbilling = "1";

				} else {
					retCode = value;
					// 根据省份代码，获取省份名称
					retDesc = creditService.getProvinceNameByCode(value);
					if (null == retDesc || "".equals(retDesc))
						retDesc = value;
					isbilling = "1";
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
	 * 手机号码自然人年龄查询/分级
	 * 
	 * @param mobile
	 * @return
	 * @
	 */
	public String getUserAgeLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码自然人年龄查询/分级";
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
		String method = "userAgeLabel";

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
				if ("A".equals(value)) {
					retCode = "A";
					retDesc = "A:[0-18)";
				} else if ("B".equals(value)) {
					retCode = "B";
					retDesc = "B:[18-25)";
				} else if ("C".equals(value)) {
					retCode = "C";
					retDesc = "C:[25-32)";
				} else if ("D".equals(value)) {
					retCode = "D";
					retDesc = "D:[32-40)";
				} else if ("E".equals(value)) {
					retCode = "E";
					retDesc = "E:[40-50)";
				} else if ("F".equals(value)) {
					retCode = "F";
					retDesc = "F:[50,+ )";
				} else if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
					isbilling = "1";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
					isbilling = "1";
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
	 * 手机号归属市
	 * 
	 * @param mobile
	 * @return
	 * @
	 */
	public String getCity(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号归属市";
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
		String method = "city";

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
					isbilling = "1";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
					isbilling = "1";
				} else {
					retCode = value;
					// 根据城市代码，获取城市名称
					retDesc = creditService.getCityNameByCode(value);
					if (null == retDesc || "".equals(retDesc))
						retDesc = value;
					isbilling = "1";
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
	 * “手机号码”月上网流量/分级
	 * 
	 * @param mobile
	 * @return
	 * @
	 */
	public String getFlowLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : ""; // 格式yyyyMM
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码月上网流量/分级";
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
		String method = "flowLabel";

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
					isbilling = "1";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
					isbilling = "1";
				} else if ("a".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[0-30) 单位：M";
				} else if ("b".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[30-50) 单位：M";
				} else if ("c".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[50-200) 单位：M";
				} else if ("d".equalsIgnoreCase(value)) {
					retCode = value;
					retDesc = "[200,+) 单位：M";
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
	 * “手机号码”余额总额/分级
	 * 
	 * @param mobile
	 * @return
	 * @
	 */
	public String getBalanceLabel(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码余额/分级";

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
		String method = "balanceLabel";

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
					retCode = "B0002";
					retDesc = "无数据";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "B0002";
					retDesc = "无数据";
				} else if ("a".equalsIgnoreCase(value)) {
					retDesc = "0";
				} else if ("b".equalsIgnoreCase(value)) {
					retDesc = "(0,50]";
				} else if ("c".equalsIgnoreCase(value)) {
					retDesc = "(50,100]";
				} else if ("d".equalsIgnoreCase(value)) {
					retDesc = "(100,+]";
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
	 * “手机号码”最近三个月凌晨时段通话时长占比/分级
	 * 
	 * @param mobile
	 * @return
	 * @
	 */
	public String getTalkTimeLengthDawnPtgIn3MonthsScore(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码最近三个月凌晨时段通话时长占比/分级";

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
		String module = "rp-label-communication";
		String method = "talkTimeLengthDawnPtgIn3MonthsScore";

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
				retDesc = "占比得分";
				if ("mdn_not_exist".equalsIgnoreCase(value)) {
					retCode = "-1";
					retDesc = mobile + "用户不存在";
					isbilling = "1";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
					isbilling = "1";
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
	 * “手机号码”月交往圈大小/分级
	 * 
	 * @param mobile
	 * @return
	 * @
	 */
	public String getSocialCircleSize(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码月交往圈大小/分级";

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
		String method = "socialCircleSize";

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
					isbilling = "1";
				} else if ("data_not_exist".equalsIgnoreCase(value)) {
					retCode = "-2";
					retDesc = "数据不存在";
					isbilling = "1";
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
	 * “手机号码”主叫月通话集中度/分级
	 * 
	 * @param mobile
	 * @return
	 * @
	 */
	public String getCallFocusScore(JSONObject bizParams) {
		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";

		String retCode = "B0001";
		String retDesc = "调用失败";
		String title = "手机号码主叫月通话集中度/分级";

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
		String method = "callFocusScore";

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

}
