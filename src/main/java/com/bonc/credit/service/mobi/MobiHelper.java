package com.bonc.credit.service.mobi;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;

/**
 * 
 * @author zhijie.ma
 * @date 2017年5月9日
 *
 */
@Service
public class MobiHelper {
	private static Logger logger = Logger.getLogger(MobiHelper.class);
	public static final String account = "mobiuser";
	private static final String pwd = "mobiuser@1234";
	private static final String baseUrl = "http://223.202.2.69:8180/datamanage/";

	public static String buildSign(long timestamp, String random) {
		String str = account + pwd + timestamp + random;
		String encStr = MD5Builder.md5(str);
		if (null == encStr)
			return null;
		return encStr.toUpperCase();
	}

	public static String httpPost(String method, String param) {
		String url = baseUrl + method;

		// String result = HttpConnUtils.httpPostRequestByParams(url, params);
		logger.info("发送post请求中的路径：" + url + "参数为：" + param);

		String sendPost = HttpRequest.sendPost(url, param);

		return sendPost;
	}

	private static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	public static String md5(String encodestr) throws NoSuchAlgorithmException {
		byte[] strTemp = encodestr.getBytes();
		MessageDigest mdTemp = MessageDigest.getInstance("MD5");
		mdTemp.update(strTemp);
		byte[] md = mdTemp.digest();
		int j = md.length;
		char[] str = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[(k++)] = hexDigits[(byte0 >>> 4 & 0xF)];
			str[(k++)] = hexDigits[(byte0 & 0xF)];
		}
		return new String(str);
	}
}
