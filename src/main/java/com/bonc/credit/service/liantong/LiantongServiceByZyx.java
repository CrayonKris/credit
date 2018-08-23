package com.bonc.credit.service.liantong;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.MD5Builder;
import com.bonc.util.ProjectErrorInformation;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhangyixuan
 * @Date: 2018/8/20 9:48
 */
@Service
public class LiantongServiceByZyx {

    private static Logger logger = Logger.getLogger(LiantongService.class);

    /**
     * 运营商三要素简版
     * @param bizParams
     * @return
     */
    public String getSimpleThreeElements(JSONObject bizParams){
        String title = "运营商三要素简版";
        String method = "check/newUserCheck.do";
        //手机号
        String sendTelNo = bizParams.getString("mobile");
        //证件类型
        String certType = bizParams.getString("idType");
        //用户姓名
        String userName = bizParams.getString("userName");
        //证件号码
        String certCode = bizParams.getString("idCard");

        //参数检验
        if (StringUtils.isEmpty(sendTelNo)||StringUtils.isEmpty(userName)|| StringUtils.isEmpty(certType)||
                StringUtils.isEmpty(certCode)){
            return ProjectErrorInformation.businessError5(title);
        }
        if (!("0101".equals(certType)||"0102".equals(certType)||"0103".equals(certType)||
                "0104".equals(certType)||"0105".equals(certType)||"0106".equals(certType)||
                "0107".equals(certType)||"0199".equals(certType)||"0999".equals(certType))){
            return ProjectErrorInformation.businessError5(title);
        }
        String name ="";
        try {
            name = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码错误");
        }
        String code ="B0001";
        String desc = "调用失败";
        String isbilling = "0";
        Map<String, String> map = new HashMap<>();
        map.put("sendTelNo", sendTelNo);
        map.put("certType", certType);
        map.put("certCode", certCode);
        map.put("userName", name);

        Long a = System.currentTimeMillis();
        String response = LiantongHelper.getRes(method,map);
        Long b =System.currentTimeMillis();
        logger.info("上游响应时间："+(b-a));

        if (StringUtils.isEmpty(response)){
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info("response >> "+response);
        JSONObject jsonres = JSONObject.parseObject(response);
        String status = (String) jsonres.get("status");
        //错误状态码
        String errCode = (String) jsonres.get("code");
        if("1".equals(status)){
            if("00".equals(errCode)){
                //成功
                String checkResult = (String) jsonres.get("checkResult");
                isbilling="1";
                if("00".equals(checkResult)){
                    //验证一致
                    code="0";
                    desc="一致";
                } else if ("01".equals(checkResult)){
                    //验证不一致
                    code="1";
                    desc="不一致";
                }
            } else if ("14".equals(errCode)){
                // 空号、非联通运营商号码、已注销号码、隐私宝号码、携号转网号码、
                // 新入网号码数据未来得及更新情况下会返回这个错误码
                isbilling="0";
                code="10";
                desc="号码不存在";
            }

        } else if ("2".equals(status)){
            isbilling="0";
            //失败
            if("01".equals(errCode)||"02".equals(errCode)){
                //参数校验失败，01表示参数不能为空，02表示格式出错
                return ProjectErrorInformation.businessError5(title+errCode);
            } else {
                // 上游其他错误，03服务器错误，04连接超时，05数据账期不存在，09机构验证错误，
                // 10ip无权访问，11机构秘钥错误，13余额不足，15序列号重复，21解密失败
                ProjectErrorInformation.businessError4(title+errCode);
            }

        }

        JSONObject ret = new JSONObject();
        ret.put("interface", title);
        ret.put("code", code);
        ret.put("desc", desc);
        ret.put("isbilling", isbilling);
        return ret.toString();
    }

    /**
     * 手机号证件核查
     * @param bizParams
     * @return
     */
    public String getMobileCard(JSONObject bizParams){
        String title = "手机号证件核查";
        String method = "check/identityVal.do";
        //手机号
        String sendTelNo = bizParams.getString("mobile");
        //证件号码
        String certCode = bizParams.getString("idCard");

        //参数检验
        if (StringUtils.isEmpty(sendTelNo)|| StringUtils.isEmpty(certCode)){
            return ProjectErrorInformation.businessError5(title);
        }
        String code ="B0001";
        String desc = "调用失败";
        String isbilling = "0";
        Map<String, String> map = new HashMap<>();
        map.put("sendTelNo", sendTelNo);
        map.put("certCode", certCode);

        Long a = System.currentTimeMillis();
        String response = LiantongHelper.getRes(method,map);
        Long b =System.currentTimeMillis();
        logger.info("上游响应时间："+(b-a));

        if (StringUtils.isEmpty(response)){
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info("response >> "+response);
        JSONObject jsonres = JSONObject.parseObject(response);
        //上游成功失败状态码，1成功，2失败
        String status = (String) jsonres.get("status");
        //错误状态码
        String errCode = (String) jsonres.get("code");
        if("1".equals(status)){
            if("00".equals(errCode)){
                //成功
                String certResult = (String) jsonres.get("certResult");
                isbilling="1";
                if("0".equals(certResult)){
                    //验证一致
                    code="0";
                    desc="验证一致";
                } else if ("1".equals(certResult)){
                    //验证不一致
                    code="1";
                    desc="验证不一致";
                }
            } else if ("14".equals(errCode)){
                // 空号、非联通运营商号码、已注销号码、隐私宝号码、携号转网号码、
                // 新入网号码数据未来得及更新情况下会返回这个错误码
                isbilling="0";
                code="10";
                desc="号码不存在";
            }

        } else if ("2".equals(status)){
            isbilling="0";
            //失败
            if("01".equals(errCode)||"02".equals(errCode)){
                //参数校验失败，01表示参数不能为空，02表示格式出错
                return ProjectErrorInformation.businessError5(title+errCode);
            } else {
                // 上游其他错误，03服务器错误，04连接超时，05数据账期不存在，09机构验证错误，
                // 10ip无权访问，11机构秘钥错误，13余额不足，15序列号重复，21解密失败
                ProjectErrorInformation.businessError4(title+errCode);
            }

        }

        JSONObject ret = new JSONObject();
        ret.put("interface", title);
        ret.put("code", code);
        ret.put("desc", desc);
        ret.put("isbilling", isbilling);
        return ret.toString();
    }
}
