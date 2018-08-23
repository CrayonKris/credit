package com.bonc.credit.service.tianyizhengxin;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.MD5Builder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TianyiHelper {

    //测试URL
    //秘钥地址
    private static final String crediturl = "https://apitest.tycredit.com/credit-front-http/credit/";
    //业务地址
    private static final String unifiedurl = "https://apitest.tycredit.com/credit-front-http/unified/";
    //获取的秘钥
    private static final String transKey = "44bb4249967beea6";
    //机构代码
    private static final String reqsys = "dongfangguoxin001";

    public static String getResponse(String method,JSONObject param){

        String newurl = "";
        if ("apply-for-private-key.json".equals(method)){
            newurl =crediturl;
        } else {
            newurl =unifiedurl+method;
        }

        String reqTransID = getReqId();
        String authorizationCode = String.valueOf(UUID.randomUUID()).replaceAll("-","");
        Map bodyMap = param;
        String response2 = CreditApplication.invoke(newurl,1,reqsys,reqTransID,authorizationCode,bodyMap,transKey);
        return response2;
    }

    //获取业务请求的消息签名
    private static String getmac(JSONObject credit) {
        String mac = MD5Builder.md5(credit.getJSONObject("header")+","+credit.getJSONObject("body"));
        return mac;
    }

    //拼接获取秘钥请求报文
    //44bb4249967beea6
    public static String getPrivateKey (){
        Map bodyMap = new HashMap();
        bodyMap.put("originalTransKey","SDGr4234gG465356gGT44");
        bodyMap.put("originalGenTime","20140531124520");
        bodyMap.put("validPeriod","0");
        bodyMap.put("keyType","0");
        String privateKey = CreditApplication.privateKeyInvoke(
                "https://apitest.tycredit.com/credit-front-http/credit/apply-for-private-key.json",
                1,//测试标识testFlag,测试环境传1或5，生产环境传0
                "dongfangguoxin001",//机构代码"
                TianyiHelper.getReqId(),   //发起方交易流水号32位不重复
                String.valueOf(UUID.randomUUID()).replaceAll("-",""),//"授权号(符合字段要求即可)",
                bodyMap,
                "dd2adfd40b0e777a" //"天翼征信提供的home_key"
        );
        System.out.println(privateKey);
        return privateKey;
    }

    //获取流水号
    public static String getReqId() {
        String reqId = "BONC" + System.currentTimeMillis() + "R"
                + (int) (Math.random() * (1000 - 100) + 100);
        return reqId;
    }
}
