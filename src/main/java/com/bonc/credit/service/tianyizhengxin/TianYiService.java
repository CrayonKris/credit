package com.bonc.credit.service.tianyizhengxin;

import com.alibaba.fastjson.JSONObject;
import com.bonc.util.ProjectErrorInformation;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * @Author: ZQ
 * @Date: 2018/8/7 16:36
 */
@Service
public class TianYiService extends TianyiHelper{

    private static final Logger logger = Logger.getLogger(TianYiService.class);
    //工作信息
    public String getJob(JSONObject bizParams){

        return null;
    }

    /**
     * 全网运营商三要素验证（详版）
     * @param bizParams
     * @return
     */
    public String getMobilecardInfo(JSONObject bizParams) {

        String title = "全网运营商三要素验证（详版）";
        String mobile = bizParams.getString("mobile");
        String userName = bizParams.getString("userName");
        String idCard = bizParams.getString("idCard");
        String method = "threeElementsCheckAllDetail.json";

        String code = "B0001";
        String desc = "调用失败";
        String isbilling = "0";
        if (StringUtils.isEmpty(userName)||StringUtils.isEmpty(mobile)||StringUtils.isEmpty(idCard)){
            return ProjectErrorInformation.businessError5(title);
        }
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("name",userName);
        jsonParam.put("mobile",mobile);
        jsonParam.put("idCardNum",idCard);

        //发送请求
        String response = TianyiHelper.getResponse(method,jsonParam);
//        String response = "{\"credit\":{\"header\":{\"version\":\"0100\",\"testFlag\":1,\"activityCode\":\"1005\",\"actionCode\":1,\"reqSys\":\"dongfangguoxin001\",\"reqChannel\":\"0\",\"reqTransID\":\"BONC1534408678389R980\",\"reqDate\":\"20180816\",\"reqDateTime\":\"20180816163758\",\"rcvSys\":\"123456\",\"rcvTransID\":\"10180816163758000002178488087221\",\"rcvDate\":\"20180816\",\"rcvDateTime\":\"20180816163759\",\"rspCode\":\"90002\",\"rspDesc\":\"查无记录\",\"authorizationCode\":\"890a1377c6034649a2c834ab7a907e75\"},\"body\":null,\"mac\":\"093f2b2e94d7e75a6bb957caad91ea74\"}}\n";
        if (StringUtils.isEmpty(response)){
            return ProjectErrorInformation.businessError1(title);
        }
        logger.info("response>>  "+response);
        JSONObject jsonResult = JSONObject.parseObject(response);
        JSONObject header = jsonResult.getJSONObject("credit").getJSONObject("header");
        Object rspCode = header.get("rspCode");
        String rspDesc = header.getString("rspDesc");
        logger.info("结果状态："+rspCode+"| "+rspDesc);

        //请求成功
        if ("0000".equals(rspCode)||"000000".equals(rspCode)){
            code = rspCode.toString();
            JSONObject body = jsonResult.getJSONObject("credit").getJSONObject("body");
            String checkResult = body.getString("checkResultAllDetail");
            if ("1".equals(checkResult)){
                desc = "三要素一致";
            } else if ("2".equals(checkResult)){
                desc = "手机号已实名，但是身份证和姓名均与实名信息不一致";
            } else if ("3".equals(checkResult)){
                desc = "手机号已实名，手机号和证件号一致，姓名不一致";
            } else if ("4".equals(checkResult)){
                desc = "手机号已实名，手机号和姓名一致，身份证不一致";
            } else if ("5".equals(checkResult)){
                desc = "其他不一致";
            }
            isbilling = "1";
            code = checkResult;
        } else if ("90002".equals(rspCode)){
            code = "0";
            desc = "查无数据";
        }else {
            return ProjectErrorInformation.businessError1(title);
        }
        return sentJson(title,code,desc,isbilling);
    }

    //返回结果
    public String sentJson(String title,String code,String desc,String isBilling){
        JSONObject json2 = new JSONObject();
        json2.put("interface",title);
        json2.put("code",code);
        json2.put("desc",desc);
        json2.put("isbilling",isBilling);

        return json2.toString();
    }
}
