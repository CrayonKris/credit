package com.bonc.credit.service.zhongchengxin;

import com.bonc.util.MqUtil;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.service.mobi.MobiInterfaceRegist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 中诚信接口注册
 * 
 * @author zhijie.ma
 * @date 2017年5月11日
 * 
 */
@Service
public class ZhongChengXinInterfaceRegist {

	private static final Logger logger = Logger.getLogger(ZhongChengXinInterfaceRegist.class);

	@Autowired
	private ZhongchengxinServicePart1 zhongchengxinServicePart1;

	@Autowired
	private ZhongchengxinServicePart1ByZyx zhongchengxinServicePart1ByZyx;

	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	MqUtil mqUtil;

	public String distributeInterfaceRegist(String providerCode, JSONObject bizParams, String uuid) {

		JSONObject jsonObject = new JSONObject();
		String result=null;
		Long aa=System.currentTimeMillis();
		if (providerCode.equals("verifyUserIdCardInfoV2")) {

			// 手机号码-证件类型-证件号码-姓名核验（本接口仅支持移动）
			result=zhongchengxinServicePart1.getRealnameVer(bizParams);

		} else if (providerCode.equals("CSM180624_1654")) {

			//手机号码-证件号码-姓名核验（本接口仅支持移动）
			result=zhongchengxinServicePart1.getVerifyMobileInfo(bizParams);

		} else if (providerCode.equals("CSM180710_1628")) {

			//姓名，身份证号码，银行卡号验证对应关系是否一致.
			result=zhongchengxinServicePart1.getVerifyVerificationInfo(bizParams);

		} else if (providerCode.equals("CSM180710_1654")) {

			//姓名，身份证号码，银行卡号，手机号验证是否一致
			result=zhongchengxinServicePart1.getVerifyVerificationInfoIV(bizParams);

		} else if (providerCode.equals("CSM180711_1438")) {


			//姓名-身份证号-手机号码核验 (详版)
			result=zhongchengxinServicePart1.getVerifyVerificationInfoIII(bizParams);

		} else if (providerCode.equals("CSM180716_1153")){

			//运营商在网状态 全网通
			result=zhongchengxinServicePart1.getUserState(bizParams);
		} else if (providerCode.equals("CSM180716_1156")){

			//运营商在网时长 全网通
			result=zhongchengxinServicePart1.getUserTime(bizParams);
		} else if (providerCode.equals("CSM180716_1157")){

			//三要素简版  全网通
			result=zhongchengxinServicePart1ByZyx.getVerifyMobileInfo(bizParams);
		} else if (providerCode.equals("CSM180716_1158")){

			//三要素简版2.0  全网通
			result=zhongchengxinServicePart1.getVerifyMobileInfoII(bizParams);
		} else if(providerCode.equals("CSM180816_1848")){
			//二要素验证  全网通
			result=zhongchengxinServicePart1ByZyx.getCidNameInfo(bizParams);
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
