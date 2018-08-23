package com.bonc.credit.service.tianyizhengxin;

import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.service.zhongchengxin.ZhongChengXinInterfaceRegist;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Miracle
 * @Date: 2018/8/7 16:02
 */
@Service
public class TianYiInterfaceRegist {

    private static final Logger logger = Logger.getLogger(ZhongChengXinInterfaceRegist.class);

    @Autowired
    TianYiService tianYiService;

    @Autowired
    TianYiServiceByZyx tianYiServiceByZyx;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String distributeInterfaceRegist(String providerCode, JSONObject bizParams, String uuid) {
        String result=null;
        Long aa=System.currentTimeMillis();
        if ("BONC2018081714".equals(providerCode)) {
            //获取工作信息
            result = tianYiService.getJob(bizParams);
        } else if ("BONC2018081715".equals(providerCode)) {
            //获取家庭信息
            result = tianYiService.getJob(bizParams);
        } else if ("BONC2018081716".equals(providerCode)) {
            //全网二要素验证，手机号+姓名
            result = tianYiService.getJob(bizParams);
        } else if ("BONC2018081717".equals(providerCode)) {
            //全网二要素验证，手机号+身份证
            result = tianYiService.getJob(bizParams);
        } else if ("BONC2018081718".equals(providerCode)) {
            //公安身份验证
            result = tianYiService.getJob(bizParams);
        } else if ("BONC2018081614".equals(providerCode)) {
            //全网运营商三要素验证，详版
            result = tianYiService.getMobilecardInfo(bizParams);
        } else if ("BONC2018081720".equals(providerCode)) {
            //全网运营商三要素验证，简版
            result = tianYiService.getJob(bizParams);
        } else if ("BONC2018081722".equals(providerCode)) {
            //银行卡四要素验证
            result = tianYiService.getJob(bizParams);
        } else if ("BONC2018081723".equals(providerCode)) {
            //银行卡五要素验证
            result = tianYiService.getJob(bizParams);
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("interface", "");
            jsonObject.put("code", "B0003");
            jsonObject.put("desc", "接口未知错误！请联系管理员！！！");
            jsonObject.put("isbilling", "0");
            result = jsonObject.toString();
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
