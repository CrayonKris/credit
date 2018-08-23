package com.bonc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 本类集中了项目中所有类型的错误信息
 * 
 * @author zhijie.ma
 * @date 2017年5月11日
 * 
 */
public class ProjectErrorInformation {
	
	private final static Logger logger = LoggerFactory.getLogger(ProjectErrorInformation.class);
	
	private static JSONObject ret = new JSONObject();
	
	/*******************     系统级错误码              *******************/

	/**
	 * 该账户审核中或已停用
	 * @param ret
	 * @return
	 */
	public static String systemError1(String title) {
		ret.put("interface", title);
		ret.put("code", "S0001");
		ret.put("desc", "该账户审核中或已停用");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}

	/**
	 * appKey不存在或已失效
	 * @param ret
	 * @return
	 */
	public static String systemError2(String title) {
		ret.put("interface", title);
		ret.put("code", "S0002");
		ret.put("desc", "appKey不存在或已失效");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}

	/**
	 * IP验证失败
	 * @param ret
	 * @return
	 */
	public static String systemError3(String title) {
		ret.put("interface", title);
		ret.put("code", "S0003");
		ret.put("desc", "IP验证失败");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}

	/**
	 * token验证失败
	 * @param ret
	 * @return
	 */
	public static String systemError4(String title) {
		ret.put("interface", title);
		ret.put("code", "S0004");
		ret.put("desc", "token验证失败");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}

	/**
	 * 请与相关人员联系
	 * @param ret
	 * @return
	 */
	public static String systemError5(String title) {
		ret.put("interface", title);
		ret.put("code", "S0005");
		ret.put("desc", "请与相关人员联系");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}

	/**
	 * 解析错误
	 * @param ret
	 * @return
	 */
	public static String systemError6(String title) {
		ret.put("interface", title);
		ret.put("code", "S0006");
		ret.put("desc", "解析错误");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}

	/**
	 * 接口未授权
	 * @param ret
	 * @return
	 */
	public static String systemError7(String title) {
		ret.put("interface", title);
		ret.put("code", "S0007");
		ret.put("desc", "接口未授权");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}

	/**
	 * 过于频繁，请稍候再试
	 * @param ret
	 * @return
	 */
	public static String systemError8(String title) {
		ret.put("interface", title);
		ret.put("code", "S0008");
		ret.put("desc", "过于频繁，请稍候再试");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}

	/**
	 * 系统异常
	 * @param ret
	 * @return
	 */
	public static String systemError9(String title) {
		ret.put("interface", title);
		ret.put("code", "S0009");
		ret.put("desc", "系统异常");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}

	/**
	 * 账户余额不足
	 * @param ret
	 * @return
	 */
	public static String systemError10(String title) {
		ret.put("interface", title);
		ret.put("code", "S0010");
		ret.put("desc", "账户余额不足");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}

	/**
	 * 接口不存在
	 * @param ret
	 * @return
	 */
	public static String systemError11(String title) {
		ret.put("interface", title);
		ret.put("code", "S0011");
		ret.put("desc", "接口不存在");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}
	
	/**
	 * 接口已停用
	 * @param ret
	 * @return
	 */
	public static String systemError12(String title) {
		ret.put("interface", title);
		ret.put("code", "S0012");
		ret.put("desc", "接口已停用");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}
	
	/**
	 * 用户信息验证失败
	 * @param ret
	 * @return
	 */
	public static String systemError13(String title) {
		ret.put("interface", title);
		ret.put("code", "S0013");
		ret.put("desc", "用户信息验证失败");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}
	
	/**
	 * 该产品未配置
	 * @param ret
	 * @return
	 */
	public static String systemError14(String title) {
		ret.put("interface", title);
		ret.put("code", "S0014");
		ret.put("desc", "该产品未配置");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}
	
	
	/*******************     系统级错误码              *******************/
	
	/**
	 * 调用失败
	 * @param ret
	 * @return
	 */
	public static String businessError1(String title) {
		ret.put("interface", title);
		ret.put("code", "B0001");
		ret.put("desc", "调用失败");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}

	/**
	 * 无数据
	 * @param ret
	 * @return
	 */
	public static String businessError2(String title) {
		ret.put("interface", title);
		ret.put("code", "B0002");
		ret.put("desc", "无数据");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}
	/**
	 * 该接口不支持联通/移动/电信用户   
	 * @param ret
	 * @return
	 */
	public static String businessError3(String title,String operator) {
		ret.put("interface", title);
		ret.put("code", "B0003");
		ret.put("desc", "该接口不支持"+operator+"用户");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}
	/**
	 * 其他错误
	 * @param ret
	 * @return
	 */
	public static String businessError4(String title) {
		ret.put("interface", title);
		ret.put("code", "B0004");
		ret.put("desc", "其他错误");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}
	/**
	 * 参数错误
	 * @param ret
	 * @return
	 */
	public static String businessError5(String title) {
		ret.put("interface", title);
		ret.put("code", "B0005");
		ret.put("desc", "参数错误");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}
	/**
	 * 无效手机号码
	 * @param ret
	 * @return
	 */
	public static String businessError6(String title) {
		ret.put("interface", title);
		ret.put("code", "B0006");
		ret.put("desc", "无效手机号码");
		ret.put("isbilling", "0");
		logger.info("接口访问未通过原因  >>>> "+ret.toString());
		return ret.toString();
	}
}
