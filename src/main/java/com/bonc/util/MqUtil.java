package com.bonc.util;

import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.service.CreditService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Async
public class MqUtil {
    private final static Logger logger = LoggerFactory.getLogger(MqUtil.class);


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CreditService creditService;

    /**
     * 响应时间，手机号网别入库
     *
     * @param allTime
     * @param uuid
     * @param bizParams
     * @param bb
     */
    public void addRecordTime(Long allTime, String uuid, JSONObject bizParams, Long bb) {
        String finalTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(bb));

        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("all_time", "" + allTime);
        hashMap.put("record_id", uuid);
        hashMap.put("time_type", "upper");
        hashMap.put("phone", bizParams.getString("mobile"));
        hashMap.put("final_time", finalTime);

        //获取手机号
        String phone = hashMap.get("phone").toString();

        //判断网别
        String netType = null;
        netType = creditService.getPhoneProvider(phone);

        if (null == netType) {
            netType = creditService.getPhoneProvider2(phone);
        }
        hashMap.put("netType", netType);
//        try {
            rabbitTemplate.convertAndSend("addRecordTime", hashMap);
//            Thread.sleep(20000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}
