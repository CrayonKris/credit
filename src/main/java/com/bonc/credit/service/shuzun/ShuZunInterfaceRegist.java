package com.bonc.credit.service.shuzun;

import com.bonc.credit.service.zhongchengxin.ZhongchengxinServicePart1;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 数尊接口注册类
 *
 * @author zhijie.ma
 * @date 2018年6月20日
 */
@Service
public class ShuZunInterfaceRegist {

    @Autowired
    private ShuZunService shuZunService;

    @Autowired
    private ShuZunServiceByZyx shuZunServiceByZyx;

    @Autowired
    private ZhongchengxinServicePart1 zhongchengxinServicePart1;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String distributeInterfaceRegist(String providerCode, JSONObject bizParams, String uuid) {

        JSONObject jsonObject = new JSONObject();
        String result=null;
        Long aa=System.currentTimeMillis();
        if (providerCode.equals("CSM180624_1146")) {

            //验证姓名、身份证号、手机号三者之间的匹配结果  	全网通
            result=shuZunService.getVerifyMobileInfo(bizParams);

        } else if (providerCode.equals("CSM180624_1148")) {

            // 电信定制-常用联系人
            result=shuZunService.getTelecomContacts(bizParams);

        } else if (providerCode.equals("CSM180624_1317")) {

            // 联通定制-常用联系人
            result=shuZunService.getUnicomContacts(bizParams);

        } else if (providerCode.equals("CSM180624_1506")) {

            // 运营商在网时长     全网通
            result=shuZunService.getOnlineTime(bizParams);

        } else if (providerCode.equals("CSM180624_1508")) {

            // 运营商状态     全网通
            result=shuZunService.getUserState(bizParams);

        } else if ("CSM180624_1510".equals(providerCode)) {
            // 电信定制-手机号码交往圈大小（得分）
            result=shuZunService.getTelecomCircle(bizParams);
        } else if ("CSM180624_1511".equals(providerCode)) {
            // 联通定制-手机号码交往圈大小（得分）
            result=shuZunService.getUnicomCircle(bizParams);
        } else if ("CSM180706_1628".equals(providerCode)) {

            // 验证姓名、身份证号、手机号是否匹配(完整版)
//            result=shuZunService.getVerifyMobileInfoV(bizParams);
            //20180806修改数尊三元素，更改上游为中诚信
            result=shuZunService.getVerifyVerificationIII(bizParams);
        }  else if ("CSM180706_1731".equals(providerCode)) {
            // 验证姓名-身份证
//            result=shuZunService.getVerifyIdentifyInfo(bizParams);
            //验证两元素更改为百川接口
//            result=shuZunServiceByZyx.getBaichuanInfo(bizParams);
            //验证两元素更改为中诚信接口
            result=shuZunServiceByZyx.getZhongchengxinInfo(bizParams);

        } else if ("CSM180725_1601".equals(providerCode)) {
            // 工作地址位置验证
            result=shuZunService.getWorkAddressPosition(bizParams);
        } else if ("CSM180725_1600".equals(providerCode)){
            //验证联通定制-经纬度验证    zyx
            result=shuZunServiceByZyx.getLatitudeAndLongitude(bizParams);
        } else if ("CSM180725_1700".equals(providerCode)){
            //手机号当前停留城市验证
            result=shuZunService.getCurrentStayCity(bizParams);
        } else if ("CSM180725_1800".equals(providerCode)){
            //居住地址位置验证
            result=shuZunServiceByZyx.getResidentialAddress(bizParams);
        } else if ("CSM180725_1900".equals(providerCode)){
            //联通定制-是否二次放号查询  zyx
            result= shuZunServiceByZyx.getTwoNumbers(bizParams);
        }else if ("CSM180706_1729".equals(providerCode)) {

            // 验证姓名、身份证号、手机号是否匹配(完整版)上游为数尊
            result=shuZunService.getVerifyMobileInfoV(bizParams);
        }

        else {
            jsonObject.put("interface", "");
            jsonObject.put("code", "B0003");
            jsonObject.put("desc", "接口未知错误！请联系管理员！！！");
            jsonObject.put("isbilling", "0");
            result=jsonObject.toString();
        }
        Long bb=System.currentTimeMillis();
        Long allTime=bb-aa;
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("all_time",""+allTime);
        hashMap.put("record_id",uuid);
        hashMap.put("time_type","upper");
        rabbitTemplate.convertAndSend("addRecordTime", hashMap);
        return result;
    }

}
