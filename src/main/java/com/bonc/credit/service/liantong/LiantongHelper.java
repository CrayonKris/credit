package com.bonc.credit.service.liantong;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class LiantongHelper {
    private static Logger logger = Logger.getLogger(LiantongHelper.class);
    private static String url = "http://120.52.23.245:8080/crp_test/inter/";
    private static String account = "bonc_test";
    private static String appkey = "6ef470B57!k4";

    /**
     * 发送HTTP POST请求
     * @param map
     * @return
     */
    public static String getRes(String method, Map<String,String> map){

        String mobile = map.get("sendTelNo").toString();
        String sequence=getSequence();
        map.put("sequence",sequence);
        map.put("orgCode",account);
        String time=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        map.put("curTime",time);
        map.put("orgSeq",getorgSeq(mobile,time,sequence));
        StringBuffer sBuffer=new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sBuffer.append(entry.getKey()+"="+entry.getValue()+"&");
        }
        String param = StringUtils.substring(sBuffer.toString(), 0, sBuffer.toString().length()-1);
        System.out.println(param);
        String newurl=url+method;
        logger.info("url>>>"+newurl+"，param参数>>>"+param);
        String response = HttpRequest.sendPost(newurl,param);
        return response;
    }

    /**
     * 获取序列号
     * @return
     */
    public static String getSequence(){
        return  UUID.randomUUID().toString().substring(0,8);
    }

    /**
     *  获取机构秘钥序列
     * @param mobile
     * @return
     */
    public static String getorgSeq(String mobile,String time,String sequence){
        String orgseq = MD5Builder.md5(mobile+"_"+account+"_"+appkey+"_"+time+"_"+sequence).toUpperCase();
        return orgseq;
    }

    public static void main(String arg[]){
        String method="check/userCheck.do";
        Map<String,String> map=new HashMap<>();
        map.put("sendTelNo","17863804694");
        map.put("certType","0101");
        map.put("userName","张宜轩");
        map.put("certCode","371725199611080013");
        String result=getRes(method,map);
        System.out.println(result);
    }

}
