package com.bonc.credit.service.zhongchengxin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;

public class ZhongchengxinHelper {
    private final static Logger logger = LoggerFactory.getLogger(ZhongchengxinHelper.class);

    // 测试
//    public static final String account = "dongfangguoxin_test";
//    private static final String privateKey = "950b52063117d7c8";
//    private static final String baseUrl = "https://tapi.ccxcredit.com/data-service/";

    // 正式
    public static final String account = "dongfangguoxin_api";
    private static final String privateKey = "0e48bf795b3c4470a1545787e785f4a3";
    private static final String baseUrl = "https://api.ccxcredit.com/data-service/";

    private static final String proxyHost = "172.16.18.1";
    private static final int proxyPort = 9999;


    public static void main(String[] args) {
        Map<String, String> bizMap = new HashMap<String, String>();
        bizMap.put("name", "陆远涛");
        bizMap.put("cid", "110105198008147517");
        bizMap.put("mobile", "13910982766");
        bizMap.put("account", account);
        bizMap.put("reqId", getReqId());
        String sign = buildSign(bizMap);
        bizMap.put("sign", sign);

        String s = doGet("telecom/identity/mobile", bizMap);

        System.out.println(s);
    }

    public static String doGet(String method, Map<String, String> reqMap) {
        String reqUrl = HttpRequest.getUrl(reqMap, baseUrl + method);

        logger.info(reqUrl);
        String response = HttpsHelper.httpGet(reqUrl);

        return response;
    }

    public static String doGetByProxy(String method, Map<String, String> reqMap) {
        String reqUrl = HttpRequest.getUrl(reqMap, baseUrl + method);

        logger.info(reqUrl);
        String response = HttpsHelper.httpGetByProxy(reqUrl, proxyHost, proxyPort);

        return response;
    }

    public static String getReqId() {
        String reqId = "BONC" + System.currentTimeMillis() + "R"
                + (int) (Math.random() * (1000 - 100) + 100);
        return reqId;
    }

    public static String buildSign(Map<String, String> sortMap) {
        StringBuilder sb = new StringBuilder();
        Object[] keys = sortMap.keySet().toArray();
        Arrays.sort(keys);

        for (Object key : keys) {
            sb.append(key).append(sortMap.get(key));
        }
        sb.append(privateKey);

        String data = sb.toString();//参数拼好的字符串
//		System.out.println(data);
        String sign = MD5Builder.md5(data).toUpperCase();
//		System.out.println(sign);
        return sign;
    }

}
