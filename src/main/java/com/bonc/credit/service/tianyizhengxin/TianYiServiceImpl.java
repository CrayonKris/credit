package com.bonc.credit.service.tianyizhengxin;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.ProjectErrorInformation;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @Author: ZQ
 * @Date: 2018/8/7 16:36
 */
@Service
public class TianYiServiceImpl extends TianyiHelper implements TianyiService {

    private static final Logger logger = Logger.getLogger(TianYiServiceImpl.class);
    //工作信息

    @Override
    public String getJobInfo(JSONObject bizParams) {
        return null;
    }

    @Override
    public String getHomeInfo(JSONObject bizParams) {
        return null;
    }

    @Override
    public String getPhoneNameInfo(JSONObject bizParams) {
        return null;
    }

    @Override
    public String getCardNameInfo(JSONObject bizParams) {
        return null;
    }

    @Override
    public String getCardInfo(JSONObject bizParams) {
        String title = "姓名-身份证验证";
        String method = "policeIdentityAuthentication.json";
        String userName = bizParams.getString("userName");
        String idCard = bizParams.getString("idCard");
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(idCard)) {
            return ProjectErrorInformation.businessError5(title);
        }
        String name = "";
        try {
            name = URLDecoder.decode(userName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }
        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        JSONObject jsonparam = new JSONObject();
        jsonparam.put("personName", name);
        jsonparam.put("idNumber", idCard);

        //发送请求获取相应结果
        String response = getResponse(method, jsonparam);
        if (StringUtils.isEmpty(response)) {
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info(userName+" response>>  " + response);
        JSONObject jsonResult = JSONObject.parseObject(response);
        JSONObject header = jsonResult.getJSONObject("credit").getJSONObject("header");
        String rspCode = header.getString("rspCode");
        String rspDesc = header.getString("rspDesc");
        logger.info("调用结果：" + rspCode + "| " + rspDesc);

        //请求成功
        if ("00000".equals(rspCode) || "000000".equals(rspCode)) {
            String value = jsonResult.getJSONObject("credit").getJSONObject("body").getString("policeIdAuthentication");
            switch (value){
                case "0":desc = "验证一致";break;
                case "1":desc = "验证不一致";break;
            }
            code = rspCode;
            isbilling = "1";
        }
        return sentJson(title, code, desc, isbilling);
    }

    /**
     * 全网运营商三要素验证（详版）
     *
     * @param bizParams
     * @return
     */
    public String getMobilecardInfo(JSONObject bizParams) {

        String title = "全网运营商三要素验证（详版）";
        String mobile = bizParams.getString("mobile");
        String userName = bizParams.getString("userName");
        String idCard = bizParams.getString("idCard");
        String method = "threeElementsCheckAllDetail.json";

        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";
        //参数为空验证
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(mobile) || StringUtils.isEmpty(idCard)) {
            return ProjectErrorInformation.businessError5(title);
        }

        String name = "";
        try {
            name = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("name", name);
        jsonParam.put("mobile", mobile);
        jsonParam.put("idCardNum", idCard);

        //发送请求获取相应结果
        String response = getResponse(method, jsonParam);
//        String response = "{\"credit\":{\"header\":{\"version\":\"0100\",\"testFlag\":1,\"activityCode\":\"1005\",\"actionCode\":1,\"reqSys\":\"dongfangguoxin001\",\"reqChannel\":\"0\",\"reqTransID\":\"BONC1534408678389R980\",\"reqDate\":\"20180816\",\"reqDateTime\":\"20180816163758\",\"rcvSys\":\"123456\",\"rcvTransID\":\"10180816163758000002178488087221\",\"rcvDate\":\"20180816\",\"rcvDateTime\":\"20180816163759\",\"rspCode\":\"90002\",\"rspDesc\":\"查无记录\",\"authorizationCode\":\"890a1377c6034649a2c834ab7a907e75\"},\"body\":null,\"mac\":\"093f2b2e94d7e75a6bb957caad91ea74\"}}\n";
        if (StringUtils.isEmpty(response)) {
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info("response>>  " + response);
        JSONObject jsonResult = JSONObject.parseObject(response);
        JSONObject header = jsonResult.getJSONObject("credit").getJSONObject("header");
        String rspCode = header.getString("rspCode");
        String rspDesc = header.getString("rspDesc");
        logger.info("结果状态：" + rspCode + "| " + rspDesc);

        //请求成功
        if ("00000".equals(rspCode) || "000000".equals(rspCode)) {
            String checkResult = jsonResult.getJSONObject("credit").getJSONObject("body").getString("checkResultAllDetail");
            if ("1".equals(checkResult)) {
                desc = "三要素一致";
            } else if ("2".equals(checkResult)) {
                desc = "手机号已实名，但是身份证和姓名均与实名信息不一致";
            } else if ("3".equals(checkResult)) {
                desc = "手机号已实名，手机号和证件号一致，姓名不一致";
            } else if ("4".equals(checkResult)) {
                desc = "手机号已实名，手机号和姓名一致，身份证不一致";
            } else if ("5".equals(checkResult)) {
                desc = "其他不一致";
            }
            isbilling = "1";
            code = checkResult;
        } else {
            //异常返回情况
            return sendReturn(title, rspCode);
        }
        return sentJson(title, code, desc, isbilling);
    }

    @Override
    public String getMobilecardInfoII(JSONObject bizParams) {
        String title = "全网运营商三要素验证（简版）";
        String mobile = bizParams.getString("mobile");
        String username = bizParams.getString("userName");
        String idCard = bizParams.getString("idCard");
        String method = "threeElementsCheckSim.json";

        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(username) || StringUtils.isEmpty(idCard)) {
            return ProjectErrorInformation.businessError5(title);
        }
        String name = "";
        try {
            name = URLDecoder.decode(username, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }
        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("name", name);
        jsonParam.put("mobile", mobile);
        jsonParam.put("idCardNum", idCard);
        //发送请求获取相应结果
        String response = getResponse(method, jsonParam);
        if (StringUtils.isEmpty(response)) {
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info(mobile + " >>response: " + response);
        JSONObject jsonresult = JSONObject.parseObject(response);
        JSONObject header = jsonresult.getJSONObject("credit").getJSONObject("header");
        String rspcode = header.getString("rspCode");
        String rspdesc = header.getString("rspDesc");
        logger.info("调用结果：" + rspcode + " | " + rspdesc);

        if ("00000".equals(rspcode) || "000000".equals(rspcode)) {
            String res = jsonresult.getJSONObject("credit").getJSONObject("body").getString("checkResultSimple");
            if ("1".equals(res)) {
                desc = "验证一致";
            } else if ("2".equals(res)) {
                desc = "验证不一致";
            }
            code = rspcode;
            isbilling = "1";
        } else {
            //其他异常情况
            return sendReturn(title, rspcode);
        }
        return sentJson(title, code, desc, isbilling);
    }

    @Override
    public String getBankcard4Info(JSONObject bizParams) {
        return null;
    }

    @Override
    public String getBankcard5Info(JSONObject bizParams) {
        return null;
    }

    @Override
    public String getUserState(JSONObject bizParams) {
        String title = "全网运营商手机在网状态查询";
        String method = "wholeNetWorkStastus.json";
        String mobile = bizParams.getString("mobile");
        if (StringUtils.isEmpty(mobile)) {
            return ProjectErrorInformation.businessError5(title);
        }
        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";
        JSONObject param = new JSONObject();
        param.put("mobileNo", mobile);

        //发送请求获取相应结果
        String response = getResponse(method, param);
        if (StringUtils.isEmpty(response)) {
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info(mobile + " >>response " + response);

        JSONObject jsonresult = JSONObject.parseObject(response);
        JSONObject header = jsonresult.getJSONObject("credit").getJSONObject("header");
        String rspcode = header.getString("rspCode");
        String rspdesc = header.getString("rspDesc");
        logger.info("调用结果：" + rspcode + " | " + rspdesc);

        if ("00000".equals(rspcode) || "000000".equals(rspcode)) {
            String netWorkStastus = jsonresult.getJSONObject("credit").getJSONObject("body").getString("netWorkStastus");
            switch (netWorkStastus) {
                case "1":
                    desc = "正常再用";
                    break;
                case "2":
                    desc = "销户";
                    break;
                case "3":
                    desc = "停机";
                    break;
                case "4":
                    desc = "未启用";
                    break;
            }
            code = rspcode;
            isbilling = "1";
        } else {
            //其他异常返回情况
            return sendReturn(title, rspcode);
        }
        return sentJson(title, code, desc, isbilling);
    }

    @Override
    public String getOnlineTime(JSONObject bizParams) {
        String title = "全网运营商手机入网时长查询";
        String method = "wholeInDate.json";
        String mobile = bizParams.getString("mobile");
        if (StringUtils.isEmpty(mobile)) {
            return ProjectErrorInformation.businessError5(title);
        }
        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";
        JSONObject param = new JSONObject();
        param.put("mobileNo", mobile);

        //发送请求获取相应结果
        String response = getResponse(method, param);
        if (StringUtils.isEmpty(response)) {
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info(mobile + " >>response " + response);

        JSONObject jsonresult = JSONObject.parseObject(response);
        JSONObject header = jsonresult.getJSONObject("credit").getJSONObject("header");
        String rspcode = header.getString("rspCode");
        String rspdesc = header.getString("rspDesc");
        logger.info("调用结果：" + rspcode + " | " + rspdesc);

        if ("00000".equals(rspcode) || "000000".equals(rspcode)) {
            String netWorkTime = jsonresult.getJSONObject("credit").getJSONObject("body").getString("netWorkTime");
            switch (netWorkTime) {
                case "03":
                    desc = "在网(0-3]月";
                    break;
                case "06":
                    desc = "在网(3-6]月";
                    break;
                case "12":
                    desc = "在网(6-12]月";
                    break;
                case "24":
                    desc = "在网(12-24]月";
                    break;
                case "99":
                    desc = "在网(24+)月";
                    break;
            }
            code = rspcode;
            isbilling = "1";
        } else {
            return sendReturn(title, rspcode);
        }
        return sentJson(title, code, desc, isbilling);
    }

    /**
     * 返回
     *
     * @param title
     * @param code
     * @param desc
     * @param isBilling
     * @return
     */
    public String sentJson(String title, String code, String desc, String isBilling) {
        JSONObject json2 = new JSONObject();
        json2.put("interface", title);
        json2.put("code", code);
        json2.put("desc", desc);
        json2.put("isbilling", isBilling);
        return json2.toString();
    }

    /**
     * 异常返回情况
     *
     * @param title
     * @param rspcode
     * @return
     */
    public String sendReturn(String title, String rspcode) {
        if (StringUtils.isNotEmpty(rspcode)) {
            if ("90002".equals(rspcode)) {
                //查无数据
                return ProjectErrorInformation.businessError2(title);
            } else if ("B00098".equals(rspcode)) {
                //参数为空或格式错误
                return ProjectErrorInformation.businessError5(title);
            } else {
                //其他错误
                return ProjectErrorInformation.businessError4(title);
            }
        }
        return null;
    }
}
