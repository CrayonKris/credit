package com.bonc.credit.controller;

import com.bonc.credit.service.CreditService;
import com.bonc.credit.service.shuzun.ShuZunServiceImpl;
import com.bonc.util.IPvalidateUtil;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 风控数据v2.0 入口类
 * @author zhijie.ma
 * @date 2017年5月2日
 *
 */
@RestController
@RequestMapping("bonc")
public class CreditController {
	@Autowired
	private CreditService creditService;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private static final Logger logger = Logger.getLogger(ShuZunServiceImpl.class);


	/**
	 * 征信用户信息查询--对外服务
	 * 
	 * @param request
	 * @return
	 * @ 
	 */
	@RequestMapping(value = "/queryinfo.json", method = { RequestMethod.GET, RequestMethod.POST })
	public String outerUse(HttpServletRequest request)  {
		String ipAddr = IPvalidateUtil.getIpAddr(request).split(",")[0];// 访问的ip
		String method = request.getParameter("method");
		String bizParams = request.getParameter("bizParams");
		String account = request.getParameter("account");
		String token = request.getParameter("token");
		String uuid = UUID.randomUUID().toString();
		Long aa = System.currentTimeMillis();
		String str = creditService.vilidateChannel(bizParams, method, account, token, ipAddr,uuid);
		Long bb = System.currentTimeMillis();
		Long allTime=bb-aa;
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("all_time",""+allTime);
		hashMap.put("record_id",uuid);
		hashMap.put("time_type","all");
		rabbitTemplate.convertAndSend("addRecordTime", hashMap);
		return str;
	}

	/**征信用户信息查询--对内服务
	 * @param method
	 * @param bizParams
	 * @return
	 */
	@RequestMapping(value = "/query/innerBonc",method = {RequestMethod.GET,RequestMethod.POST})
//	public  String innerUse(HttpServletRequest request) {
//
//		try {
//			request.setCharacterEncoding("UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String method = request.getParameter("method");
//		String bizParams = request.getParameter("bizParams");
//
//		return creditService.vilidateInformation(bizParams, method, "bonc");
//	}
	public String innerUse(@PathParam("method") String method,@PathParam("bizParams") String bizParams){
		String uuid= UUID.randomUUID().toString();
		Long aa = System.currentTimeMillis();
		String str=creditService.vilidateInformation(bizParams,method,"bonc",uuid);
		Long bb = System.currentTimeMillis();
		Long allTime=bb-aa;
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("all_time",""+allTime);
		hashMap.put("record_id",uuid);
		hashMap.put("time_type","all");
		rabbitTemplate.convertAndSend("addRecordTime", hashMap);
		return str;
	}

	/**
	 * 生成token
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "token.json", method = { RequestMethod.GET, RequestMethod.POST })
	public String produceToken(HttpServletRequest request) {
		String ipAddr = IPvalidateUtil.getIpAddr(request).split(",")[0];// 访问的ip
		String account = request.getParameter("account");
		String appKey = request.getParameter("appKey");
		return creditService.vilidateChannelToken(account, appKey, ipAddr);
	}

	@RequestMapping(value="/query/innerBoncTest",method = {RequestMethod.GET,RequestMethod.POST})
	public String testController(){
		return "{\"resCode\":\"0000\",\"resMsg\":\"请求成功\",\"tranNo\":null,\"sign\":\"D123F1CC7378B91FE646633A74444BDC\",\"data\":[{\"statusCode\":1,\"statusMsg\":\"查询成功,查得结果\",\"quotaID\":\"MD004\",\"quotaValue\":\"1\",\"channel\":1}]}";
	}

}
