package com.bonc.credit.service.zhongsheng;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 中胜接口注册
 * 
 * @author zhijie.ma
 * @date 2017年5月8日
 * 
 */
@Service
@Transactional
public class ZhongShengInterfaceRegist {

	private static final Logger logger = Logger.getLogger(ZhongShengInterfaceRegist.class);

	@Autowired
	private ZhongShengServicePart1 zhongShengServicePart1;

	@Autowired
	private ZhongShengServicePart2 zhongShengServicePart2;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public String distributeInterfaceRegist(String providerCode, JSONObject bizParams, String uuid) {

		JSONObject jsonObject = new JSONObject();
		String result=null;
		Long aa=System.currentTimeMillis();
		if (providerCode.equals("verifyUserIdCardInfoV2")) {

			// 手机号码-证件类型-证件号码-姓名核验（本接口，仅支持移动）
			result=zhongShengServicePart1.verifyUserIdCardInfoV2(bizParams);

		} else if (providerCode.equals("CSM161118_0950")) {

			// 手机号码-证件类型-证件号码核验(目前支持联通电信)
			result=zhongShengServicePart1.verifyUserIdCardNoFor3Net(bizParams);

		} else if (providerCode.equals("timeLengthLabel")) {

			// “手机号码”在网时长/分级
			result=zhongShengServicePart1.getTimeLengthLabel(bizParams);

		} else if (providerCode.equals("phoneNumState")) {

			// “手机号码”当前状态查询
			result=zhongShengServicePart1.getPhoneNumState(bizParams);

		} else if (providerCode.equals("verifyUserName")) {

			// 手机号码-姓名校验
			result=zhongShengServicePart1.verifyUserName(bizParams);

		} else if (providerCode.equals("CSM161130_1435")) {

			// 手机号码最近1个月内的平均消费区间段(目前仅支持移动 2016.11.30) --对方暂无授权，本接口暂不开放
			result=zhongShengServicePart2.getCSM1611301635(bizParams);

		} else if (providerCode.equals("CSM161130_1456")) {

			// 手机号码最近3个月内的平均消费区间段(目前仅支持移动 2016.11.30)--对方暂无授权，本接口暂不开放
			result=zhongShengServicePart2.getCSM161130_1456(bizParams);

		} else if (providerCode.equals("CSM161130_1518")) {

			// 手机号码最近6个月内的平均消费区间段(目前仅支持移动 2016.11.30)--对方暂无授权，本接口暂不开放
			result=zhongShengServicePart2.getCSM161130_1518(bizParams);

		} else if (providerCode.equals("CSM161130_1532")) {

			// 手机号码最近12个月内的平均消费区间段(目前仅支持移动 2016.11.30)--对方暂无授权，本接口暂不开放
			result=zhongShengServicePart2.getCSM161130_1532(bizParams);

		} else if (providerCode.equals("CSM170123_1427")) {

			// 银行卡验证接口 返回描述信息粗略版本
			result=zhongShengServicePart2.getCSM170123_1427(bizParams);

		} else if (providerCode.equals("CSM170123_1427_2")) {

			// 银行卡验证接口 返回描述信息详细版本
			result=zhongShengServicePart2.getCSM170123_1427_2(bizParams);

		} else if (providerCode.equals("CSM170626_0948")) {

			// 银联个人评分
			result=zhongShengServicePart2.unionPayPersonal(bizParams);

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
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("all_time",""+allTime);
		hashMap.put("record_id",uuid);
		hashMap.put("time_type","upper");
		rabbitTemplate.convertAndSend("addRecordTime", hashMap);
		return result;

	}

}
