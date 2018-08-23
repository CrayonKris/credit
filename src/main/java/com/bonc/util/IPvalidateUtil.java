package com.bonc.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 本类是获取ip地址的工具类
 * @author zhijie.ma
 * @date 2017年5月2日
 *
 */
public class IPvalidateUtil {
	 /**   
     * 判断客户端的IP是否在某个ip段中   
     * @param clientIp 客户端的IP   
     * @param begin  开始IP   
     * @param end   结束IP   
     * @return   
     */    
    public static boolean isInner(long clientIp, long begin, long end) {     
        return (clientIp >= begin) && (clientIp <= end);     
    }  
    
    /**   
     * 分割IP   
     * @param ipAddress   
     * @return   
     */    
    public static long getIpNum(String ipAddress) {     
        String[] ip = ipAddress.split("\\.");     
        long a = Integer.parseInt(ip[0]);     
        long b = Integer.parseInt(ip[1]);     
        long c = Integer.parseInt(ip[2]);     
        long d = Integer.parseInt(ip[3]);     
        long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;     
        return ipNum;     
    }   
    
    //IP验证
  	public static boolean valiRequestIp(String requestIp, String channelIp) {
  		// TODO Auto-generated method stub
  		boolean flag = true;
  		if("all".equals(channelIp)){
  			flag = true;
  		}
  		String[] channelIps = channelIp.split("\\|");
  		for (int r = 0; r < channelIps.length; r++) {
  			if (channelIps[r].indexOf("-") != -1) {
  				String[] unit = channelIps[r].split("-");
  				if (unit.length == 2) {
  					long ip = getIpNum(requestIp);    
  					long startIp = getIpNum(unit[0]);     
                      long endIp = getIpNum(unit[1]);     
                      if (IPvalidateUtil.isInner(ip, startIp, endIp)) {     
                            flag=false;   
                      }   
  				}
  			}else{
  				if(requestIp.equals(channelIps[r])){
  					flag=false;
  				}
  			}
  		}
  		return flag;
  	}
    
    /***************** 后项订购接口 **********************/
	public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
	}
	
	public static void main(String[] args,HttpServletRequest req) {
		String ipAddr = IPvalidateUtil.getIpAddr(req).split(",")[0];// 访问的ip
	}
	
}

