package com.bonc.credit.service.baichuan;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.MqUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaiChuanInterfaceRegist {
    @Autowired
    private BaiChuanService baiChuanService;
    @Autowired
    private BaiChuanServiceByZyx baiChuanServiceByZyx;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    MqUtil mqUtil;

    public String distributeInterfaceRegist(String providerCode, JSONObject bizParams, String uuid) {
        JSONObject jsonObject = new JSONObject();
        String result=null;
        Long aa=System.currentTimeMillis();
        if ("CSM180801_1531".equals(providerCode)){
            //银行3元素验证
            result=baiChuanService.getVerifyBankcard3(bizParams);
        } else if ("CSM180801_1532".equals(providerCode)){
            //银行4元素验证
            result=baiChuanService.getVerifyBankcard4(bizParams);
        } else if ("CSM180801_1533".equals(providerCode)){
            //在网时长
            result=baiChuanServiceByZyx.getTimeByPhone(bizParams);
        } else if ("CSM180801_1534".equals(providerCode)){
            //手机号状态
            result= baiChuanServiceByZyx.getPhoneStatus(bizParams);
        } else if ("CSM180801_1535".equals(providerCode)){
            //验证手机号身份证姓名是否一致
            result=baiChuanService.getMobilecardInfo(bizParams);
        } else if ("CSM180801_1536".equals(providerCode)){
            //验证身份证姓名是否一致
            result=baiChuanServiceByZyx.getCardNameInfo(bizParams);
        } else if ("CSM180801_1537".equals(providerCode)){
            //验证学历
            result=baiChuanServiceByZyx.getXueli(bizParams);
        } else {
            jsonObject.put("interface", "");
            jsonObject.put("code", "B0003");
            jsonObject.put("desc", "接口未知错误！请联系管理员！！！");
            jsonObject.put("isbilling", "0");
            result=jsonObject.toString();
        }
        Long bb=System.currentTimeMillis();
        Long allTime=bb-aa;

        mqUtil.addRecordTime(allTime,uuid,bizParams,bb);
        return result;
    }
}
