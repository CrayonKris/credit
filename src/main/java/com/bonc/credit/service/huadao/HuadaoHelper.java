package com.bonc.credit.service.huadao;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author zhijie.ma
 * @date 2017年5月9日
 *
 */
@Service
public class HuadaoHelper {
	private static final Logger logger = Logger.getLogger(HuadaoHelper.class);
	public static final String privateKey = "22cb14cf6ea8cd1464bad7d5f8cd7ea0";
	public static final String account = "test0801";
	public static final String baseUrl = "https://api.sinowaycredit.com:8093/SinowayApi/authticateService/";
	public static final String baseUrl2 = "https://api.sinowaycredit.com:8093/SinowayApi/";
	
	public static String paramsError() {
		JSONObject ret = new JSONObject();
		logger.info("参数错误");
		
		ret.put("interface", "");
		ret.put("code", "S0001");
		ret.put("desc", "参数错误");
		ret.put("isbilling", "0");
		return ret.toString();
	}
	
}
