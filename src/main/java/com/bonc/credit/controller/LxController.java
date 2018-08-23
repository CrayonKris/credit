package com.bonc.credit.controller;

import com.bonc.credit.service.CreditService;
import com.bonc.util.IPvalidateUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author zj
 * @date 2018/7/6 18:09
 */
@RestController
@RequestMapping("/lx")
public class LxController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(value = "/queryinfo.json", method = {RequestMethod.POST, RequestMethod.GET})
    public String query(@PathParam("method") String method,
                        @PathParam("bizParams") String bizParams,
                        @PathParam("appKey") String account,
                        @PathParam("token") String token) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ipAddr = IPvalidateUtil.getIpAddr(request).split(",")[0];// 访问的ip
//        String result = creditService.vilidateInformation(bizParams, method, "bonc");
        String uuid = UUID.randomUUID().toString();
        Long aa=System.currentTimeMillis();
        String str = creditService.oldVilidateChannel(bizParams, method, account, token, ipAddr,uuid);
        Long bb = System.currentTimeMillis();
        Long allTime=bb-aa;
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("all_time",""+allTime);
        hashMap.put("record_id",uuid);
        hashMap.put("time_type","all");
        rabbitTemplate.convertAndSend("addRecordTime", hashMap);
        return str;
    }

    @GetMapping
    public String test() {
        return "welcome!!!";
    }
}
