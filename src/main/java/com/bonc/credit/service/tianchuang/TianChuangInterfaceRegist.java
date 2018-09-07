package com.bonc.credit.service.tianchuang;

import com.bonc.util.MqUtil;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 天创接口注册类
 * 
 * @author zhijie.ma
 * @date 2017年7月17日
 * 
 */

@Service
public class TianChuangInterfaceRegist {

	private static final Logger logger = Logger.getLogger(TianChuangInterfaceRegist.class);

	@Autowired
	private TianChuangService tianChuangService;

	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	MqUtil mqUtil;

	public String distributeInterfaceRegist(String providerCode, JSONObject bizParams, String uuid) {

		JSONObject jsonObject = new JSONObject();
		String result=null;
		Long aa=System.currentTimeMillis();
		if (providerCode.equals("CSM170717_1658")) {

			// 手机号码在网时长 	全网通
			result=tianChuangService.getOnlineTime(bizParams);

		} else if (providerCode.equals("CSM170719_1656")) {

			// 手机号码当前状态查询	 全网通
			result=tianChuangService.getState(bizParams);

		} else if (providerCode.equals("CSM170719_1657")) {

			// 验证手机号码、身份证号、姓名三要素是否匹配		 全网通
			result=tianChuangService.getVerifyMobileInfo(bizParams);

		} else if (providerCode.equals("CSM170719_1658")) {

			// 身份证号码和姓名认证 认证成功时，会返回照片流
			result=tianChuangService.getVerifyIdcard(bizParams);

		} else if (providerCode.equals("CSM170719_1660")) {

			// 身份证号码和姓名认证 ---> (常用)
			result=tianChuangService.getVerifyIdcardC(bizParams);

		} else if (providerCode.equals("CSM171017_1126")) {

			// 获取个人学历信息
			result=tianChuangService.getDegreeInfoC(bizParams);

		} else if (providerCode.equals("CSM171017_1555")) {

			// 查询指定号码的月消费档次
			result=tianChuangService.getConsumeGrade(bizParams);

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
