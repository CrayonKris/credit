package com.bonc.credit.service.liantong;

import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhangyixuan
 * @Date: 2018/8/20 9:46
 */
@Service
public class LiantongInterfaceRegist {

    @Autowired
    LiantongService liantongService;

    @Autowired
    LiantongServiceByZyx liantongServiceByZyx;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String distributeInterfaceRegist(String providerCode, JSONObject bizParams, String uuid) {
        String result=null;
        Long aa=System.currentTimeMillis();
        if (("CSM201808201015").equals(providerCode)) {
            //简版三要素
            result=liantongServiceByZyx.getSimpleThreeElements(bizParams);
        } else if ("CSM201808201016".equals(providerCode)){
            //三要素验证详版，MD5
            result=liantongService.getThreeElementsMd5(bizParams);
        } else if ("CSM201808201017".equals(providerCode)){
            //三要素验证详版，第二版
            result=liantongService.getThreeElementsMd5V2(bizParams);
        } else if ("CSM201808201018".equals(providerCode)){
            //手机号姓名核查
            result=liantongService.getMobileName(bizParams);
        } else if ("CSM201808201019".equals(providerCode)){
            //手机号证件核查
            result=liantongServiceByZyx.getMobileCard(bizParams);
        } else {
            JSONObject jsonObject=new JSONObject();
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
