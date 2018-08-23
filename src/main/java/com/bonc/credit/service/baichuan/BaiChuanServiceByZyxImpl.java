package com.bonc.credit.service.baichuan;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bonc.util.ProjectErrorInformation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaiChuanServiceByZyxImpl implements BaiChuanServiceByZyx {

    private final static Logger logger = LoggerFactory.getLogger(BaiChuanServiceByZyxImpl.class);


    /**
     * 获取当前的头部数据
     * @param json
     * @param arg
     * @return
     */
    private JSONObject getJson(JSONObject json,String...arg){
        JSONObject jsonparam = new JSONObject();
        for(String str:arg){
            jsonparam.put(str,json.getString("str"));
        }
        return jsonparam;
    }

    /**
     * 头部获取
     * @param
     * @return
     */
    private String getString(String serviceName,JSONObject jsonparam){
        Map<String, Object> map = new HashMap<>();
        map.put("loginName", BaiChuanHelper.account);
        map.put("pwd", BaiChuanHelper.password);
        map.put("serviceName",serviceName);
        map.put("param",jsonparam);
        String param = JSONUtils.toJSONString(map);
        return param;
    }

    /**
     * 返回值设置
     * @param title
     * @param code
     * @param desc
     * @param isBilling
     * @return
     */
    private String resultJson(String title,String code,String desc,String isBilling){
        JSONObject json2 = new JSONObject();
        json2.put("interface",title);
        json2.put("code",code);
        json2.put("desc",desc);
        json2.put("isbilling",isBilling);
        return json2.toString();
    }

    /**
     * 验证身份证姓名是否一致
     * @param bizParams
     * @return
     */
    @Override
    public String getCardNameInfo(JSONObject bizParams) {
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
        String param = getString(serviceName,jsonparam);
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
            case "-1":   desc = "无记录";isBilling="1";break;
            case "0":   desc = "身份证姓名一致";isBilling="1";break;
            case "1":   desc = "身份证姓名不一致";isBilling="1";break;
            default:    resCode="B0001";
        }
        return resultJson(title,resCode,desc,isBilling);
    }

    /**
     * 验证学历
     * @param bizParams
     * @return
     */
    @Override
    public String getXueli(JSONObject bizParams) {
        String title = "获取学历信息";
        //姓名
        String name=bizParams.getString("userName");
        //身份证
        String idNo=bizParams.getString("idCard");
        //接口名
        String serviceName = "getEdu";
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(idNo)) {
            return ProjectErrorInformation.businessError5(title);
        }
        JSONObject jsonparam = new JSONObject();
        jsonparam.put("name",name);
        jsonparam.put("idNo",idNo);
        String param = getString(serviceName,jsonparam);
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
        if("-1".equals(resCode)){
            isBilling="1";
            desc = "未知";
        }else if("1".equals(resCode)){
            isBilling="1";
            JSONArray data= result.getJSONArray("data");
            JSONObject dataInfo = data.getJSONObject(0);
            desc = dataInfo.toString();
        }else {
            return ProjectErrorInformation.businessError1(title);
        }
        return resultJson(title,resCode,desc,isBilling);
    }


    /**
     * 获取入网时长
     * @param bizParams
     * @return
     */
    @Override
    public String getTimeByPhone(JSONObject bizParams){
        String title = "获取在网时长";
        //手机号
        String mobile=bizParams.getString("mobile");
        //接口名
        String serviceName = "getDuration";
        if (StringUtils.isEmpty(mobile)) {
            return ProjectErrorInformation.businessError5(title);
        }
        JSONObject jsonparam = new JSONObject();
        jsonparam.put("mobile",mobile);
        String param = getString(serviceName,jsonparam);
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
        if("-1".equals(resCode)){
            isBilling="1";
            desc = "未知";
        }else if("1".equals(resCode)){
            isBilling="1";
            JSONObject data=result.getJSONObject("data");
            String duration=data.getString("duration");
            desc = "查询成功";
            if("-1".equals(duration)){
                desc += "，未知";
            }else{
                desc = desc + "," + duration;
            }
        }else {
            return ProjectErrorInformation.businessError1(title);
        }
        return resultJson(title,resCode,desc,isBilling);
    }

    /**
     * 获取入网状态
     * @param bizParams
     * @return
     */
    @Override
    public String getPhoneStatus(JSONObject bizParams){
        String title = "获取手机号状态";
        //手机号
        String mobile=bizParams.getString("mobile");
        //接口名
        String serviceName = "getStatus";
        if (StringUtils.isEmpty(mobile)) {
            return ProjectErrorInformation.businessError5(title);
        }
        JSONObject jsonparam = new JSONObject();
        jsonparam.put("mobile",mobile);
        String param = getString(serviceName,jsonparam);
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
        if("-1".equals(resCode)){
            isBilling="1";
            desc = "异常情况";
        }else if("1".equals(resCode)){
            isBilling="1";
            desc = "正常";
        }else if("2".equals(resCode)){
            isBilling="1";
            desc = "停机";
        }else if("3".equals(resCode)){
            isBilling="1";
            desc = "在网但不可用";
        }else if("4".equals(resCode)){
            isBilling="1";
            desc = "不在网";
        }else if("5".equals(resCode)){
            isBilling="1";
            desc = "未启用";
        }else if("6".equals(resCode)){
            isBilling="1";
            desc = "已销号";
        }else if("7".equals(resCode)){
            isBilling="1";
            desc = "未知";
        }else {
            return ProjectErrorInformation.businessError1(title);
        }
        return resultJson(title,resCode,desc,isBilling);
    }



}
