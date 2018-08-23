package com.bonc.credit.service.bdcsc;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.HttpRequest;

/**
 * 
 * @author zhijie.ma
 * @date 2017年5月3日
 *
 */
@Service
public class BdcscServicePart6 {
	private static final Logger logger = Logger.getLogger(BdcscServicePart6.class);

	@Autowired
	private BdcscHelper bdcscHelper;

	/**
	 * 手机号码-资金需求指数得分
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getFundDemandScore(JSONObject bizParams) {
		String title = "手机号码-资金需求指数得分";
		String retCode = "B0001";
		String retDesc = "";
		String isbilling = "0";
		JSONObject ret = new JSONObject();

		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		if (mobile == null || "".equals(mobile) || month == null || "".equals(month)) {

			retCode = "S0001";
			retDesc = "参数错误";

			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);

			return ret.toString();
		}

		String productId = "rp-label";
		String module = "rp-label-communication";
		String method = "fundDemandScore";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);

		reqUrl = reqUrl + "&month=" + month;
		logger.info(mobile + ">>" + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***请求后的结果：***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		// JSONObject jsonObject = JSONObject.fromObject(result);
		JSONObject jsonObject = JSONObject.parseObject(result);

		if (jsonObject.containsKey("code")) {
			String code = jsonObject.getString("code");
			if ("200".equals(code)) {
				// 响应成功
				if (jsonObject.containsKey("data")) {

					JSONObject dataObj = jsonObject.getJSONObject("data");
					retCode = dataObj.getString("value");
					retDesc = "资金需求指数得分:" + retCode;
					isbilling = "1";

				} else {
					logger.info(mobile + " >> 解析失败");

					retCode = "S0006";
					retDesc = "解析失败";

				}

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {

				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";

			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {

				retCode = "B0002";
				retDesc = "无数据";

			} else if ("224".equals(code)) {

				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";

			} else {
				// 响应失败
				retDesc = "调用失败,响应失败";

			}

		} else {

			retDesc = "调用失败，JSONObject中没有code";

		}

		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码-预/后付费类型
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getPaymentPreType(JSONObject bizParams) {

		String title = "手机号码-预/后付费类型";
		String retCode = "B0001";
		String retDesc = "";
		String isbilling = "0";
		JSONObject ret = new JSONObject();

		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		// 判断用户是否输入了手机号
		if (mobile == null || "".equals(mobile)) {

			retCode = "S0001";
			retDesc = "参数错误";

			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);

			return ret.toString();
		}

		// 获取url
		String productId = "rp-label";
		String module = "rp-label-info";
		String method = "paymentPreType";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);

		logger.info("***请求地址***" + mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***get方式结果***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		if ("error".equals(result)) {

			retDesc = "调用失败，result结果error";

			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);

			return ret.toString();
		}

		// JSONObject fromObject = JSONObject.fromObject(result);
		JSONObject fromObject = JSONObject.parseObject(result);

		if (fromObject.containsKey("code")) {
			String code = fromObject.getString("code");
			if ("200".equals(code)) {
				// 响应成功
				String value = fromObject.getJSONObject("data").getString("value");
				if (value.equals("1200")) {

					retDesc = "预/后付费类型为：后付费";

				} else if (value.equals("1201")) {

					retDesc = "预/后付费类型为：准实时预付费";

				} else if (value.equals("2100")) {

					retDesc = "预/后付费类型为：预付费";

				} else if (value.equals("9999")) {

					retDesc = "预/后付费类型为：其他";

				} else {

					retDesc = "预/后付费类型为：未知";

				}

				retCode = value;
				isbilling = "1";

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {

				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";

			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {

				retCode = "B0002";
				retDesc = "无数据";

			} else if ("224".equals(code)) {

				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";

			} else {
				// 响应失败
				retDesc = "调用失败，响应失败code不是200";

			}

		} else {

			retDesc = "调用失败，JSONObject中没有code";

		}

		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 个人风险分值评估体系——总分
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getRiskScoreEvaluation(JSONObject bizParams) {
		String title = "个人风险分值评估体系——总分";
		String retCode = "B0001";
		String retDesc = "";
		String isbilling = "0";
		JSONObject ret = new JSONObject();

		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		if (mobile == null || "".equals(mobile)) {

			retCode = "S0001";
			retDesc = "参数错误";

			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);

			return ret.toString();
		}

		// 定制URL的路径头
		String productId = "rp-label";
		String module = "grade";
		String method = "totalScore";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);

		logger.info("***请求地址***" + mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***get方式结果***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		// JSONObject fromObject = JSONObject.fromObject(result);
		JSONObject fromObject = JSONObject.parseObject(result);

		if (fromObject.containsKey("code")) {
			// 主要逻辑代码
			String code = fromObject.getString("code");
			if ("200".equals(code)) {
				// 响应成功
				if (fromObject.containsKey("data")) {
					String totalScore = fromObject.getJSONObject("data").getString("totalScore");
					if ((totalScore.hashCode() < "300".hashCode()) || (totalScore.hashCode() > "1000".hashCode())) {
						// 结果分数不在要求范围内 请重新计算
						retCode = "B0003";
						retDesc = "未知：分数结果不符合要求，请重新计算，分数应在[300,1000]内";

					} else {
						// 分数评定成功
						retCode = "totalScore";
						retDesc = "个人风险分值评估体系——总分为：" + totalScore;
						isbilling = "1";

					}

				} else {
					logger.info(mobile + " >> 解析失败");

					retCode = "S0006";
					retDesc = "解析失败";

				}

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {

				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";

			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {

				retCode = "B0002";
				retDesc = "无数据";

			} else if ("224".equals(code)) {

				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";

			} else {
				// 响应失败
				retDesc = "调用失败,响应失败code不是200";

			}

		} else {

			retDesc = "调用失败，JSONObject中没有code";

		}

		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 个人风险分值评估体系——身份特征
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getStatusScore(JSONObject bizParams) {
		String title = "个人风险分值评估体系——身份特征";

		String retCode = "B0001";
		String retDesc = "";
		String isbilling = "0";
		JSONObject ret = new JSONObject();

		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		if (mobile == null || "".equals(mobile)) {

			retCode = "S0001";
			retDesc = "参数错误";

			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);

			return ret.toString();
		}

		// 定制URL的路径头
		String productId = "rp-label";
		String module = "grade";
		String method = "statusScore";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);

		logger.info("***请求地址***" + mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***get方式结果***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		// JSONObject fromObject = JSONObject.fromObject(result);
		JSONObject fromObject = JSONObject.parseObject(result);

		if (fromObject.containsKey("code")) {
			// 主要逻辑代码
			String code = fromObject.getString("code");
			if ("200".equals(code)) {
				// 响应成功
				if (fromObject.containsKey("data")) {
					JSONObject dataObj = fromObject.getJSONObject("data");
					if (dataObj.containsKey("shenFen")) {
						// 身份特征得分
						String status = dataObj.getJSONObject("shenFen").getString("status");
						// 身份特征分档
						String score = dataObj.getJSONObject("shenFen").getString("score");
						String statusCode = null;
						String scoreCode = null;
						String statusDesc = null;
						String scoreDesc = null;
						if (score.hashCode() >= "60".hashCode() && score.hashCode() <= "200".hashCode()) {

							scoreCode = score;
							scoreDesc = "身份特征得分（整数）为：" + score;

						} else {
							// 结果分数不在要求范围内 请重新计算
							scoreCode = score;
							scoreDesc = "分数超出范围,输出分数范围应在：[60,200]内，整数";
						}
						// ==============身份特征分档====================
						if ("C".equals(status) || "CC".equals(status) || "CCC".equals(status) || "B".equals(status)
								|| "BB".equals(status) || "BBB".equals(status) || "A".equals(status)
								|| "AA".equals(status) || "AAA".equals(status)) {

							statusCode = status;
							statusDesc = "身份特征分档的等级为：" + status;

						} else {
							// 结果分数不在要求范围内 请重新计算
							statusCode = status;
							statusDesc = statusCode + "不在以下档位中，请重新计算。档位由低到高依次是：C、CC、CCC、B、BB、BBB、A、AA、AAA档";
						}

						isbilling = "1";

						JSONObject scoreObj = new JSONObject();
						scoreObj.put("scoreCode", scoreCode);
						scoreObj.put("scoreDesc", scoreDesc);

						JSONObject statusObj = new JSONObject();
						statusObj.put("statusCode", statusCode);
						statusObj.put("statusDesc", statusDesc);

						ret.put("interface", title);
						ret.put("score", scoreObj);
						ret.put("status", statusObj);
						ret.put("isbilling", isbilling);

						return ret.toString();
					} else {
						logger.info(mobile + " >> 解析失败,JSONObject中没有shenFen");

						retCode = "S0006";
						retDesc = "解析失败,JSONObject中没有shenFen";

					}

				} else {
					logger.info(mobile + " >> 解析失败,JSONObject中没有data");

					retCode = "S0006";
					retDesc = "解析失败,JSONObject中没有data";

				}

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {

				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";

			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {

				retCode = "B0002";
				retDesc = "无数据";

			} else if ("224".equals(code)) {

				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";

			} else {
				// 响应失败
				retDesc = "调用失败,响应失败code不是200";

			}

		} else {

			retDesc = "调用失败，JSONObject中没有code";

		}

		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 个人风险分值评估体系—行为偏好
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getBehaviorScore(JSONObject bizParams) {
		String title = "个人风险分值评估体系——行为偏好";
		String retCode = "B0001";
		String retDesc = "";
		String isbilling = "0";
		JSONObject ret = new JSONObject();

		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		if (mobile == null || "".equals(mobile)) {

			retCode = "S0001";
			retDesc = "参数错误";

			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);

			return ret.toString();
		}

		// 定制URL的路径头
		String productId = "rp-label";
		String module = "grade";
		String method = "behaviorScore";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);

		logger.info("***请求地址***" + mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***get方式结果***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		// JSONObject fromObject = JSONObject.fromObject(result);
		JSONObject fromObject = JSONObject.parseObject(result);

		if (fromObject.containsKey("code")) {
			// 主要逻辑代码
			String code = fromObject.getString("code");
			if ("200".equals(code)) {
				// 响应成功
				if (fromObject.containsKey("data")) {
					JSONObject dataObj = fromObject.getJSONObject("data");
					if (dataObj.containsKey("xingWei")) {
						// 行为偏好分档
						String status = dataObj.getJSONObject("xingWei").getString("status");
						// 行为偏好得分
						String score = dataObj.getJSONObject("xingWei").getString("score");
						String statusCode = null;
						String scoreCode = null;
						String statusDesc = null;
						String scoreDesc = null;
						if (score.hashCode() >= "60".hashCode() && score.hashCode() <= "200".hashCode()) {

							scoreCode = score;
							scoreDesc = "行为偏好得分（整数）为：" + score;

						} else {
							// 结果分数不在要求范围内 请重新计算
							scoreCode = score;
							scoreDesc = "分数超出范围,输出分数范围应在：[60,200]内，整数";
						}
						// ==============行为偏好分档====================
						if ("C".equals(status) || "CC".equals(status) || "CCC".equals(status) || "B".equals(status)
								|| "BB".equals(status) || "BBB".equals(status) || "A".equals(status)
								|| "AA".equals(status) || "AAA".equals(status)) {

							statusCode = status;
							statusDesc = "行为偏好分档的等级为：" + status;

						} else {
							// 结果分数不在要求范围内 请重新计算
							statusCode = status;
							statusDesc = statusCode + "不在以下档位中，请重新计算。档位由低到高依次是：C、CC、CCC、B、BB、BBB、A、AA、AAA档";
						}

						isbilling = "1";

						JSONObject scoreObj = new JSONObject();
						scoreObj.put("scoreCode", scoreCode);
						scoreObj.put("scoreDesc", scoreDesc);

						JSONObject statusObj = new JSONObject();
						statusObj.put("statusCode", statusCode);
						statusObj.put("statusDesc", statusDesc);

						ret.put("interface", title);
						ret.put("score", scoreObj);
						ret.put("status", statusObj);
						ret.put("isbilling", isbilling);

						return ret.toString();
					} else {
						logger.info(mobile + " >> 解析失败,JSONObject中没有xingWei");

						retCode = "S0006";
						retDesc = "解析失败,JSONObject中没有xingWei";

					}

				} else {
					logger.info(mobile + " >> 解析失败,JSONObject中没有data");

					retCode = "S0006";
					retDesc = "解析失败,JSONObject中没有data";

				}

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {

				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";

			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {

				retCode = "B0002";
				retDesc = "无数据";

			} else if ("224".equals(code)) {

				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";

			} else {
				// 响应失败

				retDesc = "调用失败,响应失败";
			}

		} else {

			retDesc = "调用失败，JSONObject中没有code";

		}

		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();

	}

	/**
	 * 个人风险分值评估体系—消费能力：
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getConsumeScore(JSONObject bizParams) {
		String title = "个人风险分值评估体系——消费能力";
		String retCode = "B0001";
		String retDesc = "";
		String isbilling = "0";
		JSONObject ret = new JSONObject();

		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		if (mobile == null || "".equals(mobile)) {
			retCode = "S0001";
			retDesc = "参数错误";

			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);

			return ret.toString();
		}

		// 定制URL的路径头
		String productId = "rp-label";
		String module = "grade";
		String method = "consumeScore";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);

		logger.info("***请求地址***" + mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***get方式结果***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		// JSONObject fromObject = JSONObject.fromObject(result);
		JSONObject fromObject = JSONObject.parseObject(result);

		if (fromObject.containsKey("code")) {
			// 主要逻辑代码
			String code = fromObject.getString("code");
			if ("200".equals(code)) {
				// 响应成功
				if (fromObject.containsKey("data")) {
					JSONObject dataObj = fromObject.getJSONObject("data");
					if (dataObj.containsKey("nengLi")) {
						// 消费能力分档
						String status = dataObj.getJSONObject("nengLi").getString("status");
						// 消费能力得分
						String score = dataObj.getJSONObject("nengLi").getString("score");
						String statusCode = null;
						String scoreCode = null;
						String statusDesc = null;
						String scoreDesc = null;
						if (score.hashCode() >= "60".hashCode() && score.hashCode() <= "200".hashCode()) {

							scoreCode = score;
							scoreDesc = "消费能力得分（整数）为：" + score;

						} else {
							// 结果分数不在要求范围内 请重新计算
							scoreCode = score;
							scoreDesc = "分数超出范围,输出分数范围应在：[60,200]内，整数";
						}
						// ==============消费能力分档====================
						if ("C".equals(status) || "CC".equals(status) || "CCC".equals(status) || "B".equals(status)
								|| "BB".equals(status) || "BBB".equals(status) || "A".equals(status)
								|| "AA".equals(status) || "AAA".equals(status)) {

							statusCode = status;
							statusDesc = "消费能力分档的等级为：" + status;

						} else {
							// 结果分数不在要求范围内 请重新计算
							statusCode = status;
							statusDesc = statusCode + "不在以下档位中，请重新计算。档位由低到高依次是：C、CC、CCC、B、BB、BBB、A、AA、AAA档";
						}

						isbilling = "1";

						JSONObject scoreObj = new JSONObject();
						scoreObj.put("scoreCode", scoreCode);
						scoreObj.put("scoreDesc", scoreDesc);

						JSONObject statusObj = new JSONObject();
						statusObj.put("statusCode", statusCode);
						statusObj.put("statusDesc", statusDesc);

						ret.put("interface", title);
						ret.put("score", scoreObj);
						ret.put("status", statusObj);
						ret.put("isbilling", isbilling);

						return ret.toString();
					} else {
						logger.info(mobile + " >> 解析失败,JSONObject中没有nengLi");

						retCode = "S0006";
						retDesc = "解析失败,JSONObject中没有nengLi";

					}

				} else {
					logger.info(mobile + " >> 解析失败,JSONObject中没有data");

					retCode = "S0006";
					retDesc = "解析失败,JSONObject中没有data";

				}

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {

				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";

			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {

				retCode = "B0002";
				retDesc = "无数据";

			} else if ("224".equals(code)) {

				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";

			} else {
				// 响应失败

				retDesc = "调用失败,响应失败code不是200";

			}

		} else {

			retDesc = "调用失败，JSONObject中没有code";

		}

		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 个人风险分值评估体系—履约意愿：
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getPerformanceScore(JSONObject bizParams) {
		String title = "个人风险分值评估体系——履约意愿";
		String retCode = "B0001";
		String retDesc = "";
		String isbilling = "0";
		JSONObject ret = new JSONObject();

		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		if (mobile == null || "".equals(mobile)) {
			retCode = "S0001";
			retDesc = "参数错误";

			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);

			return ret.toString();
		}

		// 定制URL的路径头
		String productId = "rp-label";
		String module = "grade";
		String method = "performanceScore";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);

		logger.info("***请求地址***" + mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***get方式结果***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		// JSONObject fromObject = JSONObject.fromObject(result);
		JSONObject fromObject = JSONObject.parseObject(result);

		if (fromObject.containsKey("code")) {
			// 主要逻辑代码
			String code = fromObject.getString("code");
			if ("200".equals(code)) {
				// 响应成功
				if (fromObject.containsKey("data")) {
					JSONObject dataObj = fromObject.getJSONObject("data");
					if (dataObj.containsKey("xinYong")) {
						// 履约意愿分档
						String status = dataObj.getJSONObject("xinYong").getString("status");
						// 履约意愿得分
						String score = dataObj.getJSONObject("xinYong").getString("score");
						String statusCode = null;
						String scoreCode = null;
						String statusDesc = null;
						String scoreDesc = null;
						if (score.hashCode() >= "60".hashCode() && score.hashCode() <= "200".hashCode()) {

							scoreCode = score;
							scoreDesc = "履约意愿得分（整数）为：" + score;

						} else {
							// 结果分数不在要求范围内 请重新计算
							scoreCode = score;
							scoreDesc = "分数超出范围,输出分数范围应在：[60,200]内，整数";
						}
						// ==============履约意愿分档====================
						if ("C".equals(status) || "CC".equals(status) || "CCC".equals(status) || "B".equals(status)
								|| "BB".equals(status) || "BBB".equals(status) || "A".equals(status)
								|| "AA".equals(status) || "AAA".equals(status)) {

							statusCode = status;
							statusDesc = "履约意愿分档的等级为：" + status;

						} else {
							// 结果分数不在要求范围内 请重新计算
							statusCode = status;
							statusDesc = statusCode + "不在以下档位中，请重新计算。档位由低到高依次是：C、CC、CCC、B、BB、BBB、A、AA、AAA档";
						}

						isbilling = "1";

						JSONObject scoreObj = new JSONObject();
						scoreObj.put("scoreCode", scoreCode);
						scoreObj.put("scoreDesc", scoreDesc);

						JSONObject statusObj = new JSONObject();
						statusObj.put("statusCode", statusCode);
						statusObj.put("statusDesc", statusDesc);

						ret.put("interface", title);
						ret.put("score", scoreObj);
						ret.put("status", statusObj);
						ret.put("isbilling", isbilling);

						return ret.toString();
					} else {
						logger.info(mobile + " >> 解析失败,JSONObject中没有xinYong");

						retCode = "S0006";
						retDesc = "解析失败,JSONObject中没有xinYong";

					}

				} else {
					logger.info(mobile + " >> 解析失败,JSONObject中没有data");

					retCode = "S0006";
					retDesc = "解析失败,JSONObject中没有data";

				}

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {

				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";

			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {

				retCode = "B0002";
				retDesc = "无数据";

			} else if ("224".equals(code)) {

				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";

			} else {
				// 响应失败
				retDesc = "调用失败,响应失败code不是200";

			}

		} else {

			retDesc = "调用失败，JSONObject中没有code";

		}

		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 个人风险分值评估体系—人脉关系：
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getConnectionScore(JSONObject bizParams) {
		String title = "个人风险分值评估体系——人脉关系";
		String retCode = "B0001";
		String retDesc = "";
		String isbilling = "0";
		JSONObject ret = new JSONObject();

		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		if (mobile == null || "".equals(mobile)) {
			retCode = "S0001";
			retDesc = "参数错误";

			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);

			return ret.toString();
		}

		// 定制URL的路径头
		String productId = "rp-label";
		String module = "grade";
		String method = "connectionScore";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);

		logger.info("***请求地址***" + mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***get方式,返回json串结果***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		// JSONObject fromObject = JSONObject.fromObject(result);
		JSONObject fromObject = JSONObject.parseObject(result);

		if (fromObject.containsKey("code")) {
			// 主要逻辑代码
			String code = fromObject.getString("code");
			if ("200".equals(code)) {
				// 响应成功
				if (fromObject.containsKey("data")) {
					JSONObject dataObj = fromObject.getJSONObject("data");
					if (dataObj.containsKey("renMai")) {
						// 人脉关系分档
						String status = dataObj.getJSONObject("renMai").getString("status");
						// 人脉关系得分
						String score = dataObj.getJSONObject("renMai").getString("score");
						String statusCode = null;
						String scoreCode = null;
						String statusDesc = null;
						String scoreDesc = null;
						if (score.hashCode() >= "60".hashCode() && score.hashCode() <= "200".hashCode()) {

							scoreCode = score;
							scoreDesc = "人脉关系得分（整数）为：" + score;

						} else {
							// 结果分数不在要求范围内 请重新计算
							scoreCode = score;
							scoreDesc = "分数超出范围,输出分数范围应在：[60,200]内，整数";
						}
						// ==============人脉关系分档====================
						if ("C".equals(status) || "CC".equals(status) || "CCC".equals(status) || "B".equals(status)
								|| "BB".equals(status) || "BBB".equals(status) || "A".equals(status)
								|| "AA".equals(status) || "AAA".equals(status)) {

							statusCode = status;
							statusDesc = "人脉关系分档的等级为：" + status;

						} else {
							// 结果分数不在要求范围内 请重新计算
							statusCode = status;
							statusDesc = statusCode + "不在以下档位中，请重新计算。档位由低到高依次是：C、CC、CCC、B、BB、BBB、A、AA、AAA档";
						}

						isbilling = "1";

						JSONObject scoreObj = new JSONObject();
						scoreObj.put("scoreCode", scoreCode);
						scoreObj.put("scoreDesc", scoreDesc);

						JSONObject statusObj = new JSONObject();
						statusObj.put("statusCode", statusCode);
						statusObj.put("statusDesc", statusDesc);

						ret.put("interface", title);
						ret.put("score", scoreObj);
						ret.put("status", statusObj);
						ret.put("isbilling", isbilling);

						return ret.toString();
					} else {
						logger.info(mobile + " >> 解析失败,JSONObject中没有renMai");

						retCode = "S0006";
						retDesc = "解析失败,JSONObject中没有renMai";

					}

				} else {
					logger.info(mobile + " >> 解析失败,JSONObject中没有data");

					retCode = "S0006";
					retDesc = "解析失败,JSONObject中没有data";

				}

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {

				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";

			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {

				retCode = "B0002";
				retDesc = "无数据";

			} else if ("224".equals(code)) {

				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";

			} else {
				// 响应失败
				retDesc = "调用失败,响应失败code不是200";

			}

		} else {

			retDesc = "调用失败，JSONObject中没有code";

		}

		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码-Meid号（后四位）验证：
	 * 
	 * @param bizParainms
	 * @return
	 *  
	 */
	public String getMeidFlag(JSONObject bizParams) {
		String title = "手机号码-Meid号（后四位）验证";
		String retCode = "B0001";
		String retDesc = "";
		String isbilling = "0";
		JSONObject ret = new JSONObject();

		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		if (mobile == null || "".equals(mobile)) {
			retCode = "S0001";
			retDesc = "参数错误";

			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);

			return ret.toString();
		}

		String productId = "rp-label";
		String module = "rp-label-status";// rp-label-communication
		String method = "meidFlag";// verifyMeidFlag

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		// 截取手机号后四位
		String meid = mobile.substring(mobile.length() - 4);
		reqUrl = reqUrl + "&meid=" + meid;
		logger.info("***请求地址***" + mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***get方式,返回json串结果***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		// JSONObject fromObject = JSONObject.fromObject(result);
		JSONObject fromObject = JSONObject.parseObject(result);

		if (fromObject.containsKey("code")) {
			String code = fromObject.getString("code");
			if (code.equals("200")) {
				// 响应成功
				if (fromObject.containsKey("data")) {
					String value = fromObject.getJSONObject("data").getString("value");

					retCode = value;
					retDesc = "0：是; 1：否。";
					isbilling = "1";

				} else {
					logger.info(mobile + " >> 解析失败,JSONObject中没有data");

					retCode = "S0006";
					retDesc = "解析失败,JSONObject中没有data";

				}

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {

				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";

			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {

				retCode = "B0002";
				retDesc = "无数据";

			} else if ("224".equals(code)) {

				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";

			} else {
				// 响应失败
				retDesc = "调用失败,响应失败code不是200";

			}

		} else {

			retDesc = "调用失败，JSONObject中没有code";

		}

		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

	/**
	 * 手机号码-漫游地最后一次通话时间：
	 * 
	 * @param bizParams
	 * @return
	 *  
	 */
	public String getRoamLastTalkTime(JSONObject bizParams) {
		String title = "手机号码-漫游地最后一次通话时间：";
		String retCode = "B0001";
		String retDesc = "";
		String isbilling = "0";
		JSONObject ret = new JSONObject();

		String mobile = bizParams.containsKey("mobile") ? bizParams.getString("mobile") : "";
		String month = bizParams.containsKey("month") ? bizParams.getString("month") : "";
		String type = bizParams.containsKey("encryptionType") ? bizParams.getString("encryptionType") : "clear";
		
		if (mobile.equals("") || mobile == null || month == null || month.equals("")) {
			retCode = "S0001";
			retDesc = "参数错误";

			ret.put("interface", title);
			ret.put("code", retCode);
			ret.put("desc", retDesc);
			ret.put("isbilling", isbilling);

			return ret.toString();
		}

		String productId = "rp-label";
		String module = "rp-label-communication";
		String method = "roamLastTalkTime";

		// 获取带手机号参数的url
		String reqUrl = bdcscHelper.getRuquestUrl(productId, module, method, mobile, type);
		reqUrl = reqUrl + "&month=" + month;

		logger.info("***请求地址***" + mobile + " >> " + title + " | Url:" + reqUrl);

		// 声明请求方式为 get方式 返回json串结果
		String result = HttpRequest.sendGet(reqUrl, null);
		logger.info("***get方式结果***" + result);

		if (result == null || result.equals("")) {
			JSONObject ret1 = new JSONObject();
			ret1.put("interface", title);
			ret1.put("code", "B0001");
			ret1.put("desc", "调用失败");
			ret1.put("isbilling", "0");
			return ret1.toString();
		}

		// JSONObject fromObject = JSONObject.fromObject(result);
		JSONObject fromObject = JSONObject.parseObject(result);
		if (fromObject.containsKey("code")) {
			String code = fromObject.getString("code");
			if (code.equals("200")) {
				if (fromObject.containsKey("data")) {
					String value = fromObject.getJSONObject("data").getString("value");

					retCode = value;
					retDesc = "漫游地最后一次通话时间点";
					isbilling = "1";

				} else {
					logger.info(mobile + " >> 解析失败");

					retCode = "S0006";
					retDesc = "解析失败";

				}

			} else if ("403".equals(code) || "401".equals(code) || "412".equals(code) || "418".equals(code)
					|| "421".equals(code) || "422".equals(code)) {

				// bdcscHelper.resetCachedToken();
				retCode = "S0008";
				retDesc = "过于频繁，请稍候再试！";

			} else if ("204".equals(code) || "221".equals(code) || "222".equals(code) || "223".equals(code)
					|| "225".equals(code)) {

				retCode = "B0002";
				retDesc = "无数据";

			} else if ("224".equals(code)) {

				retCode = "B0003";
				retDesc = "未知";
				isbilling = "1";

			} else {
				// 响应失败
				retDesc = "调用失败,响应失败code不是200";

			}

		} else {

			retDesc = "调用失败，JSONObject中没有code";

		}

		ret.put("interface", title);
		ret.put("code", retCode);
		ret.put("desc", retDesc);
		ret.put("isbilling", isbilling);

		return ret.toString();
	}

}
