package com.bonc.credit.service.zhongsheng;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.bonc.redis.RedisUtil;
import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * 
 * @author zhijie.ma
 * @date 2017年5月8日
 *
 */
@Service
public class ZhongShengHelper {
	private static final Logger logger = Logger.getLogger(ZhongShengHelper.class);

	// token地址
	// private static final String tokenUrl =
	// "http://api10.g315.net:8511/wndc/tkn/queryaccount.do";
//	private static final String tokenUrl = "http://api.g315.net:8511/wndc/tkn/queryaccount.do";
	private static final String tokenUrl = "http://apt.zhixin.net:8511/wndc/tkn/queryaccount.do";
	// public static final String account = "xly_zz";
	// private static final String pwd = "zsxytest0810";
	public static final String account = "bjdfgx";
	public static final String pwd = "c7vZU69G";

	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 获取中胜的请求路径和token
	 * 
	 * @return token : token <br/>
	 *         callbackUrl : 访问路径
	 *  
	 */
	public Map<String, String> getZhongShengInformation() {
		String zhongShengToken = redisUtil.getString("zhongShengToken");
		String zhongShengUrl = redisUtil.getString("zhongShengUrl");
		Map<String, String> map = new HashMap<String, String>();
		if (null == zhongShengToken || "".equals(zhongShengToken) || null == zhongShengUrl
				|| "".equals(zhongShengUrl)) {
			logger.info("获取token为空，重新获取！");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			map = getToken();
			redisUtil.setStringTime("zhongShengToken", map.get("token"), 60 * 60);
			redisUtil.setStringTime("zhongShengUrl", map.get("callbackUrl"), 60 * 60);

		} else {
			map.put("token", zhongShengToken);
			map.put("callbackUrl", zhongShengUrl);
		}

		logger.info("获取到中胜的token为：" + map);
		return map;
	}

	/**
	 * 获取token 中胜请求的token强制在短时间内不允许重复获取 所以建议 60分钟后再重新获取
	 * 
	 * @return
	 *  
	 */
	public Map<String, String> getToken() {

		String msg = account + MD5Builder.md5(pwd).toUpperCase().substring(8, 24);

		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("action", "login");
		reqParams.put("token", "");
		reqParams.put("msg", msg);

		String reqUrl = HttpRequest.getUrl(reqParams, tokenUrl);
		logger.info("token url >> " + reqUrl);

		String response = HttpRequest.sendGet(reqUrl, null);
		logger.info("token response >> " + response);

		// 解析token的json串
		JSONObject jsonObject = JSONObject.parseObject(response);
		String token = jsonObject.getString("msg");
		String callbackUrl = jsonObject.getString("callbackUrl");

		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("callbackUrl", callbackUrl);

		return map;
	}

	public static String Base64Encode(String s) {
		return Base64.encode(s.getBytes());
	}

	public static String Base64Decode(String encoded) {
		byte[] binaryData = Base64.decode(encoded);
		return new String(binaryData);
	}

	public static boolean IsBilling(String code) {
		boolean isBilling = true;
		if ("100".equals(code))
			return false; // 不收费
		if ("1500".equals(code))
			return isBilling; // 收费
		else if ("1501".equals(code))
			return isBilling;// 收费
		else if ("1502".equals(code))
			return isBilling;// 收费
		else if ("1503".equals(code))
			return isBilling; // 先收后返
		else if ("1505".equals(code))
			return isBilling; // 收费
		else if ("1510".equals(code))
			return isBilling;// 收费
		else if ("1511".equals(code))
			return isBilling;// 收费
		else if ("1512".equals(code))
			return isBilling;// 收费
		else if ("1513".equals(code))
			return isBilling;// 收费
		else if ("1590".equals(code))
			return isBilling;// 先收后返
		else if ("1591".equals(code))
			return isBilling;// 先收后返
		else
			return false;
	}

	/**
	 * 响应代码表
	 * 
	 * @param code
	 * @return
	 */
	public static String getErrorDesc(String code) {
		if ("100".equals(code) || "0".equals(code))
			return "请求应答成功";
		else if ("9100".equals(code))
			return "余额不足";
		else if ("9110".equals(code))
			return "无模块操作权限";
		else if ("9200".equals(code))
			return "账户不存在";
		else if ("9201".equals(code))
			return "密码不正确";
		else if ("9202".equals(code))
			return "无权限查询(IP 限制)";
		else if ("9300".equals(code))
			return "无效 token";
		else if ("9301".equals(code))
			return "请求 token 太频繁";
		else if ("1000".equals(code))
			return "请求格式错误";
		else if ("1100".equals(code))
			return "结果一致";// 收费
		else if ("1400".equals(code))
			return "查得评分";// 收费
		else if ("1402".equals(code))
			return "身份证不存在";// 收费
		else if ("1405".equals(code))
			return "查无评分";// 收费
		else if ("1490".equals(code))
			return "评分接口错误";// 先收后返

		else if ("1500".equals(code))
			return "认证通过"; // 收费
		else if ("1501".equals(code))
			return "认证未通过";// 收费
		else if ("1502".equals(code))
			return "号码状态有误";// 收费
		else if ("1503".equals(code))
			return "查无数据"; // 先收后返
		else if ("1505".equals(code))
			return "查得数据";// 收费
		else if ("1510".equals(code))
			return "在网状态正常";// 收费
		else if ("1511".equals(code))
			return "在网状态停机";// 收费
		else if ("1512".equals(code))
			return "在网但不可用";// 收费
		else if ("1513".equals(code))
			return "销号/未启用";// 收费
		else if ("1590".equals(code))
			return "认证接口错误";// 先收后返
		else if ("1591".equals(code))
			return "不支持查询";// 先收后返
		else if ("1902".equals(code))
			return "无效请求MapKey";
		else if ("1903".equals(code))
			return "无效身份证号码";
		else if ("1904".equals(code))
			return " 姓名不正确";
		else if ("1905".equals(code))
			return "无效相片";
		else if ("1906".equals(code))
			return "业务类型不正确 ";
		else if ("1907".equals(code))
			return " 无效发生地";
		else if ("1910".equals(code))
			return "无效取值";
		else if ("1913".equals(code))
			return "无效手机号码";
		else if ("1920".equals(code))
			return " 银行账号不正确";
		else
			return null;
	}

	public static String getParamsError() {
		JSONObject ret = new JSONObject();

		ret.put("interface", "");
		ret.put("code", "S0001");
		ret.put("desc", "参数错误");
		ret.put("isbilling", "0");
		return ret.toString();
	}

	public static String getParamsError2() {
		JSONObject ret = new JSONObject();

		ret.put("interface", "");
		ret.put("code", "S0005");
		ret.put("desc", "请与相关人员联系");
		ret.put("isbilling", "0");
		return ret.toString();
	}

	public static String getParamsError3() {
		JSONObject ret = new JSONObject();

		ret.put("interface", "");
		ret.put("code", "B0003");
		ret.put("desc", "未知");
		ret.put("isbilling", "1");
		return ret.toString();
	}

	public static String getParamsError4() {
		JSONObject ret = new JSONObject();

		ret.put("interface", "");
		ret.put("code", "S0008");
		ret.put("desc", "过于频繁，请稍候再试");
		ret.put("isbilling", "0");
		return ret.toString();
	}
}
