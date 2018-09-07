package com.bonc.credit.service.mobi;

import com.bonc.util.MqUtil;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.service.huadao.HuadaoHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 魔比接口注册
 * 
 * @author zhijie.ma
 * @date 2017年5月9日
 * 
 */
@Service
@Transactional
public class MobiInterfaceRegist {

	private static final Logger logger = Logger.getLogger(MobiInterfaceRegist.class);

	@Autowired
	private MobiServicePart1 mobiServicePart1;

	@Autowired
	MqUtil mqUtil;

	public String distributeInterfaceRegist(String providerCode, JSONObject bizParams, String uuid) {
		JSONObject jsonObject = new JSONObject();
		Long aa=System.currentTimeMillis();
		String result=null;
		if (providerCode.equals("CSN300003")) {

			/*
			 * 三元素验真 透传运营商验真结果，针对魔比需求专用，注意结算
			 */
			result=mobiServicePart1.verifyUserIdCardInfoV2(bizParams);

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
