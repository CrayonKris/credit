package com.bonc.credit.service.zhongchengxin;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.ProjectErrorInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhangyixuan
 * @Date: 2018/8/3 18:14
 */
@Service
public class ZhongchengxinServicePart1ByZyx {

    private final static Logger logger = LoggerFactory.getLogger(ZhongchengxinServicePart1.class);

    /**
     * 手机号码-证件号码-姓名核验简版
     *
     * @param bizParams
     * @return
     */
    public String getVerifyMobileInfo(JSONObject bizParams) {
        String title = "手机号码-证件号码-姓名核验(简版)";
        String mobile = bizParams.getString("mobile");
        String userName = bizParams.getString("userName");
        String name = "";
        String cid = bizParams.getString("idCard");

        if (null == mobile || "".equals(mobile) || null == userName || "".equals(userName)) {
            return ProjectErrorInformation.businessError5(title);
        }
        try {
            name = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }


        Map<String, String> reqMap = new HashMap<String, String>();
        reqMap.put("name", name);
        reqMap.put("cid", cid);
        reqMap.put("mobile", mobile);
        reqMap.put("account", ZhongchengxinHelper.account);
        reqMap.put("reqId", ZhongchengxinHelper.getReqId());

        String sign = ZhongchengxinHelper.buildSign(reqMap);
        reqMap.put("sign", sign);
        String response = ZhongchengxinHelper.doGet("/telecom/identity/3mo/t1/noca", reqMap);
//        String response = ZhongchengxinHelper.doGetByProxy("/telecom/identity/3mo/t1/noca", reqMap);
//        String response = "{\"resCode\":\"2060\",\"resMsg\":\"匹配成功\",\"tid\":\"3C21301615343215872821202\",\"sign\":\"61B4D30FE6A37CFC78EE6DD96DFCC292\",\"reqId\":\"BONC1534321512848R663\"}";
        logger.info(mobile + " response >> " + response);

        if (response == null || response.equals("")) {
            JSONObject ret1 = new JSONObject();
            ret1.put("interface", title);
            ret1.put("code", "B0001");
            ret1.put("desc", "调用失败");
            ret1.put("isbilling", "0");
            return ret1.toString();
        }

        String retCode = "B0001";
        String retDesc = "调用失败";
        String isbilling = "1";

        JSONObject result = JSONObject.parseObject(response);
        String status = result.getString("resCode");
        String resMsg = result.getString("resMsg");
        String tid = result.getString("tid"); // 运营商流水号
        String reqId = result.getString("reqId");// 平台流水号

        logger.info(mobile + " 请求结果 >> status:" + status + " | resMsg:" + resMsg + " | pid:" + tid + " | sid:" + reqId);

        if ("1001".equals(status) || "1002".equals(status) || "1003".equals(status) || "1011".equals(status)
                || "1013".equals(status)) {
            JSONObject ret = new JSONObject();
            ret.put("interface", title);
            ret.put("code", "B0001");
            ret.put("desc", "调用失败");
            ret.put("isbilling", "0");

            return ret.toString();
        } else if ("1012".equals(status)) {
            JSONObject ret = new JSONObject();
            ret.put("interface", title);
            ret.put("code", "B0005");
            ret.put("desc", "参数错误");
            ret.put("isbilling", "0");
            return ret.toString();
        } else if ("9999".equals(status)) {
            JSONObject ret = new JSONObject();
            ret.put("interface", title);
            ret.put("code", "B0004");
            ret.put("desc", "其他错误");
            ret.put("isbilling", "0");
            return ret.toString();
        } else {
            if ("2060".equals(status)) {
                retCode = "0";
                retDesc = "验证一致";
                isbilling = "1"; // 收费
            } else if ("2062".equals(status)) {
                retCode = "1";
                retDesc = "号码不存在";
                isbilling = "1";
            } else if ("2061".equals(status)) {
                retCode = "2";
                retDesc = "验证不一致";
                isbilling = "1";
            }  else if("2063".equals(status)){
                retCode = "3";
                retDesc = "手机号一致，无身份证或姓名信息";
                isbilling = "1";
            } else{
                retCode = "B0004";
                retDesc = "其他错误";
                isbilling = "0";
            }
        }

        JSONObject ret = new JSONObject();
        ret.put("interface", title);
        ret.put("code", retCode);
        ret.put("desc", retDesc);
        ret.put("isbilling", isbilling);

        return ret.toString();
    }

    /**
     * 身份证姓名验证
     * @param bizParams
     * @return
     */
    public String getCidNameInfo(JSONObject bizParams){
        String title = "证件号码-姓名核验";
        String idCard = bizParams.getString("idCard");
        String userName = bizParams.getString("userName");
        String name = "";
        if (null == idCard || "".equals(idCard) || null == userName || "".equals(userName)) {
            return ProjectErrorInformation.businessError5(title);
        }
        try {
            name = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }


        Map<String, String> reqMap = new HashMap<String, String>();
        reqMap.put("name", name);
        reqMap.put("cid", idCard);
        reqMap.put("account", ZhongchengxinHelper.account);
//        reqMap.put("reqId", ZhongchengxinHelper.getReqId());

        String sign = ZhongchengxinHelper.buildSign(reqMap);
        reqMap.put("sign", sign);
        String response = ZhongchengxinHelper.doGet("identity/auth", reqMap);
//        String response = ZhongchengxinHelper.doGetByProxy("identity/auth", reqMap);
//        String response = "{\"resCode\":\"2060\",\"resMsg\":\"匹配成功\",\"tid\":\"3C21301615343215872821202\",\"sign\":\"61B4D30FE6A37CFC78EE6DD96DFCC292\",\"reqId\":\"BONC1534321512848R663\"}";
        logger.info(" response >> " + response);

        if (response == null || response.equals("")) {
            JSONObject ret1 = new JSONObject();
            ret1.put("interface", title);
            ret1.put("code", "B0001");
            ret1.put("desc", "调用失败");
            ret1.put("isbilling", "0");
            return ret1.toString();
        }

        String retCode = "B0001";
        String retDesc = "调用失败";
        String isbilling = "1";

        JSONObject result = JSONObject.parseObject(response);
        String status = result.getString("resCode");
        String resMsg = result.getString("resMsg");
        String tid = result.getString("tid"); // 运营商流水号
//        String reqId = result.getString("reqId");// 平台流水号

        logger.info( " 请求结果 >> status:" + status + " | resMsg:" + resMsg + " | pid:" + tid);

        if ("1001".equals(status) || "1002".equals(status) || "1003".equals(status) || "1011".equals(status)
                || "1013".equals(status)) {
            JSONObject ret = new JSONObject();
            ret.put("interface", title);
            ret.put("code", "B0001");
            ret.put("desc", "调用失败");
            ret.put("isbilling", "0");

            return ret.toString();
        } else if ("1012".equals(status)) {
            JSONObject ret = new JSONObject();
            ret.put("interface", title);
            ret.put("code", "B0005");
            ret.put("desc", "参数错误");
            ret.put("isbilling", "0");
            return ret.toString();
        } else if ("9999".equals(status)) {
            JSONObject ret = new JSONObject();
            ret.put("interface", title);
            ret.put("code", "B0004");
            ret.put("desc", "其他错误");
            ret.put("isbilling", "0");
            return ret.toString();
        } else {
            if ("2010".equals(status)) {
                retCode = "0";
                retDesc = "验证一致";
                isbilling = "1"; // 收费
            } else if ("2011".equals(status)) {
                retCode = "1";
                retDesc = "验证不一致";
                isbilling = "1";
            } else if ("2012".equals(status)) {
                retCode = "2";
                retDesc = "库无记录";
                isbilling = "1";
            }  else if("2013".equals(status)){
                retCode = "3";
                retDesc = "输入信息不符合要求";
                isbilling = "1";
            } else{
                retCode = "B0004";
                retDesc = "其他错误";
                isbilling = "0";
            }
        }

        JSONObject ret = new JSONObject();
        ret.put("interface", title);
        ret.put("code", retCode);
        ret.put("desc", retDesc);
        ret.put("isbilling", isbilling);

        return ret.toString();
    }



}
