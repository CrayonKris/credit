package com.bonc.credit.service.zhongchengxin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.service.CreditService;
import com.bonc.util.ProjectErrorInformation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 中诚信-移动
 * <p>
 * 2060和2061收费，其他代码不收费
 *
 * @author jiwen.shang
 * @date 2016.10.31
 */
@Service
public class ZhongchengxinServicePart1 {
    private final static Logger logger = LoggerFactory.getLogger(ZhongchengxinServicePart1.class);

    @Autowired
    CreditService service;

    /**
     * 手机号码-证件类型-证件号码-姓名核验（本接口仅支持移动）
     *
     * @param bizParams
     * @return
     */
    public String getRealnameVer(JSONObject bizParams) {
        String title = "手机号码-证件类型-证件号码-姓名核验";
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

        String idType = bizParams.containsKey("idType") ? bizParams.get("idType").toString() : "";
        String cidType = "1";
        if (null == idType || "".equals(idType) || "idCard".equals(idType))
            cidType = "1"; // 身份证
        else if ("passport".equals(idType))
            cidType = "3"; // 护照

        Map<String, String> reqMap = new HashMap<String, String>();
        reqMap.put("name", name);
        reqMap.put("cid", cid);
        reqMap.put("mobile", mobile);
        reqMap.put("account", ZhongchengxinHelper.account);
        reqMap.put("reqId", ZhongchengxinHelper.getReqId());

        String sign = ZhongchengxinHelper.buildSign(reqMap);
        reqMap.put("sign", sign);
        // 证件类型不签名
        reqMap.put("cidType", cidType);

        String response = ZhongchengxinHelper.doGet("telecom/identity/mobile", reqMap);
//        String response = ZhongchengxinHelper.doGetByProxy("telecom/identity/mobile", reqMap);
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
            ret.put("code", "-1");
            ret.put("desc", "调用失败");
            ret.put("isbilling", "0");

            return ret.toString();
        } else if ("1012".equals(status)) {
            JSONObject ret = new JSONObject();
            ret.put("interface", title);
            ret.put("code", "S0001");
            ret.put("desc", "参数错误");
            ret.put("isbilling", "0");
            return ret.toString();
        } else if ("9999".equals(status)) {
            JSONObject ret = new JSONObject();
            ret.put("interface", title);
            ret.put("code", "B0003");
            ret.put("desc", "未知");
            ret.put("isbilling", "0");
            return ret.toString();
        } else {
            if ("2060".equals(status)) {
                retCode = "0";
                retDesc = "验证一致";
                isbilling = "1"; // 收费
            } else if ("2061".equals(status)) {
                retCode = "1";
                retDesc = "验证不一致";
                isbilling = "1";// 收费
            } else if ("2062".equals(status)) {
                retCode = "2";
                retDesc = "无数据";
                isbilling = "0";
            } else if ("2063".equals(status)) {
                retCode = "2";
                retDesc = "无数据";
                isbilling = "0";
            } else {
                retCode = "B0003";
                retDesc = "未知";
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
     * 手机号码-证件号码-姓名核验
     *
     * @param bizParams
     * @return
     */
    public String getVerifyMobileInfo(JSONObject bizParams) {
        String title = "手机号码-证件号码-姓名核验";
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

        String response = ZhongchengxinHelper.doGet("telecom/identityverification", reqMap);
//        String response = ZhongchengxinHelper.doGetByProxy("telecom/identityverification", reqMap);
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
            ret.put("code", "-1");
            ret.put("desc", "调用失败");
            ret.put("isbilling", "0");

            return ret.toString();
        } else if ("1012".equals(status)) {
            JSONObject ret = new JSONObject();
            ret.put("interface", title);
            ret.put("code", "S0001");
            ret.put("desc", "参数错误");
            ret.put("isbilling", "0");
            return ret.toString();
        } else if ("9999".equals(status)) {
            JSONObject ret = new JSONObject();
            ret.put("interface", title);
            ret.put("code", "B0003");
            ret.put("desc", "未知");
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
            } else if ("2064".equals(status)) {
                retCode = "2";
                retDesc = "手机号与身份证不匹配";
                isbilling = "0";
            } else if ("2065".equals(status)) {
                retCode = "3";
                retDesc = "手机号与姓名不匹配";
                isbilling = "0";
            } else if ("2066".equals(status)) {
                retCode = "4";
                retDesc = "不支持该运营商";
                isbilling = "0";
            } else {
                retCode = "B0003";
                retDesc = "未知";
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
     * 姓名，身份证号码，银行卡号验证对应关系是否一致.
     *
     * @param bizParams 需要传入userName、idCard、bankCardNum三个参数
     * @return
     */
    public String getVerifyVerificationInfo(JSONObject bizParams) {
        String title = "姓名-身份证号码-银行卡号核验";

        // 姓名
        String userName = bizParams.getString("userName");
        String name = "";
        // 身份证号码，字母 X 大写
        String cid = bizParams.getString("idCard");
        // 银行卡号
        String card = bizParams.getString("bankCardNum");

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(card) || StringUtils.isEmpty(cid)) {
            return ProjectErrorInformation.businessError5(title);
        }
        try {
            name = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }

        Map<String, String> req = new HashMap<>();
        req.put("name", name);
        req.put("cid", cid);
        req.put("card", card);
        req.put("account", ZhongchengxinHelper.account);
        req.put("reqTid", ZhongchengxinHelper.getReqId());

        String sign = ZhongchengxinHelper.buildSign(req);
        req.put("sign", sign);
        String response = ZhongchengxinHelper.doGet("auth/cnc/t2", req);
//        String response = ZhongchengxinHelper.doGetByProxy("auth/cnc/t2", req);
        logger.info(name + " response >> " + response);

        String retCode = "B0001";
        String retDesc = "调用失败";
        if (response == null || "".equals(response)) {
            return paramsError(title, retCode, retDesc);
        }

        JSONObject result = JSONObject.parseObject(response);
        String status = result.getString("resCode");
        String resMsg = result.getString("resMsg");
        String tid = result.getString("tid"); // 运营商流水号
        String reqId = result.getString("reqTid");// 平台流水号

        logger.info(userName + " 请求结果 >> status:" + status + " | resMsg:" + resMsg + " | pid:" + tid + " | sid:" + reqId);

        JSONObject ret = new JSONObject();
        ret.put("interface", title);
        switch (status) {
            case "1001":
            case "1002":
            case "1003":
            case "1011":
            case "1013": {
                ret.put("code", "-1");
                ret.put("desc", retDesc);
                ret.put("isbilling", "0");
                break;
            }
            case "1012": {
                ret.put("code", "B0005");
                ret.put("desc", "参数错误");
                ret.put("isbilling", "0");
                break;
            }
            case "2030": {
                ret.put("code", "0");
                ret.put("desc", "验证一致");
                ret.put("isbilling", "1");
                break;
            }
            case "2031": {
                ret.put("code", "1");
                ret.put("desc", "验证不一致");
                ret.put("isbilling", "1");
                break;
            }
            case "2039": {
                ret.put("code", "-2");
                ret.put("desc", "不作验证");
                ret.put("isbilling", "1");
                break;
            }
            // 9999
            default: {
                ret.put("code", "B0004");
                ret.put("desc", "其他错误");
                ret.put("isbilling", "0");
            }
        }
        return ret.toString();
    }

    /**
     * 姓名，身份证号码，银行卡号，手机号验证是否一致
     *
     * @param bizParams 需要传入userName、idCard、bankCard、mobile
     * @return
     */
    public String getVerifyVerificationInfoIV(JSONObject bizParams) {
        String title = "姓名-身份证号码-银行卡号-手机号核验";

        // 姓名
        String userName = bizParams.getString("userName");
        String name = "";
        // 身份证号码，字母 X 大写
        String cid = bizParams.getString("idCard");
        // 银行卡号
        String card = bizParams.getString("bankCardNum");
        String mobile = bizParams.getString("mobile");

        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(cid) || StringUtils.isEmpty(card) || StringUtils.isEmpty(mobile)) {
            return ProjectErrorInformation.businessError5(title);
        }
        try {
            name = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }

        Map<String, String> req = new HashMap<>();
        req.put("name", name);
        req.put("cid", cid);
        req.put("card", card);
        req.put("mobile", mobile);
        req.put("account", ZhongchengxinHelper.account);
        req.put("reqTid", ZhongchengxinHelper.getReqId());

        String sign = ZhongchengxinHelper.buildSign(req);
        req.put("sign", sign);
        String response = ZhongchengxinHelper.doGet("auth/cncm/t2", req);
//        String response = ZhongchengxinHelper.doGetByProxy("auth/cncm/t2", req);
        logger.info(name + " response >> " + response);

        String retCode = "B0001";
        String retDesc = "调用失败";
        if (response == null || "".equals(response)) {
            return paramsError(title, retCode, retDesc);
        }

        JSONObject result = JSONObject.parseObject(response);
        String status = result.getString("resCode");
        String resMsg = result.getString("resMsg");
        String tid = result.getString("tid"); // 运营商流水号
        String reqId = result.getString("reqId");// 平台流水号

        logger.info(userName + " 请求结果 >> status:" + status + " | resMsg:" + resMsg + " | pid:" + tid + " | sid:" + reqId);

        JSONObject ret = new JSONObject();
        ret.put("interface", title);
        switch (status) {
            case "1001":
            case "1002":
            case "1003":
            case "1011":
            case "1013": {
                ret.put("code", "-1");
                ret.put("desc", retDesc);
                ret.put("isbilling", "0");
                break;
            }
            case "1012": {
                ret.put("code", "B0005");
                ret.put("desc", "参数错误");
                ret.put("isbilling", "0");
                break;
            }
            case "2030": {
                ret.put("code", "0");
                ret.put("desc", "验证成功");
                ret.put("isbilling", "1");
                break;
            }
            case "2031": {
                ret.put("code", "1");
                ret.put("desc", "验证失败");
                ret.put("isbilling", "0");
                break;
            }
            case "2039": {
                ret.put("code", "-2");
                ret.put("desc", "不作验证");
                ret.put("isbilling", "0");
                break;
            }
            // 9999
            default: {
                ret.put("code", "B0004");
                ret.put("desc", "其他错误");
                ret.put("isbilling", "0");
            }
        }
        return ret.toString();
    }

    /**
     * 姓名-身份证号-手机号码核验
     *
     * @param bizParams 需要传入userName、idCard、mobile三个参数
     * @return
     */
    public String getVerifyVerificationInfoIII(JSONObject bizParams) {
        String title = "运营商三要素核验详版";
        // 姓名
        String userName = bizParams.getString("userName");
        String name = "";
        String method1 = "telecom/idencheck/";
        String method2 = "telecom/idencheck/noca";
        String method = "";

        // 身份证号码，字母 X 大写
        String cid = bizParams.getString("idCard");
        // 手机号码
        String mobile = bizParams.getString("mobile");
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(cid) || StringUtils.isEmpty(mobile)) {
            return ProjectErrorInformation.businessError5(title);
        }
        try {
            name = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }

        //判断运营商
        String oprator = service.getPhoneProvider(mobile);
        if ("YD".equals(oprator)){
            method = method2;
        } else {
            method = method1;
        }
        Map<String, String> req = new HashMap<>();
        req.put("name", name);
        req.put("cid", cid);
        req.put("mobile", mobile);
        req.put("account", ZhongchengxinHelper.account);
        // 获取 reqID
        req.put("reqTid", ZhongchengxinHelper.getReqId());
        // 获取sign
        String sign = ZhongchengxinHelper.buildSign(req);
        req.put("sign", sign);
        // 向上游发起请求获取响应结果
        String response = ZhongchengxinHelper.doGet(method, req);
//        String response = "{\"resCode\":\"2060\",\"resMsg\":\"匹配成功\",\"operator\":3,\"tid\":\"3C22301115341521143496259\",\"reqTid\":\"BONC1534152040501R515\",\"sign\":\"61B4D30FE6A37CFC78EE6DD96DFCC292\"}";
//        String response = ZhongchengxinHelper.doGetByProxy(method, req);
        logger.info(name + " response >> " + response);

        String retCode = "B0001";
        String retDesc = "调用失败";
        if (StringUtils.isEmpty(response)) {
            return paramsError(title, retCode, retDesc);
        }

        JSONObject result = JSONObject.parseObject(response);
        String status = result.getString("resCode");
        String resMsg = result.getString("resMsg");
        String tid = result.getString("tid"); // 运营商流水号
        String reqId = result.getString("reqId");// 平台流水号

        String operator = result.getString("operator");


        logger.info(userName + " 请求结果 >> status:" + status + " | resMsg:" + resMsg + " | pid:" + tid + " | sid:" + reqId);

        JSONObject ret = new JSONObject();
        ret.put("interface", title);

        if ("1".equals(operator)){
            operator = "电信";
        } else if ("2".equals(operator)){
            operator = "联通";
        } else if ("3".equals(operator)){
            operator = "移动";
        } else {
            operator = "未知";
        }
        switch (status) {
            case "1001":
            case "1002":
            case "1003":
            case "1011":
            case "1013": {
                ret.put("code", "-1");
                ret.put("desc", retDesc);
                ret.put("isbilling", "0");
                break;
            }
            case "1012": {
                ret.put("code", "B0005");
                ret.put("desc", "参数错误");
                ret.put("isbilling", "0");
                break;
            }
            case "2060": {
                ret.put("code", "0");
                ret.put("desc", "匹配成功，"+operator);
                ret.put("isbilling", "1");
                break;
            }
            case "2062": {
                ret.put("code", "1");
                ret.put("desc", "号码不存在，"+operator);
                ret.put("isbilling", "1");
                break;
            }
            case "2063": {
                ret.put("code", "2");
                ret.put("desc", "无姓名或身份证信息，"+operator);
                ret.put("isbilling", "1");
                break;
            }
            case "2067": {
                ret.put("code", "3");
                ret.put("desc", "手机号和姓名一致，身份证号不一致，"+operator);
                ret.put("isbilling", "1");
                break;
            }
            case "2068": {
                ret.put("code", "4");
                ret.put("desc", "手机号和身份证号一致，姓名不一致，"+operator);
                ret.put("isbilling", "1");
                break;
            }
            case "2069": {
                ret.put("code", "5");
                ret.put("desc", "手机号一致，姓名和身份证号不一致，"+operator);
                ret.put("isbilling", "1");
                break;
            }
            case "2073": {
                ret.put("code", "-2");
                ret.put("desc", "其他不一致，"+operator);
                ret.put("isbilling", "1");
                break;
            }
            // 9999
            default: {
                ret.put("code", "B0004");
                ret.put("desc", "未知");
                ret.put("isbilling", "0");
            }
        }
        return ret.toString();
    }

    private String paramsError(String title, String retCode, String retDesc) {
        JSONObject ret = new JSONObject();
        ret.put("interface", title);
        ret.put("code", retCode);
        ret.put("desc", retDesc);
        ret.put("isbilling", "0");
        return ret.toString();
    }

    public String getUserState(JSONObject bizParams) {
        String title = "运营商在网状态";
        String method = "telecom/state3";
        String idcard = bizParams.getString("idCard");
        String mobile = bizParams.getString("mobile");
        String username = bizParams.getString("userName");
        String name = "";

        if (StringUtils.isEmpty(username)||StringUtils.isEmpty(idcard)||StringUtils.isEmpty(mobile)){
            return ProjectErrorInformation.businessError5(title);
        }
        try {
            name = URLDecoder.decode(username, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }

        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("account", ZhongchengxinHelper.account);
        //客户端产生的流水号
        map.put("reqId", ZhongchengxinHelper.getReqId());

        String sign = ZhongchengxinHelper.buildSign(map);
        map.put("sign", sign);
        map.put("name", name);
        map.put("cid", idcard);

        String result = ZhongchengxinHelper.doGet(method, map);
//        String result = ZhongchengxinHelper.doGetByProxy(method, map);
        logger.info("result>> " + result);
        if (StringUtils.isEmpty(result)) {
            return ProjectErrorInformation.businessError1(title);
        }
        JSONObject json = JSONObject.parseObject(result);
        String code = json.getString("resCode");
        String msg = json.getString("resMsg");
        String tid = json.getString("tid"); //服务端生成流水号
        String reqid = json.getString("reqId"); //客户端请求流水号
        String sign2 = json.getString("sign");

        logger.info("调用结果：>>"+code+"| "+msg+"| "+tid+"| "+reqid+"| "+sign2 );
        JSONObject res = new JSONObject();

        String retCode = "";
        JSONObject retDesc = new JSONObject();
        String isBilling = "0";

        if ("0000".equals(code)) { //处理成功
            retCode = "1";
            JSONObject jsondata = (JSONObject) json.get("data");
            String opretor = jsondata.getString("mobileOperator");
            String state = jsondata.getString("mobileState");
            String cancelTime = jsondata.getString("cancelTime");

            switch (opretor) {
                case "1":
                    opretor = "联通";
                    break;
                case "2":
                    opretor = "电信";
                    break;
                case "3":
                    opretor = "移动";
                    break;
            }
            switch (state) {
                case "0":
                    state = "正常使用";
                    retCode += "0";
                    break;
                case "1":
                    state = "停机";
                    retCode += "1";
                    break;
                case "2":
                    state = "销号";
                    retCode += "2";
                    break;
                case "3":
                    state = "预销号";
                    retCode += "3";
                    break;
                case "4":
                    state = "未启用";
                    retCode += "4";
                    break;
                case "-1":
                    state = "查无结果";
                    retCode = "-1";
                    break;
            }
            if ("2".equals(opretor)) {
                switch (cancelTime) {
                    case "1":
                        cancelTime = "【1-30】天";
                        break;
                    case "2":
                        cancelTime = "【31-90】天";
                        break;
                    case "3":
                        cancelTime = "【>90】天";
                        break;
                }
            }
            isBilling = "1";

            retDesc.put("opretor",opretor);
            retDesc.put("state",state);
            retDesc.put("cancelTime",cancelTime);

        }else if ("1012".equals(code)){//参数为空或格式错误
            return ProjectErrorInformation.businessError5(title);
        }else if ("9999".equals(code)){//系统错误
            return ProjectErrorInformation.businessError4(title);
        }else if ("2062".equals(code)){//无效手机号
            return ProjectErrorInformation.businessError6(title);
        }else {
            return ProjectErrorInformation.businessError1(title);
        }

        res.put("interface",title);
        res.put("code",retCode);
        res.put("desc",retDesc);
        res.put("isbilling",isBilling);
        return res.toString();
    }

    public String getUserTime(JSONObject bizParams) {
        String title = "运营商在网时长";
        String userName = bizParams.getString("userName");
        String mobile = bizParams.getString("mobile");
        String cid = bizParams.getString("idCard");


        if (StringUtils.isEmpty(userName)||StringUtils.isEmpty(mobile)||StringUtils.isEmpty(cid)){
            return ProjectErrorInformation.businessError5(title);
        }
        String name = "";
        try {
            name = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }
        Map<String,String> map = new HashMap<>();
        map.put("name",name);
        map.put("mobile",mobile);
        map.put("cid",cid);
        map.put("account",ZhongchengxinHelper.account);
        map.put("reqId",ZhongchengxinHelper.getReqId());

        String sign = ZhongchengxinHelper.buildSign(map);
        logger.info("签名sign：" + sign);

        map.put("sign",sign);
        String result = ZhongchengxinHelper.doGet("telecom/time",map);
//        String result = ZhongchengxinHelper.doGetByProxy("telecom/time",map);
        if (StringUtils.isEmpty(result)){
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info("response>>> "+result);
        JSONObject jsonResult = JSON.parseObject(result);
        String resCode = jsonResult.getString("resCode");
        String resMsg = jsonResult.getString("resMsg");
        String tid = jsonResult.getString("tid"); //服务端生成流水号
        String reqid = jsonResult.getString("reqId"); //客户端请求流水号
        String sign2 = jsonResult.getString("sign");

        logger.info("调用结果：>> resCode："+resCode+"| "+resMsg+"| tid："+tid+"| "+reqid+"| "+sign2);


        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        if ("0000".equals(resCode)){

            String userTime = jsonResult.getJSONObject("data").getString("inUseTime");
            switch (userTime) {
                case "0":
                    code = "A";
                    desc = "在网 [0,3)个月";
                    break;
                case "3":
                    code = "B";
                    desc = "在网 [3,6)个月";
                    break;
                case "6":
                    code = "C";
                    desc = "在网 [6,12)个月";
                    break;
                case "12":
                    code = "D";
                    desc = "在网 [12,24)个月";
                    break;
                case "24":
                    code = "E";
                    desc = "在网 [24,+)个月";
                    break;
            }
            isbilling = "1";
        }else if("2001".equals(resCode)){
            code = "-1";
            desc = "没有查询到结果";
            isbilling = "1";
        }else if ("1012".equals(resCode)){//参数为空或格式错误
            return ProjectErrorInformation.businessError5(title);
        }else if ("9999".equals(resCode)) {//系统错误
            return ProjectErrorInformation.businessError4(title);
        }else {
            return ProjectErrorInformation.businessError1(title);
        }
        JSONObject json = new JSONObject();
        json.put("interface",title);
        json.put("code",code);
        json.put("desc",desc);
        json.put("isbilling",isbilling);
        return json.toString();
    }

    public String getVerifyMobileInfoII(JSONObject bizParams) {
        String title = "手机号码-证件号码-姓名核验(简版2.0)";
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
        String response = ZhongchengxinHelper.doGet("/telecom/identity/3mo/t1", reqMap);
//        String response = ZhongchengxinHelper.doGetByProxy("/telecom/identity/3mo/t1", reqMap);
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
}
