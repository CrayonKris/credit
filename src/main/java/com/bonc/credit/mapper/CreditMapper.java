package com.bonc.credit.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * @author zhijie.ma
 * @date 2017年5月2日
 *
 */
@Mapper
public interface CreditMapper {
	
	/**
	 * 获取地市名称
	 * @param cityCode	城市编码
	 * @return
	 */
	public String getCityName(String cityCode);
	
	/**
	 * 获取省份名称
	 * @param provinceCode	省份编码
	 * @return
	 */
	public String getProvinceName(String provinceCode);
	
	/**
	 * 获取接口注册信息
	 * @param labelName	接口名
	 * @param providerType	运营商类型
	 * @return
	 */
	public List<Map<String,String>> getInterfaceInformation(@Param("labelName") String labelName,@Param("providerType") String providerType);
	
	/**
	 * 添加接口访问记录
	 * @param map
	 */
	public void addVisitRecord(Map<String, Object> map);
	
	/**
	 * 添加一个渠道账户
	 * @param map
	 */
	public void addChannel(Map<String,Object> map);
	
	/**
	 * 查询一个账户信息
	 * @param map <br>
	 * 		account		账户名称	<br>
	 * 		password	账户密码	<br>
	 * 		appKey		密钥	<br>
	 * 		channel_code	渠道编码	<br>
	 * @return
	 */
	public Map<String,Object> getChannelByAccount(Map<String,Object> map);
	
	/**
	 * 进行IP验证
	 * @param map <br>
	 * 		channel_code 渠道编码
	 * @return
	 */
	public List<Map<String,String>> getIPvilidate(Map<String,Object> map);
	
	/**
	 * 更新接口访问次数
	 * @param channelCode
	 */
	public void updateVisitCount(Map<String,Object> channelCode);
	
	/**
	 * 查看接口剩余次数
	 * @param map	 <br/>
	 * 		channel_code 渠道编码 <br/>
	 * 		product_number 产品编码
	 * @return
	 */
	public Integer selectVisitCount(Map<String,Object> map);
	
	/**
	 * 查询产品的单个信息
	 * @param method	接口名称
	 * @return
	 */
	public Map<String,String> getProductInformation(String method);
	
	/**
	 * 手机号码为md5时，根据接口名称判断调用哪个方法
	 * @param method	接口名称
	 * @return
	 */
	public List<Map<String,String>> getInterfaceMobile(String method);

	/**
	 * 添加本次访问的总响应时长
	 * @param map
	 */
	public void insertRecordTime(Map<String,Object> map);
}
