package com.bonc.credit.service.tianchuang;

import java.util.Arrays;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;

/**
*	天创服务类
* @author zhijie.ma
* @date 2017年7月17日
* 
*/
@Service
public class TianChuangHelper {
	
	static final String tokenId = "5f46d779-19a7-4ff9-9c3f-620267bfbb1b";
	static final String appId = "41ba0139-8b3c-4e41-bc7a-e341d9939a1e";
	static final String url = "http://api.tcredit.com";
	
	/**
	 * 获取带参接口请求路径
	 * @param tokenKey	密钥
	 * @param map	业务参数
	 * @param method	请求方法
	 * @return
	 */
	public String getUrlResult(String tokenKey,Map<String,String> map,String method){
		map.put("appId", TianChuangHelper.appId);
		map.put("tokenKey", tokenKey);
		String urlResult = HttpRequest.getUrl(map, TianChuangHelper.url+method);
		return urlResult;
	}
	
	/**
	 * 获取tokenKey
	 * @param method	访问的方法
	 * @param tokenId	
	 * @param map	业务参数
	 * @return
	 */
	public String getTokenKey(String method,Map<String,String> map){
		
		StringBuffer sb = new StringBuffer();
		String urlResult = url + method;
		
		sb.append(urlResult).append(tokenId);
		
		Object[] keys = map.keySet().toArray();
		Arrays.sort(keys);
		
		for (Object key : keys) {
			sb.append(key).append("=").append(map.get(key)).append(",");
		}
		
		return MD5Builder.md5(sb.substring(0, sb.length()-1).toString());
	}
	
	/**
	 * 天创接口返回码表
	 * @param code
	 * @return
	 */
	public static String getErrorCode(String code){
		if(code.equals("0")) return "成功";
		if(code.equals("1")) return "系统错误";
		if(code.equals("2")) return "参数错误";
		if(code.equals("3")) return "数据异常，未能获取结果";
		if(code.equals("4")) return "用户不存在或配置错误";
		if(code.equals("5")) return "TokenKey验证失败";
		if(code.equals("6")) return "接口未定义或停止使用";
		if(code.equals("7")) return "用户没有接口使用权限";
		if(code.equals("8")) return "用户接口暂停使用";
		if(code.equals("9")) return "用户余额不足";
		if(code.equals("10")) return "系统繁忙，请稍后再试";
		else return "超越码表范围";
	}
	
	/*public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		Map<String,String> map = new HashMap<String,String>();
		map.put("1111", "aaaa");
		map.put("2222", "aada");
		map.put("3333", "aada");
		map.put("4444", "aada");
		String tokenKey = getTokenKey("verifyMobileInfo3", map);
		System.out.println(tokenKey);
	}*/
	
}
