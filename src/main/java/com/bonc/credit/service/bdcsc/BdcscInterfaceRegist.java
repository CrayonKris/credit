package com.bonc.credit.service.bdcsc;

import com.bonc.util.MqUtil;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.ProjectErrorInformation;

import java.util.HashMap;
import java.util.Map;

/**
 * 电信云项目接口注册
 * 
 * @author zhijie.ma
 * @date 2017年5月4日
 * 
 */
@Service
@Transactional
public class BdcscInterfaceRegist {

	private static final Logger logger = Logger.getLogger(BdcscInterfaceRegist.class);

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
	@Autowired
	MqUtil mqUtil;

	/**
	 * 电信云项目接口注册
	 * 
	 * @param providerCode
	 * @return
	 * @
	 */
	public String distributeInterfaceRegist(String providerCode, JSONObject bizParams, String uuid) {

		JSONObject jsonObject = new JSONObject();
		
		if(bizParams.containsKey("province")){
			bizParams.put("encryptionType", "md5");
		}
		String result="";
		Long aa=System.currentTimeMillis();
		if (providerCode.equals("talkTimeLengthDawnPtgIn3MonthsScore")) {

			// “手机号码”最近三个月凌晨时段通话时长占比/分级
			result=bdcscServicePart1.getTalkTimeLengthDawnPtgIn3MonthsScore(bizParams);

		} else if (providerCode.equals("socialCircleSize")) {

			// “手机号码”月交往圈大小/分级
			result=bdcscServicePart1.getSocialCircleSize(bizParams);

		} else if (providerCode.equals("callFocusScore")) {

			// “手机号码”主叫月通话集中度/分级
			result=bdcscServicePart1.getCallFocusScore(bizParams);

		} else if (providerCode.equals("abnormalContactScore")) {

			// 异常交往指数得分（主动/被动联系黑名单号码池）
			result=bdcscServicePart2.getAbnormalContactScore(bizParams);

		} else if (providerCode.equals("roamCityList")) {

			// 手机号码漫游城市列表
			result=bdcscServicePart2.getRoamCityList(bizParams);

		} else if (providerCode.equals("calledTimeLengthAvg20Ptg")) {

			// 获取通话时长在前20%联系人的平均被叫时长
			result=bdcscServicePart2.getCalledTimeLengthAvg20Ptg(bizParams);

		} else if (providerCode.equals("callMaxDaysCity")) {

			// 获取指定月通话天数最多的城市
			result=bdcscServicePart2.getCallMaxDaysCity(bizParams);

		} else if (providerCode.equals("callCountTop5Label")) {

			// 获取TOP5联系人主叫通话次数
			result=bdcscServicePart2.getCallCountTop5Label(bizParams);

		} else if (providerCode.equals("suspendTimesIn3MonthsLabel")) {

			// 最近3个月手机号码停机次数
			result=bdcscServicePart2.getSuspendTimesIn3MonthsLabel(bizParams);

		} else if (providerCode.equals("lastTalkCity")) {

			// 最后一次通话所在城市
			result=bdcscServicePart2.getLastTalkCity(bizParams);

		} else if (providerCode.equals("timeLengthLabel")) {

			// “手机号码”在网时长/分级
			result=bdcscServicePart2.getTimeLengthLabel(bizParams);

		} else if (providerCode.equals("accessNumberCountLabel")) {

			// “手机号码”自然人接入号码个数/分级
			result=bdcscServicePart2.getAccessNumberCountLabel(bizParams);

		} else if (providerCode.equals("phoneNumState")) {

			// “手机号码”当前状态查询
			result=bdcscServicePart3.getPhoneNumState(bizParams);

		} else if (providerCode.equals("membershipLevel")) {

			// 手机号码-会员级别
			result=bdcscServicePart3.getMembershipLevel(bizParams);

		} else if (providerCode.equals("flowTimeLengthAvgLabel")) {

			// 手机号码日均上网时长/分级
			result=bdcscServicePart3.getFlowTimeLengthAvgLabel(bizParams);

		} else if (providerCode.equals("flowTimeLengthLabel")) {

			// 手机号码月上网时长/分级
			result=bdcscServicePart3.getFlowTimeLengthLabel(bizParams);

		} else if (providerCode.equals("flowForEGameLabel")) {

			// 指定月份流量
			result=bdcscServicePart3.getFlowForEGameLabel(bizParams);

		} else if (providerCode.equals("talkTimeLengthIn3MonthsAvgLabel")) {

			// 手机号码最近三月通话时长的均值/分级
			result=bdcscServicePart3.getTalkTimeLengthIn3MonthsAvgLabel(bizParams);

		} else if (providerCode.equals("roamCountIn3MonthsLabel")) {

			// “手机号码”最近三个月漫游次数/分级
			result=bdcscServicePart3.getRoamCountIn3MonthsLabel(bizParams);

		} else if (providerCode.equals("talkFocusScore")) {

			// 获取指定月份手机通话集中度得分
			result=bdcscServicePart3.getTalkFocusScore(bizParams);

		} else if (providerCode.equals("talkTimeLengthLabel")) {

			// 获取指定月通话时长阶梯
			result=bdcscServicePart4.getTalkTimeLengthLabel(bizParams);

		} else if (providerCode.equals("callForwardCount3MonthsLabel")) {

			// 手机最近三个月呼转次数
			result=bdcscServicePart4.getCallForwardCount3MonthsLabel(bizParams);

		} else if (providerCode.equals("callDaysCommunicate")) {

			// 获取指定月主动通信活跃天数
			result=bdcscServicePart4.getCallDaysCommunicate(bizParams);

		} else if (providerCode.equals("callTimeLengthLabelV2")) {

			// 手机号码主叫通话时长
			result=bdcscServicePart4.getCallTimeLengthLabelV2(bizParams);

		} else if (providerCode.equals("calledTimeLengthLabelV2")) {

			// 手机号码被叫通话时长
			result=bdcscServicePart4.getCalledTimeLengthLabelV2(bizParams);

		} else if (providerCode.equals("paymentIn3MonthsAvgLabel")) {

			// 手机号码-连续三个月缴费金额均值区间
			result=bdcscServicePart4.getPaymentIn3MonthsAvgLabel(bizParams);

		} else if (providerCode.equals("overdueBillLabel")) {

			// “手机号码”欠费金额/分级
			result=bdcscServicePart4.getOverdueBillLabel(bizParams);

		} else if (providerCode.equals("paymentAmountLabel")) {

			// 手机号码-缴费总额阶梯（月度）
			result=bdcscServicePart4.getPaymentAmountLabel(bizParams);

		} else if (providerCode.equals("terminalChangeFrequencyLabel")) {

			// “手机号码”换机频率查询/分级
			result=bdcscServicePart4.getTerminalChangeFrequencyLabel(bizParams);

		} else if (providerCode.equals("terminalModelNumber")) {

			// “手机号码”终端型号查询
			result=bdcscServicePart4.getTerminalModelNumber(bizParams);

		} else if (providerCode.equals("suspendDaysIn3MonthsLabel")) {

			// 最近3个月手机号码停机天数
			result=bdcscServicePart5.getSuspendDaysIn3MonthsLabel(bizParams);

		} else if (providerCode.equals("fixedNoNameFlag")) {

			// 座机号码-姓名二元组验证接口
			result=bdcscServicePart5.getFixedNoNameFlag(bizParams);

		} else if (providerCode.equals("timeLengthForTNLabel")) {

			// 入网时长阶梯
			result=bdcscServicePart5.getTimeLengthForTNLabel(bizParams);

		} else if (providerCode.equals("openDateLabel")) {

			// 手机号码最近一次启用时间
			result=bdcscServicePart5.getOpenDateLabel(bizParams);

		} else if (providerCode.equals("groupNameFlag")) {

			// 集团客户验证
			result=bdcscServicePart5.getGroupNameFlag(bizParams);

		} else if (providerCode.equals("talkTimeLengthDawnPtgScore")) {

			// 手机号码-指定月凌晨时段通话时长占比得分
			result=bdcscServicePart3.getTalkTimeLengthDawnPtgScore(bizParams);

		} else if (providerCode.equals("hobbyLabelMonthly")) {
			/**
			 * 此接口有问题 待解决
			 */
			// 手机号码-最近一个月兴趣标签
			result=Bdcsc2ServicePart1.getHobbyLabelMonthly(bizParams);

		} else if (providerCode.equals("hobbyLabelByMeidMonthly")) {
			/**
			 * 此接口有问题 待解决
			 */
			// MEID-最近一个月兴趣标签
			result=Bdcsc2ServicePart1.getHobbyLabelByMeidMonthly(bizParams);

		} else if (providerCode.equals("CSM161222_1508")) {

			// 手机号码-资金需求指数得分
			result=bdcscServicePart6.getFundDemandScore(bizParams);

		} else if (providerCode.equals("CSM161226_1521")) {

			// 手机号码-Meid号（后四位）验证:
			result=bdcscServicePart6.getMeidFlag(bizParams);

		} else if (providerCode.equals("CSM161226_1522")) {

			// 手机号码-漫游地最后一次通话时间：
			result=bdcscServicePart6.getRoamLastTalkTime(bizParams);

		} else if (providerCode.equals("verifyUserIdCardInfo2")) {

			// 此接口为三元素md5的问题 还未上线 已通过测试

			result=null;

		} else if (providerCode.equals("province")) {

			// 手机号归属省
			result=bdcscServicePart1.getProvince(bizParams);

		} else if (providerCode.equals("verifyUserIdCardInfo")) {

			// 手机号码-证件类型-证件号码-姓名核验
			result=bdcscServicePart1.verifyUserIdCardInfoV2(bizParams);

		} else if (providerCode.equals("verifyUserIdCardNo")) {

			// 手机号码-证件类型-证件号码核验
			result=bdcscServicePart4.verifyUserIdCardNo(bizParams);

		} else if (providerCode.equals("realnameFlag")) {

			// “手机号码”实名制核验
			result=bdcscServicePart1.getRealnameFlag(bizParams);

		} else if (providerCode.equals("userGender")) {

			// “手机号码-性别”获取
			result=bdcscServicePart3.getUserGender(bizParams);

		} else if (providerCode.equals("verifyUserName")) {

			// 手机号码-姓名校验
			result=bdcscServicePart3.getVerifyUserName(bizParams);

		} else if (providerCode.equals("userBIP3")) {

			// 手机号码-年龄分级-性别-姓名校验
			result=bdcscServicePart1.userBIP3(bizParams);

		} else if (providerCode.equals("userAgeLabel")) {

			// “手机号码”自然人年龄查询/分级
			result=bdcscServicePart1.getUserAgeLabel(bizParams);

		} else if (providerCode.equals("city")) {

			// 手机号归属市
			result=bdcscServicePart1.getCity(bizParams);

		} else if (providerCode.equals("provinceCity")) {

			// 手机号码-归属省市
			result=bdcscServicePart3.getProvinceCity(bizParams);

		} else if (providerCode.equals("flowLabel")) {

			// 手机号码”月上网流量/分级
			result=bdcscServicePart1.getFlowLabel(bizParams);

		} else if (providerCode.equals("balanceLabel")) {

			// “手机号码”余额/分级
			result=bdcscServicePart1.getBalanceLabel(bizParams);

		} else if (providerCode.equals("balanceLabelV2")) {

			// “手机号码”余额/分级（II）
			result=bdcscServicePart4.getBalanceLabelV2(bizParams);

		} else if (providerCode.equals("CSM161118_0950")) {

			// 手机号码-证件类型-证件号码核验(电信联通及移动的合并版本支持) 2016.11.18
			result=bdcscServicePart4.verifyUserIdCardNoFor3Net(bizParams);

		} else if (providerCode.equals("CSM161222_1703")) {

			// 手机号码-预/后付费类型
			result=bdcscServicePart6.getPaymentPreType(bizParams);

		} else if (providerCode.equals("CSM161223_1111")) {

			// 个人风险分值评估体系——总分
			result=bdcscServicePart6.getRiskScoreEvaluation(bizParams);

		} else if (providerCode.equals("CSM161223_1439")) {

			// 个人风险分值评估体系——身份特征
			result=bdcscServicePart6.getStatusScore(bizParams);

		} else if (providerCode.equals("CSM161223_1645")) {

			// 个人风险分值评估体系—行为偏好
			result=bdcscServicePart6.getBehaviorScore(bizParams);

		} else if (providerCode.equals("CSM161226_1049")) {

			// 个人风险分值评估体系—消费能力：
			result=bdcscServicePart6.getConsumeScore(bizParams);

		} else if (providerCode.equals("CSM161226_1102")) {

			// 个人风险分值评估体系—履约意愿：
			result=bdcscServicePart6.getPerformanceScore(bizParams);

		} else if (providerCode.equals("CSM161226_1110")) {

			// 个人风险分值评估体系—人脉关系：
			result=bdcscServicePart6.getConnectionScore(bizParams);

		} else if (providerCode.equals("verifyUserIdCardInfoV2")) {

			// 手机号码-证件类型-证件号码-姓名核验
			result=bdcscServicePart1.verifyUserIdCardInfoV2(bizParams);

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
