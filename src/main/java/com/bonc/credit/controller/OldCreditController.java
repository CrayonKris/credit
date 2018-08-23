package com.bonc.credit.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bonc.credit.service.CreditService;
import com.bonc.util.IPvalidateUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 	老用户专用接口	风控数据v1.0 入口类
 * @author zhijie.ma
 * @date 2017年5月2日
 *
 */
@RestController
@RequestMapping("v4")
public class OldCreditController {
	@Autowired
	private CreditService creditService;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * 旧版客户调用验证方法
	 * 		新注册用户请用新版本项目
	 * 征信用户信息查询--对外服务
	 * 		
	 * 		http://ip:port/openapi/v4/queryinfo.jsonp?
	 * appKey=${appKey}&token=${token}&method= CSM170425_1455&bizParams={"mobile":${mobile},"month":${month}}
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryinfo.json", method = { RequestMethod.GET, RequestMethod.POST })
	public String outerUse(HttpServletRequest request){
		String ipAddr = IPvalidateUtil.getIpAddr(request).split(",")[0];// 访问的ip
		String method = request.getParameter("method");
		String bizParams = request.getParameter("bizParams");
		String account = request.getParameter("appKey");
		String token = request.getParameter("token");
		String uuid = UUID.randomUUID().toString();
		Long aa = System.currentTimeMillis();
		String str = creditService.oldVilidateChannel(bizParams, method, account, token, ipAddr, uuid);
		Long bb = System.currentTimeMillis();
		Long allTime=bb-aa;
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("all_time",""+allTime);
		hashMap.put("record_id",uuid);
		hashMap.put("time_type","all");
		rabbitTemplate.convertAndSend("addRecordTime", hashMap);
		return str;
	}
	
}


@RestController
@RequestMapping("v3")
class OldCreditController2 {
	@Autowired
	private CreditService creditService;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * 旧版客户调用验证方法
	 * 		新注册用户请用新版本项目
	 * 征信用户信息查询--对外服务
	 * 		
	 * 		http://ip:port/openapi/v3/queryinfo.json?
	 * appKey=${appKey}&token=${token}&method= CSM170425_1455&bizParams={"mobile":${mobile},"month":${month}}
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryinfo.json", method = { RequestMethod.GET, RequestMethod.POST })
	public String outerUse(HttpServletRequest request) {
		String ipAddr = IPvalidateUtil.getIpAddr(request).split(",")[0];// 访问的ip
		String method = request.getParameter("method");
		String bizParams = request.getParameter("bizParams");
		String account = request.getParameter("appKey");
		String token = request.getParameter("token");
		String uuid = UUID.randomUUID().toString();
		Long aa = System.currentTimeMillis();
		String str=creditService.oldVilidateChannel(bizParams, method, account, token, ipAddr, uuid);
		Long bb = System.currentTimeMillis();
		Long allTime=bb-aa;
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("all_time",""+allTime);
		hashMap.put("record_id",uuid);
		hashMap.put("time_type","all");
		rabbitTemplate.convertAndSend("addRecordTime", hashMap);
		return str;
	}
	@RequestMapping(value = "/querytest.json", method = { RequestMethod.GET, RequestMethod.POST })
	public String outerUse2(HttpServletRequest request) {
		String ipAddr = IPvalidateUtil.getIpAddr(request).split(",")[0];// 访问的ip
		String method = request.getParameter("method");
		String bizParams = request.getParameter("bizParams");
		String account = request.getParameter("appKey");
		String uuid = UUID.randomUUID().toString();
		Long aa = System.currentTimeMillis();
		String str=creditService.oldVilidateChannelII(bizParams, method, account, ipAddr, uuid);;
		Long bb = System.currentTimeMillis();
		Long allTime=bb-aa;
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("all_time",""+allTime);
		hashMap.put("record_id",uuid);
		hashMap.put("time_type","all");
		rabbitTemplate.convertAndSend("addRecordTime", hashMap);
		return str;
	}
	
}
