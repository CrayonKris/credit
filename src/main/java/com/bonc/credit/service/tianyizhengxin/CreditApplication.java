package com.bonc.credit.service.tianyizhengxin;

import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.pojo.Credit;
import com.bonc.credit.pojo.Header;
import com.bonc.credit.pojo.Top;
import com.bonc.util.AesEncrypt;
import com.bonc.util.HttpClientUtil;
import com.bonc.util.JacksonUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 天翼征信接口入口
 */
public class CreditApplication {

    /**
     * 日期型：如20151008
     */
    public final static String DATE_FORMATE_YYYYMMDD = "yyyyMMdd";
    /**
     * 日期时间型：如 20151008183212
     */
    public final static String DATE_FORMATE_YYYYMMDDHHSS = "yyyyMMddHHmmss";

    private static final Logger logger = Logger.getLogger(CreditApplication.class);

    /**
     * 服务接口
     * @param url 请求路径
     * @param testFlag 测试标记
     * @param reqSys 发起方机构代码
     * @param reqTransID 发起方交易流水号32位不重复
     * @param authorizationCode 授权号
     * @param bodyMap 报文body体
     * @param key 秘钥申请接口获得的home_key
     * @return 解密后响应报文
     */
    public static String invoke(String url, Integer testFlag, String reqSys, String reqTransID, String authorizationCode, Map bodyMap , String key) {

        String resInfo= "";

        Top top = getTop(testFlag,reqSys,reqTransID,authorizationCode);
        top.getCredit().getHeader().setActivityCode("1005");
        try {
            String encryptStr = AesEncrypt.encrypt(JacksonUtil.bean2Json(bodyMap), key);
            top.getCredit().setBody(encryptStr);
           resInfo = send(top,url,key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resInfo;
    }

    /**
     * 秘钥申请接口
     * @param url 请求路径
     * @param testFlag 测试标记
     * @param reqSys 发起方机构代码
     * @param reqTransID 发起方交易流水号32位不重复
     * @param authorizationCode 授权号
     * @param bodyMap 报文body体
     * @param key 天翼征信提供的home_key
     * @return 解密后响应报文
     */
    public static String privateKeyInvoke(String url, Integer testFlag, String reqSys, String reqTransID, String authorizationCode, Map bodyMap , String key) {

        String resInfo= "";
        Top top = getTop(testFlag,reqSys,reqTransID,authorizationCode);
        top.getCredit().getHeader().setActivityCode("1001");
        JSONObject body = JSONObject.parseObject(JacksonUtil.bean2Json(bodyMap));
        top.getCredit().setBody(body);
        resInfo = send(top,url,key);
        return resInfo;
    }

    /**
     *
     * @param top 请求体
     * @param url 请求路径
     * @param key 秘钥信息
     * @return
     */
    private static String send(Top top, String url, String key){
        String resInfo= "";

        try {
            String macStr = "\"header\":"+ JacksonUtil.bean2Json(top.getCredit().getHeader())+
                    ",\"body\":"+JacksonUtil.bean2Json(top.getCredit().getBody());
            top.getCredit().setMac(DigestUtils.md5Hex(macStr));
//            System.err.println(macStr);
            String context = JacksonUtil.bean2Json(top);
//            System.err.println(context);
            logger.info("请求报文："+context);
            //发送post请求
            logger.info("完整的请求URL："+url);
            String res = HttpClientUtil.sendByPost(url,context,HttpClientUtil.CONTENT_TYPE_APPLICATION_JSON);
            Top topRes = JSONObject.parseObject(res,Top.class);
            String rspCode = topRes.getCredit().getHeader().getRspCode();
            if(!rspCode.equals("00000")&&!rspCode.equals("000000")){
                resInfo = res;
            }else{
                if(top.getCredit().getHeader().getActivityCode().equals("1001")){
                    String transKey = ((JSONObject)topRes.getCredit().getBody()).getString("transKey");
                    String mackey = AesEncrypt.decrypt(transKey, key);
                    ((JSONObject)topRes.getCredit().getBody()).put("transKey",mackey);

                }else {
                    String bodyRes = (String)topRes.getCredit().getBody();
                    String body = AesEncrypt.decrypt(bodyRes, key);
                    System.err.println("结果body>> :"+body);
                    topRes.getCredit().setBody(JSONObject.parseObject(body,Map.class));
                }
                resInfo = JacksonUtil.bean2Json(topRes);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resInfo;
    }

    /**
     *
     * @param testFlag 测试标记
     * @param reqSys 发起方机构代码
     * @param reqTransID 发起方交易流水号32位不重复
     * @param authorizationCode 授权号
     * @return
     */
    private static Top getTop(Integer testFlag, String reqSys, String reqTransID, String authorizationCode){
        Top top = new Top();
        Header header = new Header();
        try {
            DateFormat dfData = new SimpleDateFormat(DATE_FORMATE_YYYYMMDD);
            DateFormat dfDataTime = new SimpleDateFormat(DATE_FORMATE_YYYYMMDDHHSS);
            header.setVersion("0100");

            header.setReqSys(reqSys);
            header.setTestFlag(testFlag);
            header.setAuthorizationCode(authorizationCode);
            header.setActionCode(0);
            header.setReqChannel("0");
            header.setReqTransID(reqTransID);
            header.setReqDate(dfData.format(new Date()));
            header.setReqDateTime(dfDataTime.format(new Date()));

            /**
             * 由于响应信息是非必填项，可有可无
             */

            Credit credit = new Credit();
            credit.setHeader(header);
            top.setCredit(credit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return top;
    }

}
