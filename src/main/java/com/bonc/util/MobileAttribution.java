package com.bonc.util;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 查询手机号码相关信息               (停用)
 * @author zhijie.ma
 * @date 2017.05.02
 * @注意              本类在本地测试可用，但在linux中无法使用，不太清楚原因           
 */
public class MobileAttribution {
	
	private final static Logger logger = LoggerFactory.getLogger(MobileAttribution.class);
	
	/**
	 *	根据手机号码查询归属地等
	 * @param mobile
	 * @return
	 */
	public static Map<String,String> getMobileInformation(String mobile){
		Map<String, String> map = new HashMap<String,String>();
		try {
			String url = "http://www.ip138.com:8080/search.asp?action=mobile&mobile=%s";
			url = String.format(url, mobile);
			Document doc = Jsoup.connect(url).get();
			Elements els = doc.getElementsByClass("tdc2");
//			System.out.println(els.toString());
//			System.out.println("归属地：" + els.get(1).text());
//			System.out.println("类型：" + els.get(2).text());
//			System.out.println("区号：" + els.get(3).text());
//			System.out.println("邮编：" + els.get(4).text().substring(0, 6));
			
			System.out.println("****************************************");
			System.out.println(doc.toString());
			System.out.println("****************************************");
			System.out.println(els.toString());
			System.out.println("****************************************");
			
			map.put("归属地", els.get(1).text());
			map.put("类型", els.get(2).text());
			map.put("区号", els.get(3).text());
			map.put("邮编", els.get(4).text());
			
			logger.info(mobile + " >>> "+map);
			
			return map;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			map.put("error", "手机号码有误或系统繁忙~~~~");
			logger.info(mobile + " >>> "+map);
			return map;
		}
	}
	
}
