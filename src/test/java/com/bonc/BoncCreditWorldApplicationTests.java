//package com.bonc;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.alibaba.fastjson.JSONObject;
//import com.bonc.credit.controller.CreditController;
//import com.bonc.credit.mapper.CreditMapper;
//import com.bonc.credit.service.CreditService;
//import com.bonc.credit.service.tianchuang.TianChuangService;
//import com.bonc.redis.RedisUtil;
//import com.bonc.util.HttpRequest;
//import com.bonc.util.MD5Builder;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class BoncCreditWorldApplicationTests {
//
////	@Value("${bdcsc.host}")
////	public String HOST;
//	@Autowired
//	private CreditMapper creditMapper;
//
//	@Autowired
//	private RedisUtil redisUtil;
//
//	@Autowired
//	private TianChuangService tianChuangService;
//
//	@Autowired
//	private CreditService creditService;
//
//	@Autowired
//	private CreditController creditController;
//
//	@Test
//	public void contextLoads() {
////		String cityName = creditMapper.getCityName("1301");
////		System.out.println(cityName);
////
////		String provinceName = creditMapper.getProvinceName("15");
////		System.out.println(provinceName);
//
//		List<Map<String,String>> interfaceInformation = null;
//		try {
//			interfaceInformation = creditMapper.getInterfaceInformation("CSM170719_1658", "LT");
//			for (Map<String, String> map : interfaceInformation) {
//				System.out.println(map.toString());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("try catch 数据为空");
//		}
//		if(interfaceInformation.isEmpty()){
//			System.out.println("空数据");
//		}
//	}
//
//	@Test
//	public void testRedis(){
////		redisUtil.setString("wo", "是帅哥");
//		String string = redisUtil.getString("wo");
//		System.out.println(string);
//		System.out.println("***************");
//
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("111", "222");
//		map.put("333", "444");
//
////		redisUtil.setObject("sss", map);
//
////		Map<String,String> getMap = (Map<String, String>) redisUtil.getObject("sss");
////		System.out.println(getMap);
//
////		redisUtil.deleteObject("fst");
//		System.out.println("***************");
//
////		redisUtil.setObjectTime("fst", "222", 10);
////		Object string2 =  redisUtil.getObject("fst");
////		System.out.println("fst的：》》"+string2);
////		redisUtil.setStringTime("fst", "大傻子", 20);
//
//		String string2 = redisUtil.getString("fst");
//		System.out.println(string2);
////		redisUtil.setStringTime("fst", "大傻子222", 20);
//	}
//
//	@Test
//	public void testProperties() throws Exception{
//		String url = "http://san.511860.com/credit/BONC_CreditWorld/bonc/query/innerBonc";
//		String param = "method=verifyUserName&encodeURI(bizParams={\"mobile\":\"18946302001\",\"userName\":\"闫莹碧\"})";
//
//		String sendGet = HttpRequest.sendGet(url, param);
//		System.out.println(sendGet);
//	}
//
//	@Test
//	public void testAddChannel(){
//		Map<String,Object> map = new HashMap<String,Object>();
//		map.put("channel_id", "1");
//		map.put("channel_code", "Q1");
//		map.put("channel_name", "BigData");
//		map.put("account", "qq");
//		map.put("password", "qqq");
//		map.put("appKey", "qq");
//		map.put("linkman", "qq");
//		map.put("phone", "123");
//		map.put("create_time", new Date());
//		map.put("check_status", "1");
//		map.put("channel_status", "1");
//		map.put("website_name", "aa");
//		map.put("website_url", "aa.com");
//		map.put("corporate", "aaa");
//		map.put("licence_number", "123");
//		map.put("email", "email");
//		map.put("address", "address");
//		map.put("note", "note");
//		map.put("QQ", "qq123");
//
//		Map<String,Object> map1 = new HashMap<String,Object>();
//		map1.put("account", "qq");
////		creditMapper.addChannel(map);
//		Map<String, Object> channelByAccount = creditMapper.getChannelByAccount(map1);
//		System.out.println(channelByAccount);
//	}
//
//	@Test
//	public void test(){
//		Map<String,Object> map = new HashMap<String,Object>();
//		map.put("channel_id", "1");
//		map.put("interface_id", "1");
//		int selectVisitCount = creditMapper.selectVisitCount(map);
//		System.out.println(selectVisitCount);
//		map.put("access_count", selectVisitCount-1);
//		creditMapper.updateVisitCount(map);
//
//	}
//
//	@Test
//	public void test1(){
////		Map<String, String> productInformation = creditMapper.getProductInformation("CSM161223_1645");
////		System.out.println(productInformation);
//		HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
//		hashMap2.put("channel_code", "Q10001");
//		hashMap2.put("product_number", "P10029");
//		Integer selectVisitCount = creditMapper.selectVisitCount(hashMap2);
//		if(selectVisitCount== null){
//			System.out.println("这是一个陷阱");
//		}
//		System.out.println(selectVisitCount);
//	}
//
//	@Test
//	public void test2(){
//		System.out.println(redisUtil.aa("gwm", "3"));
//	}
//
//	@Test
//	public void test3() throws Exception{
//		JSONObject jsonObject = new JSONObject();
////		jsonObject.put("mobile", "15369110314");
//		jsonObject.put("idcard", "152501196909230031");
//		jsonObject.put("name", "赵亮");
//		System.out.println(jsonObject);
////		String onlineTime = tianChuangService.getOnlineTime(jsonObject);
////		String state = tianChuangService.getState(jsonObject);
////		String verifyMobileInfo = tianChuangService.getVerifyMobileInfo(jsonObject);
//		String verifyIdcardC = tianChuangService.getVerifyIdcardC(jsonObject);
////		String verifyIdcard = tianChuangService.getVerifyIdcard(jsonObject);
//
//		System.out.println(verifyIdcardC);
//	}
//
//	@Test
//	public void test4() throws Exception{
//		String bizParams = "{\"idcard\":\"152501196909230031\",\"name\":\"赵亮\"}";
//		String vilidateInformation = creditService.vilidateInformation(bizParams, "CSM170719_1660", "bonc");
//		System.out.println(vilidateInformation);
//	}
//
//	@Test
//	public void test5(){
//		String bizParams = "{\"idcard\":\"152501196909230031\",\"name\":\"赵亮\"}";
//		String channelToken = MD5Builder.md5("9999" + "01ef28c6aba9c3383d91236452b85683" + "CSM170719_1660" + bizParams);
//		System.out.println(channelToken);
//	}
//
//	public static void main(String[] args) {
//		String bizParams = "{mobile:\"15132866892\",idCard:\"131102199210101061\",userName:\"郭琪琪\"}";
////		JSONObject jsonObject = new JSONObject();
////		jsonObject.put("idcard", "152501196909230031");
////		jsonObject.put("name", "赵亮");
//		String channelToken = MD5Builder.md5("9999" + "01ef28c6aba9c3383d91236452b85683" + "CSN300003" + bizParams);
//		System.out.println(channelToken);
//	}
//
//	@Test
//	public void test6() throws Exception{
//		String bizParams = "{mobile:\"15132866892\",idCard:\"131102199210101061\",userName:\"郭琪琪\"}";
//
//		//String vilidateChannel = creditService.vilidateChannel(bizParams, "CSM170719_1660", "test", "963cc83b8a22268e6773c863c3ce7404", "39.155.134.149");
//		String vilidateChannel = creditService.oldVilidateChannel(bizParams, "CSN300003", "9999", "1bb3ebd3995d296c06ab7a93885cb0e9", "39.155.134.149");
//		System.out.println(vilidateChannel);
//	}
//
//	@Test
//	public void test7() throws Exception{
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("idcard", "152501196909230031");
//		jsonObject.put("name", "赵亮");
//		Map<String,String> hashMap = new HashMap<String,String>();
//		hashMap.put("appKey", "9999");
//		hashMap.put("method", "CSN300003");
//		hashMap.put("token", "5f1e130854b0d24972ca96014daf0abe");
//		hashMap.put("bizParams", "{mobile:\"15132866892\",idCard:\"131102199210101061\",userName:\"郭琪琪\"}");
//		//String urlParamsObject = HttpRequest.getUrlParamsObject(hashMap);
//		String urlParams = HttpRequest.getUrlParams(hashMap);
//		String sendGet = HttpRequest.sendGet("http://localhost:8080/v4/queryinfo.jsonp", urlParams);
//		System.out.println(sendGet);
//	}
//
//}
