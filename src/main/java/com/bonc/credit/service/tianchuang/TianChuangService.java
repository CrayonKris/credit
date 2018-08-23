package com.bonc.credit.service.tianchuang;

import com.alibaba.fastjson.JSONObject;

/**
*	天创业务接口
* @author zhijie.ma
* @date 2017年7月17日
* 
*/

public interface TianChuangService {
	
	/**
	 * 手机号码在网时长		全网通
	 * @param params
	 * @return
	 */
	public String getOnlineTime(JSONObject params);
	
	/**
	 * 手机号码当前状态查询	全网通
	 * @param params
	 * @return
	 */
	public String getState(JSONObject params);
	
	/**
	 * 验证手机号码、身份证号、姓名三要素是否匹配		全网通
	 * @param params
	 * @return
	 */
	public String getVerifyMobileInfo(JSONObject params);
	
	/**
	 * 身份证号码和姓名认证
	 * @param params
	 * @return	认证成功时，会返回照片流
	 */
	public String getVerifyIdcard(JSONObject params);
	
	/**
	 * 身份证号码和姓名认证	--->	(常用)
	 * @param params
	 * @return	仅返回认证结果
	 */
	public String getVerifyIdcardC(JSONObject params);
	
	/**
	 * 获取个人学历信息
	 * @param params
	 * @return
	 */
	public String getDegreeInfoC(JSONObject params);
	
	/**
	 * 查询指定号码的月消费档次
	 * @param params
	 * @return
	 */
	public String getConsumeGrade(JSONObject params);
}
