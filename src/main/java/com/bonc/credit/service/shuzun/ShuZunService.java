package com.bonc.credit.service.shuzun;

import com.alibaba.fastjson.JSONObject;

/**
*
* @author zhijie.ma
* @date 2018年6月20日
*
*/
public interface ShuZunService {

	/**
	 * 验证姓名、身份证号、手机号三者之间的匹配结果    全网通
	 * @param params
	 * @return
	 */
	String getVerifyMobileInfo(JSONObject params);

	/**
	 * 验证姓名、身份证号 两者之间的匹配结果  全网通
	 *
	 */
	String getVerifyIdentifyInfo(JSONObject params);

	/**
	 * 电信定制-常用联系人
	 * @param params
	 * @return
	 */
	String getTelecomContacts(JSONObject params);

	/**
	 * 联通定制-常用联系人
	 * @param params
	 * @return
	 */
	String getUnicomContacts(JSONObject params);

	/**
	 * 运营商在网时长      全网通
	 * @param params
	 * @return
	 */
	String getOnlineTime(JSONObject params);

	/**
	 * 用户状态         全网通
	 * @param params
	 * @return
	 */
	String getUserState(JSONObject params);

	/**
	 * 联通定制-手机号码交往圈大小（得分）
	 * @param bizParams
	 * @return
	 */
	String getUnicomCircle(JSONObject bizParams);

	/**
	 * 电信定制-手机号码交往圈大小（得分）
	 * @param bizParams
	 * @return
	 */
	String getTelecomCircle(JSONObject bizParams);

	/**
	 * 验证姓名、身份证号、手机号是否匹配(完整版)
	 * @param bizParams
	 * @return
	 */
    String getVerifyMobileInfoV(JSONObject bizParams);

	/**
	 * 工作地址位置验证
	 * @param bizParams
	 * @return
	 */
	String getWorkAddressPosition(JSONObject bizParams);

	/**
	 * 手机号当前停留城市验证
	 * @param bizParams
	 * @return
	 */
    String getCurrentStayCity(JSONObject bizParams);

	/**
	 * 验证姓名、身份证号、手机号是否匹配(完整版)
	 * @param bizParams
	 * @return
	 */
    String getVerifyVerificationIII(JSONObject bizParams);
}
