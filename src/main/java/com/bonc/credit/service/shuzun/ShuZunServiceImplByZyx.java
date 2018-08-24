package com.bonc.credit.service.shuzun;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.service.baichuan.BaiChuanHelper;
import com.bonc.credit.service.zhongchengxin.ZhongchengxinHelper;
import com.bonc.util.HttpRequest;
import com.bonc.util.ProjectErrorInformation;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyixuan
 * @date 2018年7月26日
 */
@Service
public class ShuZunServiceImplByZyx extends ShuZunHelper implements ShuZunServiceByZyx {

    private static final Logger logger = Logger.getLogger(ShuZunServiceImplByZyx.class);

    /**
     * 传入参数，对参数进行处理转成map格式
     * @param params
     * @param strings
     * @return
     */
    public Map<String,String> getMap(JSONObject params,String...strings){
        Map<String,String> map=new HashMap<String,String>();
        map.put("accountID",accountID);
        for(String string : strings){
            String result=params.getString(string);
            map.put(string,result);
        }
        return map;
    }


    /**
     * 获取sign标签，并生成url调用接口，获取返回值
     * @param map
     * @param title
     * @return
     */
    public String UrlSend(Map<String,String> map,String title){
        String sign = ShuZunHelper.getSign(map);
        logger.info("签名sign：" + sign);
        String urlResult = ShuZunHelper.getUrlResult(sign, map);
        String response = HttpRequest.sendGet(urlResult, null);
        logger.info("response >>> "+response+",sign >>>"+sign);
        return response;
    }

    /**
     * 对结果进行JSONObject处理
     * @param title
     * @param code
     * @param desc
     * @param isbilling
     * @return
     */
    public String getJSONObject(String title,String code,String desc,String isbilling){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("interface", title);
        jsonObject.put("code", code);
        jsonObject.put("desc", desc);
        jsonObject.put("isbilling", isbilling);
        return jsonObject.toString();
    }

    /**
     * 验证联通定制-经纬度验证
     *
     * @param params
     * @return
     */
    @Override
    public String getLatitudeAndLongitude(JSONObject params){
        String title = "联通定制-经纬度验证";
        String select="LT028";
        Map<String,String> map=getMap(params,"mobile","longitude","latitude");
        map.put("select",select);
        String response = UrlSend(map,title);
        if (StringUtils.isEmpty(response)) {
            return ProjectErrorInformation.businessError1(title);
        }




        JSONObject jsonResult = JSONObject.parseObject(response);
        String resCode = (String) jsonResult.get("resCode");
        String errorCode = ShuZunHelper.getCodeTable(resCode);
        logger.info("调用结果>>>" + resCode + " : " + errorCode);
        String isbilling="0";
        String desc = "查询失败";
        String code = "B0001";
        if("0000".equals(resCode) || "1000".equals(resCode)){
            isbilling="1";
            JSONArray jsonArray = jsonResult.getJSONArray("data");
            JSONObject json2=(JSONObject) jsonArray.get(0);
            String statusCode=json2.getString("statusCode");
            String quotaValue=json2.getString("quotaValue");
            if("1".equals(statusCode)) {
                desc = "查询成功有数据";
                if("A".equals(quotaValue)){
                    desc+=",距离[0,3]km";
                    code="11";
                }else if("B".equals(quotaValue)) {
                    desc+=",距离(3,10]km";
                    code="12";
                }else if("C".equals(quotaValue)) {
                    desc+=",距离(10,20]km";
                    code="13";
                }else if("D".equals(quotaValue)) {
                    desc+=",距离(20,50]km";
                    code="14";
                }else if("E".equals(quotaValue)) {
                    desc+=",距离(50,+)km";
                    code="15";
                }else {
                    desc+=",无法查询";
                    code="16";
                }
            }else if ("2".equals(statusCode)) {
                desc = "查询成功无数据";
                code="20";
            } else if("3".equals(statusCode)){
                desc = "查询失败";
                code="30";
            }
        }else if ("2006".equals(resCode)) {
            //未查询到结果
            code = "B0002";
            desc = "请求未查询到结果";
        }





        return getJSONObject(title,code,desc,isbilling);
    }

    /**
     * 居住地址位置验证
     *
     * @param params
     * @return
     */
    @Override
    public String getResidentialAddress(JSONObject params) {
        String title = "居住地址位置验证";
        String select="RZ035";
        Map<String,String> map=getMap(params,"name","mobile","cardID","longitude","latitude");
        map.put("select",select);
        String response = UrlSend(map,title);
        if (StringUtils.isEmpty(response)) {
            return ProjectErrorInformation.businessError1(title);
        }



        JSONObject jsonResult = JSONObject.parseObject(response);
        String resCode = (String) jsonResult.get("resCode");
        String errorCode = ShuZunHelper.getCodeTable(resCode);
        logger.info("调用结果>>>" + resCode + " : " + errorCode);
        String isbilling="0";
        String desc = "查询失败";
        String code = "B0001";
        if("0000".equals(resCode) || "1000".equals(resCode)) {
            isbilling = "1";
            JSONArray jsonArray = jsonResult.getJSONArray("data");
            JSONObject json2=(JSONObject) jsonArray.get(0);
            String statusCode=json2.getString("statusCode");
            String quotaValue=json2.getString("quotaValue");
            String channel = json2.getString("channel");
            if("1".equals(statusCode)) {
                desc = "查询成功有数据";
                if("A".equals(quotaValue)){
                    desc+=",距离[0,2]km";
                    code="1A";
                }else if("B".equals(quotaValue)) {
                    desc+=",距离(2,5]km";
                    code="1B";
                }else if("C".equals(quotaValue)) {
                    desc+=",距离(5,10)km";
                    code="1C";
                }else if("D".equals(quotaValue)) {
                    desc+=",距离10公里以上，但在同一个城市";
                    code="1D";
                }else if("E".equals(quotaValue)) {
                    desc+=",不在同一个城市";
                    code="1E";
                }else {
                    desc+=",未查得";
                    code="1X";
                }
            } else if ("2".equals(statusCode)) {
                desc = "查询成功无数据";
                code="2";
            } else if("3".equals(statusCode)){
                desc = "查询失败";
                code="3";
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
        }else if ("2006".equals(resCode)) {
            //未查询到结果
            code = "B0002";
            desc = "请求未查询到结果";
        }

        return getJSONObject(title,code,desc,isbilling);
    }

    /**
     * 联通定制-是否二次放号查询
     *
     * @param params
     * @return
     */
    @Override
    public String getTwoNumbers(JSONObject params) {
        String title = "联通定制-是否二次放号";
        String select="LT032";
        Map<String,String> map=getMap(params,"mobile","date");
        map.put("select",select);
        String response=UrlSend(map,title);
        JSONObject jsonResult = JSONObject.parseObject(response);
        String resCode = (String) jsonResult.get("resCode");
        String errorCode = ShuZunHelper.getCodeTable(resCode);
        logger.info("调用结果>>>" + resCode + " : " + errorCode);
        String isbilling="0";
        String desc = "查询失败";
        String code = "B0001";
        if("0000".equals(resCode) || "1000".equals(resCode)){
            isbilling="1";
            JSONArray jsonArray = jsonResult.getJSONArray("data");
            JSONObject json2=(JSONObject) jsonArray.get(0);
            String statusCode=json2.getString("statusCode");
            String value=json2.getString("value");
            if("1".equals(statusCode)){
                desc = "查询成功";
                if("T".equals(value)){
                    desc+=",是二次放号";
                    code="1T";
                }else if("F".equals(value)){
                    desc+="，不是二次放号";
                    code="1F";
                }else{
                    desc+="，库无记录";
                    code="1U";
                }
            }else if("2".equals(statusCode)){
                desc = "查询成功无数据";
                code="2";
            } else if("3".equals(statusCode)){
                desc = "查询失败";
                code="3";
            }
        }else if ("2006".equals(resCode)) {
            //未查询到结果
            code = "B0002";
            desc = "请求未查询到结果";
        }
        return getJSONObject(title,code,desc,isbilling);
    }


    /**
     * 调用百川的两元素验证
     * @param bizParams
     * @return
     */
    @Override
    public String getBaichuanInfo(JSONObject bizParams){
        String title = "验证身份证姓名是否一致";
        //姓名
        String name=bizParams.getString("userName");
        //身份证
        String idCard=bizParams.getString("idCard");
        //接口名
        String serviceName = "verifyIdNoName";
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(idCard)) {
            return ProjectErrorInformation.businessError5(title);
        }
        JSONObject jsonparam = new JSONObject();
        jsonparam.put("name",name);
        jsonparam.put("idCard",idCard);
        Map<String, Object> map = new HashMap<>();
        map.put("loginName", BaiChuanHelper.account);
        map.put("pwd", BaiChuanHelper.password);
        map.put("serviceName",serviceName);
        map.put("param",jsonparam);
        String param = JSONUtils.toJSONString(map);
        String response = BaiChuanHelper.getResponse(param);
        logger.info(" response >> " + response);

        String code = "B0001";
        String desc = "调用失败";
        String isBilling="0";
        if (response == null || "".equals(response)) {
            return ProjectErrorInformation.businessError1(title);
        }

        JSONObject result = JSONObject.parseObject(response);
        String resCode = result.getString("code");
        String resMsg = result.getString("message");

        logger.info("调用信息："+resCode+"| "+resMsg);
        //保留，会改动
        switch (resCode){
            case "-1":   desc = "库无记录";isBilling="1";code=resCode;break;
            case "0":   desc = "身份证姓名认证一致";isBilling="1";code=resCode;break;
            case "1":   desc = "身份证姓名认证不一致";isBilling="1";code=resCode;break;
            default:    desc = "异常情况，"+resCode;
        }
        JSONObject json2 = new JSONObject();
        json2.put("interface",title);
        json2.put("code",code);
        json2.put("desc",desc);
        json2.put("isbilling",isBilling);
        return json2.toString();
    }

    /**
     * 调用中诚信的两元素验证
     * @param bizParams
     * @return
     */
    @Override
    public String getZhongchengxinInfo(JSONObject bizParams) {
        String title = "证件号码-姓名核验";
        String idCard = bizParams.getString("idCard");
        String userName = bizParams.getString("userName");
        String name = "";
        try {
            name = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }
        if (null == idCard || "".equals(idCard) || null == userName || "".equals(userName)) {
            JSONObject ret = new JSONObject();
            ret.put("interface", title);
            ret.put("code", "S0001");
            ret.put("desc", "参数错误");
            ret.put("isbilling", "0");
            return ret.toString();
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
                retCode = "-1";
                retDesc = "库无记录";
                isbilling = "1";
            } else if("2013".equals(status)){
                retCode = "3";
                retDesc = "查询失败";
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
