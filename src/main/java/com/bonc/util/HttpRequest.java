package com.bonc.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.RuntimeErrorException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 发送http请求的工具类
 * @author zhijie.ma
 * @date 2017年5月2日
 *
 */
@Component
public class HttpRequest {
	private static Logger logger = Logger.getLogger(HttpRequest.class);
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     *  
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
        	
        	String urlNameString = null;
        	
        	if(param == null || param.equals("")){
        		urlNameString = url;
        	}else{
        		urlNameString = url + "?" + param;
        	}

            URL realUrl = new URL(urlNameString);

            logger.info("***以get请求方式的url为*** "+realUrl);
            
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            logger.info("连接成功！");
            
            
            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            
            
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("【发送GET请求出现异常！】" + e);
//            e.printStackTrace();
            throw new RuntimeException("【发送 GET 请求出现异常！】"+e.getMessage());
//            throw new Exception(e);
            
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     *  
     * @throws IOException 
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            logger.info("***最后请求的URL为***  "+realUrl);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("【发送 POST 请求出现异常！】"+e);
//            e.printStackTrace();
            throw new RuntimeException("【发送 POST 请求出现异常！】"+e);
//            throw new Exception(e);
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 当参数以map传输时，用这个方法成最终的URL，再配合get方式发送结果即可
     * @param paramMap
     * @param baseUrl
     * @return
     * 注意：用此生成的结果为url?param=xxx   所以当用sendGet(url,null)这样使用
     */
    public static String getUrl(Map<String, String> paramMap, String baseUrl){
		StringBuffer sBuffer = new StringBuffer();
		Set<String> keys = paramMap.keySet();
		for(String key : keys){
			String value = paramMap.get(key);
			if(null == value) continue;
			String paramCode = "";
			try {
				paramCode = URLEncoder.encode(value,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			sBuffer.append(""+key+"="+paramCode+"&");		
		}
		String param = StringUtils.substring(sBuffer.toString(), 0, sBuffer.toString().length()-1);
		
		String url = baseUrl+"?"+param;
		return url;
	}
    
    /**
	 * 将map里的key,value 格式化为URL参数
	 * @param params
	 * @return
	 */
	public static String getUrlParams(Map<String, String> params) {
		StringBuffer buff = new StringBuffer();
		Collection<String> keyset = params.keySet();
		List<String> list = new ArrayList<String>(keyset);

		Collections.sort(list);
		int len = list.size();
		for (int i = 0; i < len; i++) {
			String key = list.get(i);
			String value = params.get(key);
			if(null == value) value = ""; 
			buff.append(key).append("=").append(value);
			if(i < (len -1)) buff.append("&");
		}
		return buff.toString();
	}
	
	
	public static String getUrlParamsObject(Map<String, Object> params) {
		StringBuffer buff = new StringBuffer();
		Collection<String> keyset = params.keySet();
		List<String> list = new ArrayList<String>(keyset);

		Collections.sort(list);
		int len = list.size();
		for (int i = 0; i < len; i++) {
			String key = list.get(i);
			String value = (String) params.get(key);
			if(null == value) value = ""; 
			buff.append(key).append("=").append(value);
			if(i < (len -1)) buff.append("&");
		}
		return buff.toString();
	}
}