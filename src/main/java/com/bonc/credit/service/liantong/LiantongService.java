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
 * @Author: zhouqian
 * @Date: 2018/8/20 9:47
 */
@Service
public class LiantongService extends LiantongHelper {

    private static Logger logger = Logger.getLogger(LiantongService.class);
    /**
     *  三要素验证详版，MD5
     * @param bizParams
     * @return
     */
    public String getThreeElementsMd5(JSONObject bizParams){
        String title = "运营商三要素详版-MD5";
        String method = "uniquecheck/txCheckUser.do";

        String mobile = bizParams.getString("mobile");
        String userName = bizParams.getString("userName");
        String idCard = bizParams.getString("idCard");
        String idType = bizParams.getString("idType");
        //参数检验
        if (StringUtils.isEmpty(mobile)||StringUtils.isEmpty(userName)|| StringUtils.isEmpty(idCard)||
                StringUtils.isEmpty(idCard)||StringUtils.isEmpty(idType)){
            return ProjectErrorInformation.businessError5(title);
        }

        if (!("0101".equals(idType)||"0102".equals(idType)||"0103".equals(idType)||
                "0104".equals(idType)||"0105".equals(idType)||"0106".equals(idType)||
                "0107".equals(idType)||"0199".equals(idType)||"0999".equals(idType))){
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
        map.put("sendTelNo", MD5Builder.md5(mobile));

        //身份证件类型判断
        if (StringUtils.equals("0101",idType)||StringUtils.equals("0102",idType)||StringUtils.equals("0103",idType)||
                StringUtils.equals("0104",idType)||StringUtils.equals("0105",idType)||StringUtils.equals("0106",idType)||
                StringUtils.equals("0107",idType)||StringUtils.equals("0199",idType)||StringUtils.equals("0999",idType)){
            map.put("certType",idType);
        }
        map.put("certCode",MD5Builder.md5(idCard));
        map.put("userName",MD5Builder.EncoderByMd5ByGBK(name));

        String response = LiantongHelper.getRes(method,map);

        if (StringUtils.isEmpty(response)){
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info("response >> "+response);
        JSONObject jsonres = JSONObject.parseObject(response);
        String status = jsonres.getString("status");
        code = jsonres.getString("code");
        String error = jsonres.getString("errorDesc");
        String checkResult = jsonres.getString("checkResult");
        logger.info("调用状态："+"code: "+code+"error："+error+"checkResult: "+checkResult);

        if ("1".equals(status)){
            //成功
            //00: 手机号、证件号、姓名均一致
            //01: 手机号一致，证件号和姓名不一致
            //02: 手机号和证件号一致，姓名不一致
            //03: 手机号和姓名一致，证件号不一致
            if ("00".equals(code)){
                switch (checkResult){
                    case "00":desc = "手机号、证件号、姓名均一致";break;
                    case "01":desc = "手机号一致，证件号和姓名不一致";break;
                    case "02":desc = "手机号和证件号一致，姓名不一致";break;
                    case "03":desc = "手机号和姓名一致，证件号不一致";break;
                }
                code = checkResult;
                isbilling = "1";
            }
            if ("14".equals(code)){
                //空号、非联通运营商号码、新入网号码数据未来得及更新情况下会返回这个错误码
                return ProjectErrorInformation.businessError6(title);
            }
        }
        if ("2".equals(status)){
            if ("01".equals(code)||"02".equals(code)){
                return ProjectErrorInformation.businessError5(title);
            }else {
                return ProjectErrorInformation.businessError4(title);
            }
        }
        return sentres(title,code,desc,isbilling);
    }

    private String sentres(String title,String code,String desc,String isbilling) {
        JSONObject ret = new JSONObject();
        ret.put("interface", title);
        ret.put("code", code);
        ret.put("desc", desc);
        ret.put("isbilling", isbilling);
        return ret.toString();
    }

    /**
     * 手机号姓名核查
     * @param bizParams
     * @return
     */
    public String getMobileName(JSONObject bizParams){
        String title = "手机号姓名核查";
        String method = "identity/nameCheck.do";

        String mobile = bizParams.getString("mobile");
        String userName = bizParams.getString("userName");
        //参数检验
        if (StringUtils.isEmpty(mobile)||StringUtils.isEmpty(userName)){
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
        map.put("sendTelNo", mobile);
        map.put("username",name);

        String response = LiantongHelper.getRes(method,map);

        if (StringUtils.isEmpty(response)){
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info("response >> "+response);
        JSONObject jsonres = JSONObject.parseObject(response);
        String status = jsonres.getString("status");
        code = jsonres.getString("code");
        String error = jsonres.getString("errorDesc");
        String checkResult = jsonres.getString("result");
        logger.info("调用状态："+"code: "+code+"error："+error+"checkResult: "+checkResult);

        if ("1".equals(status)){
            //成功
            //0：验证一致；
            //1：验证不一致
            if ("00".equals(code)){
                switch (checkResult){
                    case "0":desc = "验证一致";break;
                    case "1":desc = "验证不一致";break;
                }
                code = checkResult;
                isbilling = "1";
            }
            if ("14".equals(code)){
                //空号、非联通运营商号码、新入网号码数据未来得及更新情况下会返回这个错误码
                return ProjectErrorInformation.businessError6(title);
            }
        }
        if ("2".equals(status)){
            if ("01".equals(code)||"02".equals(code)){
                return ProjectErrorInformation.businessError5(title);
            }else {
                return ProjectErrorInformation.businessError4(title);
            }
        }
        return sentres(title,code,desc,isbilling);
    }

    /**三要素验证详版，第二版
     * @param bizParams
     * @return
     */
    public String getThreeElementsMd5V2(JSONObject bizParams) {
        String title = "三要素验证详版v2.0";
        String method = "check/userCheck.do";

        String mobile = bizParams.getString("mobile");
        String userName = bizParams.getString("userName");
        String idCard = bizParams.getString("idCard");
        String idType = bizParams.getString("idType");
        //参数检验
        if (StringUtils.isEmpty(mobile)||StringUtils.isEmpty(userName)|| StringUtils.isEmpty(idCard)||
                StringUtils.isEmpty(idCard)||StringUtils.isEmpty(idType)){
            return ProjectErrorInformation.businessError5(title);
        }

        if (!("0101".equals(idType)||"0102".equals(idType)||"0103".equals(idType)||
                "0104".equals(idType)||"0105".equals(idType)||"0106".equals(idType)||
                "0107".equals(idType)||"0199".equals(idType)||"0999".equals(idType))){
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
        map.put("sendTelNo", mobile);
        map.put("certType",idType);
        map.put("certCode",idCard);
        map.put("userName",name);

        String response = LiantongHelper.getRes(method,map);

        if (StringUtils.isEmpty(response)){
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info("response >> "+response);
        JSONObject jsonres = JSONObject.parseObject(response);
        String status = jsonres.getString("status");
        code = jsonres.getString("code");
        String error = jsonres.getString("errorDesc");
        String checkResult = jsonres.getString("checkResult");
        logger.info("调用状态："+"code: "+code+"error："+error+"checkResult: "+checkResult);

        if ("1".equals(status)){
            //成功
            //00: 手机号、证件号、姓名均一致
            //01: 手机号一致，证件号和姓名不一致
            //02: 手机号和证件号一致，姓名不一致
            //03: 手机号和姓名一致，证件号不一致
            if ("00".equals(code)){
                switch (checkResult){
                    case "00":desc = "手机号、证件号、姓名均一致";break;
                    case "01":desc = "手机号、证件号一致、姓名不一致";break;
                    case "02":desc = "手机号、证件号一致、姓名为空";break;
                    case "03":desc = "手机号，姓名一致、证件号不一致";break;
                    case "04":desc = "手机号一致，证件号，姓名不一致";break;
                    case "05":desc = "手机号一致，证件号不一致，姓名为空";break;
                    case "06":desc = "手机号、姓名一致，证件号为空";break;
                    case "07":desc = "手机号一致，证件号为空，姓名不一致";break;
                    case "08":desc = "手机号一致，证件号，姓名为空";break;
                }
                code = checkResult;
                isbilling = "1";
            }
            if ("14".equals(code)){
                //空号、非联通运营商号码、新入网号码数据未来得及更新情况下会返回这个错误码
                return ProjectErrorInformation.businessError6(title);
            }
        }
        if ("2".equals(status)){
            if ("01".equals(code)||"02".equals(code)){
                return ProjectErrorInformation.businessError5(title);
            }else {
                return ProjectErrorInformation.businessError4(title);
            }
        }
        return sentres(title,code,desc,isbilling);
    }
}
