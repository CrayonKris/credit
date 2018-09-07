package com.bonc.credit.service.huadao;

import com.bonc.util.MqUtil;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 华道接口注册
 * 
 * @author zhijie.ma
 * @date 2017年5月9日
 * 
 */
@Service
@Transactional
public class HuaDaoInterfaceRegist {

	private static final Logger logger = Logger.getLogger(HuaDaoInterfaceRegist.class);

	@Autowired
	private HuadaoServicePart1 huadaoServicePart1;

	@Autowired
	private HuadaoServicePart2 huadaoServicePart2;

	@Autowired
	MqUtil mqUtil;

	public String distributeInterfaceRegist(String providerCode, JSONObject bizParams, String uuid) {
		JSONObject jsonObject = new JSONObject();
		Long aa=System.currentTimeMillis();
		String result="";
		if (providerCode.equals("CSM161215_1619")) {

			// 终端更换频率评分
			result=huadaoServicePart2.getTerminalScoreUrl(bizParams);

		} else if (providerCode.equals("timeLengthLabelM2")) {

			// “手机号码”在网时长/分级
			result=huadaoServicePart1.getTimeLengthLabel(bizParams);

		} else if (providerCode.equals("CSM170425_1455")) {

			// 用户单月流量评分
			result=huadaoServicePart2.CSM170425_1455(bizParams);

		} else if (providerCode.equals("CSM170425_1539")) {

			// 手机号码主叫/被叫通话时长评分
			result=huadaoServicePart2.CSM170425_1539(bizParams);

		} else if (providerCode.equals("verifyUserName")) {

			// 手机号码-姓名校验
			result=huadaoServicePart1.getVerifyUserName(bizParams);

		} else if (providerCode.equals("userAgeLabel")) {

			// 手机号码自然人年龄查询/分级
			result=huadaoServicePart1.getUserAgeLabel(bizParams);

		} else if (providerCode.equals("phoneNumState")) {

			// “手机号码”当前状态查询 2016.11.18
			result=huadaoServicePart1.getPhoneNumState(bizParams);

		} else if (providerCode.equals("CSM161118_0950")) {

			// 手机号码-证件类型-证件号码核验(电信联通及移动的合并版本支持) 2016.11.18
			result=huadaoServicePart1.verifyUserIdCardNoFor3Net(bizParams);

		} else if (providerCode.equals("CSM161121_1459")) {

			// "手机号码"单月话费分级
			result=huadaoServicePart2.balanceLabel(bizParams);

		} else if (providerCode.equals("CSM161120_1041")) {

			// 手机号码主叫通话时长
			result=huadaoServicePart2.CSM161120_1041(bizParams);

		} else if (providerCode.equals("CSM161120_1606")) {

			// 手机号码被叫通话时长
			result=huadaoServicePart2.CSM161120_1606(bizParams);

		} else if (providerCode.equals("verifyUserIdCardInfoV2")) {

			// “手机号码-证件类型-证件号码-姓名”核验
			result=huadaoServicePart1.verifyUserIdCardInfoV2(bizParams);

		} else if (providerCode.equals("")) {

			result=null;

		} else {
			jsonObject.put("interface", "");
			jsonObject.put("code", "B0003");
			jsonObject.put("desc", "接口未知错误！请联系管理员！！！");
			jsonObject.put("isbilling", "0");
			result=jsonObject.toString();
		}
		Long bb=System.currentTimeMillis();
		Long allTime=bb-aa;
		mqUtil.addRecordTime(allTime,uuid,bizParams,bb);
		return result;
	}
}
