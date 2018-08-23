package com.bonc.credit.service.shuzun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.service.CreditService;
import com.bonc.credit.service.zhongchengxin.ZhongchengxinHelper;
import com.bonc.util.HttpRequest;
import com.bonc.util.ProjectErrorInformation;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhijie.ma
 * @date 2018年6月22日
 */
@Service
public class ShuZunServiceImpl extends ShuZunHelper implements ShuZunService {

    private static final Logger logger = Logger.getLogger(ShuZunServiceImpl.class);

    @Autowired
    CreditService service;
    @Override
    public String getVerifyMobileInfo(JSONObject params) {

        String title = "姓名-身份证号-手机号码验证";
        JSONObject jsonObject = new JSONObject();
        String method = "MD004";

        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        String mobile = params.getString("mobile"); // 手机号
        String name = params.getString("userName"); // 姓名
        String cardID = params.getString("idCard"); // 身份证号码

        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", mobile);
        map.put("name", name);
        map.put("cardID", cardID);
        map.put("select", method);
        map.put("accountID", accountID);

        String sign = ShuZunHelper.getSign(map);

        logger.info("签名sign：" + sign);
        Long tt1 = System.currentTimeMillis();


        String urlResult = ShuZunHelper.getUrlResult(sign, map);
        String response = HttpRequest.sendGet(urlResult, null);

        logger.info("response >>> " + response);
//        String response = "{\"resCode\":\"0000\",\"resMsg\":\"请求成功\",\"tranNo\":null,\"sign\":\"D123F1CC7378B91FE646633A74444BDC\",\"data\":[{\"statusCode\":1,\"statusMsg\":\"查询成功,查得结果\",\"quotaID\":\"MD004\",\"quotaValue\":\"1\",\"channel\":1}]}";

        Long tt2 = System.currentTimeMillis();

//        System.out.println("上游响应时间  "+ (tt2-tt1));
        logger.info("上游响应时间 "+ (tt2-tt1));
        if (response == null || response.equals("")) {

            return ProjectErrorInformation.businessError1(title);
        }

        JSONObject ret = JSONObject.parseObject(response);
        String status = ret.getString("resCode");
        String errorCode = ShuZunHelper.getCodeTable(status);
        logger.info("调用信息  >>> " + status + " >>> " + errorCode);

        if ("0000".equals(status) || "1000".equals(status)) {

            JSONArray jsonObject2 = ret.getJSONArray("data");
            JSONObject obj = (JSONObject) jsonObject2.get(0);
            code = obj.getString("quotaValue");
            String channel = obj.getString("channel");
            String statusCode = obj.getString("statusCode");

            if ("1".equals(statusCode)) { // 查询成功有数据

                if ("1".equals(code)) {
                    desc = "验证一致";
                } else if ("-1".equals(code)) {
                    desc = "验证不一致";
                } else { // code = 0
                    desc = "库无记录";
                }

            } else if ("2".equals(statusCode)) { // 查询成功无数据
                desc = "查询成功无数据";
                code = "2"; // 查询无数据
            } else { // 查询失败
                desc = "查询失败";
                code = "2"; // 查询无数据
            }

            if ("1".equals(channel)) {
                desc += ";移动";
            } else if ("2".equals(channel)) {
                desc += ";电信";
            } else if ("3".equals(channel)) {
                desc += ";联通";
            } else { // code = 0
                desc += ";其他";
            }

            isbilling = "1";

        } else if ("2006".equals(status)) {
            code = "B0002";
            desc = "请求没有查询到结果";

        }

        jsonObject.put("interface", title);
        jsonObject.put("code", code);
        jsonObject.put("desc", desc);
        jsonObject.put("isbilling", isbilling);

        return jsonObject.toString();
    }

    @Override
    public String getVerifyMobileInfoV(JSONObject params) {
        String title = "姓名-身份证号-手机号码验证（完整版）";
        JSONObject jsonObject = new JSONObject();
        String method = "MD009";

        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        String mobile = params.getString("mobile"); // 手机号
        String name = params.getString("userName"); // 姓名
        String cardID = params.getString("idCard"); // 身份证号码

        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("name", name);
        map.put("cardID", cardID);
        map.put("select", method);
        map.put("accountID", accountID);

        String sign = ShuZunHelper.getSign(map);

        logger.info("签名sign：" + sign);

        String urlResult = ShuZunHelper.getUrlResult(sign, map);
        String response = HttpRequest.sendGet(urlResult, null);

        logger.info("response >>> " + response);

        if (response == null || response.equals("")) {

            return ProjectErrorInformation.businessError1(title);
        }

        JSONObject ret = JSONObject.parseObject(response);
        String status = ret.getString("resCode");
        String errorCode = ShuZunHelper.getCodeTable(status);
        logger.info("调用信息  >>> " + status + " >>> " + errorCode);

        if ("0000".equals(status) || "1000".equals(status)) {

            JSONArray jsonObject2 = ret.getJSONArray("data");
            JSONObject obj = (JSONObject) jsonObject2.get(0);
            code = obj.getString("quotaValue");
            String channel = obj.getString("channel");
            String statusCode = obj.getString("statusCode");

            if ("1".equals(statusCode)) { // 查询成功有数据

                switch (code) {
                    case "1,-1,-1": {
                        desc = "非身份证注册";
                        break;
                    }
                    case "0,0,0": {
                        desc = "身份证-手机号一致，姓名手机号一致";
                        break;
                    }
                    case "0,0,1": {
                        desc = "身份证-手机号一致，姓名手机号不一致";
                        break;
                    }
                    case "0,1,0": {
                        desc = "身份证-手机号不一致，姓名手机号一致";
                        break;
                    }
                    case "0,1,1": {
                        desc = "身份证-手机号不一致，姓名手机号不一致";
                        break;
                    }
                    case "0,1,-1": {
                        desc = "三维验证不一致，具体不一致未知";
                        break;
                    }
                    case "0,-1,-1": {
                        desc = "身份证-手机号未知，姓名手机号未知";
                        break;
                    }
                    default: {
                        desc = "库无数据";
                    }
                }
            } else if ("2".equals(statusCode)) { // 查询成功无数据
                desc = "查询成功无数据";
                code = "2"; // 查询无数据
            } else { // 查询失败
                desc = "查询失败";
                code = "3"; // 查询无数据
            }

            if ("1".equals(channel)) {
                desc += ";移动";
            } else if ("2".equals(channel)) {
                desc += ";电信";
            } else if ("3".equals(channel)) {
                desc += ";联通";
            } else { // code = 0
                desc += ";其他";
            }

            isbilling = "1";

        } else if ("2006".equals(status)) {

            code = "B0002";
            desc = "请求没有查询到结果";

        }

        jsonObject.put("interface", title);
        jsonObject.put("code", code);
        jsonObject.put("desc", desc);
        jsonObject.put("isbilling", isbilling);

        return jsonObject.toString();
    }

    @Override
    public String getTelecomContacts(JSONObject params) {
        String title = "电信定制-常用联系人";
        JSONObject jsonObject = new JSONObject();
        String method = "Z0011";

        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        String mobile = params.getString("mobile"); // 手机号
        String verifyMobile = params.getString("verifyMobile"); // 验证人手机号码
        String month = params.getString("month"); // 结束月份（YYYYMM）

        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", mobile);
        map.put("verifymobile", verifyMobile);
        map.put("month", month);
        map.put("select", method);
        map.put("accountID", accountID);

        String sign = ShuZunHelper.getSign(map);

        logger.info("签名sign：" + sign);

        String urlResult = ShuZunHelper.getUrlResult(sign, map);
        String response = HttpRequest.sendGet(urlResult, null);

        logger.info("response >>> " + response);

        if (response == null || response.equals("")) {

            return ProjectErrorInformation.businessError1(title);
        }

        JSONObject ret = JSONObject.parseObject(response);
        String status = ret.getString("resCode");
        String errorCode = ShuZunHelper.getCodeTable(status);
        logger.info("调用信息  >>> " + status + " >>> " + errorCode);

        if ("0000".equals(status) || "1000".equals(status)) {

            JSONArray jsonObject2 = ret.getJSONArray("data");
            JSONObject obj = (JSONObject) jsonObject2.get(0);
            code = obj.getString("quotaValue");
            String channel = obj.getString("channel");
            String statusCode = obj.getString("statusCode");

            if ("1".equals(statusCode)) { // 查询成功有数据

                if ("0".equals(code)) {
                    desc = "是";
                } else if ("1".equals(code)) {
                    desc = "不是";
                } else if ("MDN_NOT_EXIST".equals(code)) { // code = 0
                    desc = "手机号码不存在";
                } else if ("DATA_NOT_EXIST".equals(code)) { // code = 0
                    desc = "手机号码不存在";
                } else {
                    code = "B0004";
                    desc = "其他错误";
                }

            } else if ("2".equals(statusCode)) { // 查询成功无数据
                desc = "查询成功无数据";
                code = "2"; // 查询无数据
            } else { // 查询失败
                desc = "查询失败";
                code = "2"; // 查询无数据
            }

            if ("1".equals(channel)) {
                desc += ";移动";
            } else if ("2".equals(channel)) {
                desc += ";电信";
            } else if ("3".equals(channel)) {
                desc += ";联通";
            } else { // code = 0
                desc += ";其他";
            }

            isbilling = "1";

        } else if ("2006".equals(status)) {

            code = "B0002";
            desc = "请求没有查询到结果";

        }

        jsonObject.put("interface", title);
        jsonObject.put("code", code);
        jsonObject.put("desc", desc);
        jsonObject.put("isbilling", isbilling);

        return jsonObject.toString();
    }

    @Override
    public String getUnicomContacts(JSONObject params) {
        String title = "联通定制-常用联系人";
        JSONObject jsonObject = new JSONObject();
        String method = "LT018";

        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        String mobile = params.getString("mobile"); // 手机号
        String verifyMobile = params.getString("verifyMobile"); // 验证人手机号码

        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", mobile);
        map.put("verifymobile", verifyMobile);
        map.put("select", method);
        map.put("accountID", accountID);

        String sign = ShuZunHelper.getSign(map);

        logger.info("签名sign：" + sign);

        String urlResult = ShuZunHelper.getUrlResult(sign, map);
        String response = HttpRequest.sendGet(urlResult, null);

        logger.info("response >>> " + response);

        if (response == null || response.equals("")) {

            return ProjectErrorInformation.businessError1(title);
        }

        JSONObject ret = JSONObject.parseObject(response);
        String status = ret.getString("resCode");
        String errorCode = ShuZunHelper.getCodeTable(status);
        logger.info("调用信息  >>> " + status + " >>> " + errorCode);

        if ("0000".equals(status) || "1000".equals(status)) {

            JSONArray jsonObject2 = ret.getJSONArray("data");
            JSONObject obj = (JSONObject) jsonObject2.get(0);
            code = obj.getString("quotaValue");
            String channel = obj.getString("channel");
            String statusCode = obj.getString("statusCode");

            if ("1".equals(statusCode)) { // 查询成功有数据

                if ("T".equals(code)) {
                    desc = "是";
                } else if ("F".equals(code)) {
                    desc = "不是";
                } else if ("U".equals(code)) { // code = 0
                    desc = "无法验证";
                } else {
                    code = "B0004";
                    desc = "其他错误";
                }

            } else if ("2".equals(statusCode)) { // 查询成功无数据
                desc = "查询成功无数据";
                code = "2"; // 查询无数据
            } else { // 查询失败
                desc = "查询失败";
                code = "2"; // 查询无数据
            }

            if ("1".equals(channel)) {
                desc += ";移动";
            } else if ("2".equals(channel)) {
                desc += ";电信";
            } else if ("3".equals(channel)) {
                desc += ";联通";
            } else { // code = 0
                desc += ";其他";
            }

            isbilling = "1";

        } else if ("2006".equals(status)) {

            code = "B0002";
            desc = "请求没有查询到结果";

        }

        jsonObject.put("interface", title);
        jsonObject.put("code", code);
        jsonObject.put("desc", desc);
        jsonObject.put("isbilling", isbilling);

        return jsonObject.toString();
    }

    @Override
    public String getOnlineTime(JSONObject params) {
        String title = "运营商在网时长";
        JSONObject jsonObject = new JSONObject();
        String method = "MD002";

        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        String mobile = params.getString("mobile"); // 手机号

        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", mobile);
        map.put("select", method);
        map.put("accountID", accountID);

        String sign = ShuZunHelper.getSign(map);

        logger.info("签名sign：" + sign);

        String urlResult = ShuZunHelper.getUrlResult(sign, map);
        String response = HttpRequest.sendGet(urlResult, null);

        logger.info("response >>> " + response);

        if (response == null || response.equals("")) {

            return ProjectErrorInformation.businessError1(title);
        }

        JSONObject ret = JSONObject.parseObject(response);
        String status = ret.getString("resCode");
        String errorCode = ShuZunHelper.getCodeTable(status);
        logger.info("调用信息  >>> " + status + " >>> " + errorCode);

        if ("0000".equals(status) || "1000".equals(status)) {

            JSONArray jsonObject2 = ret.getJSONArray("data");
            JSONObject obj = (JSONObject) jsonObject2.get(0);
            code = obj.getString("quotaValue");
            String channel = obj.getString("channel");
            String statusCode = obj.getString("statusCode");

            if ("1".equals(statusCode)) { // 查询成功有数据

                if (code == null) {
                    desc = "数据不存在";
                } else if ("A".equals(code)) {
                    desc = "[0-3),【单位：月】";
                } else if ("B".equals(code)) { // code = 0
                    desc = "[3-6),【单位：月】";
                } else if ("C".equals(code)) { // code = 0
                    desc = "[6-12),【单位：月】";
                } else if ("D".equals(code)) { // code = 0
                    desc = "[12-24),【单位：月】";
                } else if ("E".equals(code)) { // code = 0
                    desc = "[24+),【单位：月】";
                } else if ("-1".equals(code)) { // code = 0
                    desc = "手机号码不存在";
                } else {
                    code = "B0004";
                    desc = "其他错误";
                }

            } else if ("2".equals(statusCode)) { // 查询成功无数据
                desc = "查询成功无数据";
                code = "2"; // 查询无数据
            } else { // 查询失败
                desc = "查询失败";
                code = "2"; // 查询无数据
            }

            if ("1".equals(channel)) {
                desc += ";移动";
            } else if ("2".equals(channel)) {
                desc += ";电信";
            } else if ("3".equals(channel)) {
                desc += ";联通";
            } else { // code = 0
                desc += ";其他";
            }

            isbilling = "1";

        } else if ("2006".equals(status)) {

            code = "B0002";
            desc = "请求没有查询到结果";

        }

        jsonObject.put("interface", title);
        jsonObject.put("code", code);
        jsonObject.put("desc", desc);
        jsonObject.put("isbilling", isbilling);

        return jsonObject.toString();
    }

    @Override
    public String getUserState(JSONObject params) {
        String title = "运营商在网状态";
        JSONObject jsonObject = new JSONObject();
        String method = "MD001";

        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        String mobile = params.getString("mobile"); // 手机号

        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", mobile);
        map.put("select", method);
        map.put("accountID", accountID);

        String sign = ShuZunHelper.getSign(map);

        logger.info("签名sign：" + sign);

        String urlResult = ShuZunHelper.getUrlResult(sign, map);
        String response = HttpRequest.sendGet(urlResult, null);

        logger.info("response >>> " + response);

        if (response == null || response.equals("")) {
            return ProjectErrorInformation.businessError1(title);
        }

        JSONObject ret = JSONObject.parseObject(response);
        String status = ret.getString("resCode");
        String errorCode = ShuZunHelper.getCodeTable(status);
        logger.info("调用信息  >>> " + status + " >>> " + errorCode);

        if ("0000".equals(status) || "1000".equals(status)) {

            JSONArray jsonObject2 = ret.getJSONArray("data");
            JSONObject obj = (JSONObject) jsonObject2.get(0);
            code = obj.getString("quotaValue");
            String channel = obj.getString("channel");
            String statusCode = obj.getString("statusCode");

            if ("1".equals(statusCode)) { // 查询成功有数据

                if (code == null) {
                    desc = "数据不存在";
                } else if ("1".equals(code)) {
                    desc = "正常在用";
                } else if ("2".equals(code)) { // code = 0
                    desc = "停机";
                } else if ("3".equals(code)) { // code = 0
                    desc = "在网但不可用";
                } else if ("4".equals(code)) { // code = 0
                    desc = "不在网";
                } else if ("9".equals(code)) { // code = 0
                    desc = "无法查询";
                } else if ("-1".equals(code)) { // code = 0
                    desc = "手机号码不存在";
                } else {
                    code = "B0004";
                    desc = "其他错误";
                }

            } else if ("2".equals(statusCode)) { // 查询成功无数据
                desc = "查询成功无数据";
                code = "7"; // 查询无数据
            } else { // 查询失败
                desc = "查询失败";
                code = "7"; // 查询无数据
            }

            if ("1".equals(channel)) {
                desc += ";移动";
            } else if ("2".equals(channel)) {
                desc += ";电信";
            } else if ("3".equals(channel)) {
                desc += ";联通";
            } else { // code = 0
                desc += ";其他";
            }

            isbilling = "1";

        } else if ("2006".equals(status)) {
            code = "B0002";
            desc = "请求没有查询到结果";

        }

        jsonObject.put("interface", title);
        jsonObject.put("code", code);
        jsonObject.put("desc", desc);
        jsonObject.put("isbilling", isbilling);

        return jsonObject.toString();
    }

    @Override
    public String getTelecomCircle(JSONObject params) {
        String title = "电信定制-手机号码交往圈大小";
        JSONObject jsonObject = new JSONObject();
        String method = "Z0007";

        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        String mobile = params.getString("mobile");
        String month = params.getString("month");

        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("month", month);
        map.put("accountID", accountID);
        map.put("select", method);
        String sign = ShuZunHelper.getSign(map);
        logger.info("签名sign：" + sign);

        String urlResult = ShuZunHelper.getUrlResult(sign, map);
        String response = HttpRequest.sendGet(urlResult, null);
        logger.info("response >>> " + response);
        if (response == null || response.equals("")) {
            return ProjectErrorInformation.businessError1(title);
        }

        JSONObject ret = JSONObject.parseObject(response);
        String status = ret.getString("resCode");
        String errorCode = ShuZunHelper.getCodeTable(status);
        logger.info("调用信息  >>> " + status + " >>> " + errorCode);

        if ("0000".equals(status) || "1000".equals(status)) {
            JSONArray data = ret.getJSONArray("data");
            JSONObject obj = (JSONObject) data.get(0);
            String value = obj.getString("quotaValue");
            String channel = obj.getString("channel");
            String statusCode = obj.getString("statusCode");

            // 查询成功有数据
            if ("1".equals(statusCode)) {
                code = "1";
                if ("-1".equals(value)||"null".equals(value)){
                    code = "2";
                    desc = "数据不存在";
                }else {
                    desc = "查询成功,得分："+value;
                }

                // 查询成功无数据
            } else if ("2".equals(statusCode)) {
                desc = "查询成功无数据";
                code = "2"; // 查询无数据
            } else { // 查询失败
                desc = "查询失败";
                code = "3"; // 查询无数据
            }

            if ("1".equals(channel)) {
                desc += ";移动";
            } else if ("2".equals(channel)) {
                desc += ";电信";
            } else if ("3".equals(channel)) {
                desc += ";联通";
            } else { // code = 0
                desc += ";其他";
            }
            isbilling = "1";
        } else if ("2006".equals(status)) {

            code = "B0002";
            desc = "请求没有查询到结果";

        }
        jsonObject.put("interface", title);
        jsonObject.put("code", code);
        jsonObject.put("desc", desc);
        jsonObject.put("isbilling", isbilling);

        return jsonObject.toString();
    }

    @Override
    public String getUnicomCircle(JSONObject params) {
        String title = "联通定制-手机号码交往圈大小";
        JSONObject jsonObject = new JSONObject();
        String method = "LT031";

        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        String mobile = params.getString("mobile");
        String month = params.getString("month");

        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("month", month);
        map.put("accountID", accountID);
        map.put("select", method);
        String sign = ShuZunHelper.getSign(map);
        logger.info("签名sign：" + sign);

        String urlResult = ShuZunHelper.getUrlResult(sign, map);
        String response = HttpRequest.sendGet(urlResult, null);
        logger.info("response >>> " + response);
        if (response == null || response.equals("")) {
            return ProjectErrorInformation.businessError1(title);
        }

        JSONObject ret = JSONObject.parseObject(response);
        String status = ret.getString("resCode");
        String errorCode = ShuZunHelper.getCodeTable(status);
        logger.info("调用信息  >>> " + status + " >>> " + errorCode);

        if ("0000".equals(status) || "1000".equals(status)) {
            JSONArray data = ret.getJSONArray("data");
            JSONObject obj = (JSONObject) data.get(0);
            String value = obj.getString("quotaValue");
            String channel = obj.getString("channel");
            String statusCode = obj.getString("statusCode");

            // 查询成功有数据
            if ("1".equals(statusCode)) {
                code = "1";
                if ("-1".equals(value)||"null".equals(value)){
                    code = "2";
                    desc = "查询成功无数据";
                }else {
                    desc = "查询成功,得分："+value;
                }
                // 查询成功无数据
            } else if ("2".equals(statusCode)) {
                desc = "查询成功无数据";
                code = "2"; // 查询无数据
            } else { // 查询失败
                desc = "查询失败";
                code = "3"; // 查询无数据
            }

            if ("1".equals(channel)) {
                desc += ";移动";
            } else if ("2".equals(channel)) {
                desc += ";电信";
            } else if ("3".equals(channel)) {
                desc += ";联通";
            } else { // code = 0
                desc += ";其他";
            }
            isbilling = "1";
        } else if ("2006".equals(status)) {

            code = "B0002";
            desc = "请求没有查询到结果";

        }
        jsonObject.put("interface", title);
        jsonObject.put("code", code);
        jsonObject.put("desc", desc);
        jsonObject.put("isbilling", isbilling);

        return jsonObject.toString();
    }

    @Override
    public String getVerifyIdentifyInfo(JSONObject params) {
        String title = "姓名-身份证号验证";
        JSONObject jsonObject = new JSONObject();
        String method = "RZ028";

        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        String name = params.getString("userName");
        String cardID = params.getString("idCard");

        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("cardID", cardID);
        map.put("accountID", accountID);
        map.put("select", method);

        String sign = ShuZunHelper.getSign(map);
        logger.info("签名sign：" + sign);
        String urlResult = ShuZunHelper.getUrlResult(sign, map);
        String result = HttpRequest.sendGet(urlResult, null);
        logger.info("result###" + result);

        if (StringUtils.isEmpty(result)) {
            return ProjectErrorInformation.businessError1(title);
        }

        JSONObject jsonResult = JSONObject.parseObject(result);
        String resCode = (String) jsonResult.get("resCode");
        String errorCode = ShuZunHelper.getCodeTable(resCode);

        logger.info("调用结果>>>" + resCode + " : " + errorCode);
        if ("0000".equals(resCode) || "1000".equals(resCode)) {
            JSONArray jsonArray = jsonResult.getJSONArray("data");
            JSONObject json2 = (JSONObject) jsonArray.get(0);
            code = json2.getString("statusCode");
            String qutoValue = json2.getString("quotaValue");

            if ("1".equals(code)) {
                if ("0".equals(qutoValue)) {
                    desc = "认证一致";
                } else if ("1".equals(qutoValue)) {
                    desc = "认证不一致";
                } else {                            //qutoValue = -1
                    desc = "库无记录";
                }
                code = qutoValue;
            } else if ("2".equals(code)) {
                desc = "查询成功无数据";
            } else {                                  //code = 3
                desc = "查询失败";
            }

            isbilling = "1";

        } else if ("2006".equals(resCode)) {
            code = "B0002";
            desc = "请求未查询到结果";
        }

        jsonObject.put("interface", title);
        jsonObject.put("code", code);
        jsonObject.put("desc", desc);
        jsonObject.put("isbilling", isbilling);
        return jsonObject.toString();
    }

    @Override
    public String getWorkAddressPosition(JSONObject bizParams) {
        String title = "工作地址位置验证";
        String select ="RZ034";

        String name = "userName";
        String mobile = bizParams.getString("mobile");
        String cardID = bizParams.getString("idCard");
        String longitude = bizParams.getString("longitude");
        String latitude = bizParams.getString("latitude");

        if (StringUtils.isEmpty(name)||StringUtils.isEmpty(mobile)
                ||StringUtils.isEmpty(cardID)||StringUtils.isEmpty(longitude)||StringUtils.isEmpty(latitude)){
            return ProjectErrorInformation.businessError5(title);
        }
        String code = "B0001";
        String desc = "调用失败";
        String isBilling = "0";

        Map<String,String> map = new HashMap<String,String>();
        map.put("name",name);
        map.put("mobile",mobile);
        map.put("cardID",cardID);
        map.put("longitude",longitude);
        map.put("latitude",latitude);
        map.put("accountID",accountID);
        map.put("select",select);

        String sign = ShuZunHelper.getSign(map);
        String urlResult = ShuZunHelper.getUrlResult(sign,map);
        String response = HttpRequest.sendGet(urlResult,null);

        logger.info("response>>> "+response);
        if (StringUtils.isEmpty(response)){
            return ProjectErrorInformation.businessError1(title);
        }
        JSONObject jsonResult = JSONObject.parseObject(response);
        String resCode = jsonResult.getString("resCode");
        String resMsg = jsonResult.getString("resMsg");

        logger.info("调用结果： "+resCode+"| "+resMsg);
        if ("0000".equals(resCode)||"1000".equals(resCode)){
            JSONArray jsonArray = jsonResult.getJSONArray("data");
            JSONObject json2 = (JSONObject) jsonArray.get(0);

            code = json2.getString("statusCode");
            String qutoValue = json2.getString("qutoValue");
            String channel = json2.getString("channel");


            if ("1".equals(code)){
                    switch (qutoValue) {
                        case "A": {
                            desc = "距离(0,2]公里";
                            code = "1";
                            break;
                        }
                        case "B": {
                            desc = "距离(2,5]公里";
                            code = "2";
                            break;
                        }
                        case "C": {
                            desc = "距离(5,10]公里";
                            code = "3";
                            break;
                        }
                        case "D": {
                            desc = "10公里以上，但在同一个城市";
                            code = "4";
                            break;
                        }
                        default: {
                            desc = "不在同一城市【单位：公里】";
                            code = "5";
                        }
                }
            }else if ("2".equals(code)){
                desc = "查询成功无数据";
                code = "0";
            }else {
                desc = "查询失败";
                code = "-1";
            }


            if ("1".equals(channel)) {
                desc += ";移动";
            } else if ("2".equals(channel)) {
                desc += ";电信";
            } else if ("3".equals(channel)) {
                desc += ";联通";
            } else { // code = 0
                desc += ";其他";
            }

            isBilling = "1";
        }else if("2006".equals(code)) {
            code = "B0002";
            desc = "请求没有查询到结果";
        }

        JSONObject json = new JSONObject();
        json.put("interface",title);
        json.put("code",code);
        json.put("desc",desc);
        json.put("isbilling",isBilling);

        return json.toString();
    }

    @Override
    public String getCurrentStayCity(JSONObject bizParams) {
        String title = "手机号当前停留城市验证";
        String select = "RZ030";

        String mobile =bizParams.getString("mobile");
        String citycode = bizParams.getString("cityCode");
        if (StringUtils.isEmpty(mobile)||StringUtils.isEmpty(citycode)){
            return ProjectErrorInformation.businessError5(title);
        }

        String code = "B0001";
        String desc = "调用失败";
        String isBilling = "0";

        Map<String,String> map = new HashMap<String,String>();
        map.put("accountID",accountID);
        map.put("select",select);
        map.put("mobile",mobile);
        map.put("cityCode",citycode);
        String sign = ShuZunHelper.getSign(map);
        String urlResult = ShuZunHelper.getUrlResult(sign,map);

        String response = HttpRequest.sendGet(urlResult,null);
        logger.info("response>>> "+response);
        if (StringUtils.isEmpty(response)){
            return ProjectErrorInformation.businessError1(title);
        }

        JSONObject jsonResult = JSON.parseObject(response);
        String resCode = jsonResult.getString("resCode");
        String resMsg = jsonResult.getString("resMsg");
        logger.info("调用结果"+resCode+"| "+resMsg);
        if ("0000".equals(resCode)||"1000".equals(resCode)){
            isBilling = "1";
            JSONArray jsonArray = jsonResult.getJSONArray("data");
            JSONObject jsons = (JSONObject)jsonArray.get(0);
            String state = jsons.getString("statusCode");
            String qotoValue = jsons.getString("quotaValue");
            String channel = jsons.getString("channel");
            if ("1".equals(state)){
                desc = "查询成功有数据";
                    switch (qotoValue){
                        case "1":desc = "省份验证一致，城市验证一致";break;
                        case "2":desc = "省份验证一致，城市验证不一致";break;
                        case "3":desc = "省份验证不一致，城市验证不一致";break;
                    }
                code = qotoValue;
            } else if ("2".equals(state)){
                desc = "查询成功无数据";
                switch (qotoValue){
                    case "4":desc = "号码无法验证";break;
                    case "0":desc = "手机号无记录";break;
                }
                code = qotoValue;
            } else {
                code = "-1";
                desc = "查询失败";
            }

            if ("1".equals(channel)) {
                desc += ";移动";
            } else if ("2".equals(channel)) {
                desc += ";电信";
            } else if ("3".equals(channel)) {
                desc += ";联通";
            } else { // code = 0
                desc += ";其他";
            }

        } else if ("2006".equals(resCode)){
            code = "B0002";
            desc = "请求未查询到结果";
        }

        JSONObject json = new JSONObject();
        json.put("interface",title);
        json.put("code",code);
        json.put("desc",desc);
        json.put("isbilling",isBilling);
        return json.toString();
    }

    /**
     * 姓名-身份证号-手机号码核验
     *
     * @param bizParams 需要传入userName、idCard、mobile三个参数
     * @return
     */
    @Override
    public String getVerifyVerificationIII(JSONObject bizParams) {

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
            String oprator = service.getPhoneProvider(mobile);
            if ("YD".equals(oprator)){
                method = method2;
            } else {
                method = method1;
            }
            logger.info("oprator>>  "+oprator);
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
//            String response = ZhongchengxinHelper.doGetByProxy(method, req);
            logger.info(name + " response >> " + response);

            String retCode = "B0001";
            String retDesc = "调用失败";
            if (StringUtils.isEmpty(response)) {
                return ProjectErrorInformation.businessError1(title);
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

            switch (operator) {
                case "1":
                    operator = "电信";
                    break;
                case "2":
                    operator = "联通";
                    break;
                case "3":
                    operator = "移动";
                    break;
                default:
                    operator = "未知";
            }
            switch (status) {
                case "1001":
                case "1002":
                case "1003":
                case "1011":
                case "1013": {
                    ret.put("code", retCode);
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
                    ret.put("code", "0,0,0");
                    ret.put("desc", "身份证-手机号一致，姓名手机号一致；"+operator);
                    ret.put("isbilling", "1");
                    break;
                }
                case "2062": {
                    ret.put("code", "0,-1,-1");
                    ret.put("desc", "身份证-手机号未知，姓名手机号未知；"+operator);
                    ret.put("isbilling", "1");
                    break;
                }
                case "2063": {
                    ret.put("code", "0,-1,-1");
                    ret.put("desc", "身份证-手机号未知，姓名手机号未知；"+operator);
                    ret.put("isbilling", "1");
                    break;
                }
                case "2067": {
                    ret.put("code", "0,1,0");
                    ret.put("desc", "身份证-手机号不一致，姓名手机号一致；"+operator);
                    ret.put("isbilling", "1");
                    break;
                }
                case "2068": {
                    ret.put("code", "0,0,1");
                    ret.put("desc", "身份证-手机号一致，姓名手机号不一致；"+operator);
                    ret.put("isbilling", "1");
                    break;
                }
                case "2069": {
                    ret.put("code", "0,1,1");
                    ret.put("desc", "身份证-手机号不一致，姓名手机号不一致；"+operator);
                    ret.put("isbilling", "1");
                    break;
                }
                case "2073": {
                    ret.put("code", "0,-1,-1");
                    ret.put("desc", "身份证-手机号未知，姓名手机号未知；"+operator);
                    ret.put("isbilling", "1");
                    break;
                }
                // 9999
                default: {
                    ret.put("code", "3");
                    ret.put("desc", "查询失败");
                    ret.put("isbilling", "0");
                }
            }
            return ret.toString();
        }
    }
