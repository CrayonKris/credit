package com.bonc;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
public class Test {
	public static void main(String[] args){
		try {
			String mobile = "15148333333";
			String url = "http://www.ip138.com:8080/search.asp?action=mobile&mobile=%s";
			url = String.format(url, mobile);
			Document doc = Jsoup.connect(url).get();
			Elements els = doc.getElementsByClass("tdc2");
//			System.out.println(els.toString());
			System.out.println("归属地：" + els.get(1).text());
			System.out.println("类型：" + els.get(2).text());
			System.out.println("区号：" + els.get(3).text());
			System.out.println("邮编：" + els.get(4).text().substring(0, 6));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test(String mobile){
		
	}
}