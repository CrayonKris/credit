package com.bonc.credit.service.baichuan;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.service.CreditService;
import com.bonc.util.ProjectErrorInformation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaiChuanServiceImpl extends BaiChuanHelper implements BaiChuanService {

    private final static Logger logger = LoggerFactory.getLogger(BaiChuanServiceImpl.class);
    @Autowired
    private CreditService service;

        /**
         * 姓名，身份证号码，银行卡号验证对应关系是否一致.
         *
         * @param bizParams 需要传入userName、idCard、bankCardNum三个参数
         * @return
         */
        public String getVerifyBankcard3 (JSONObject bizParams) {
            String title = "姓名-身份证号码-银行卡号核验";

            // 姓名
            String userName = bizParams.getString("userName");
            String name = "";
            // 身份证号码，字母 X 大写
            String cid = bizParams.getString("idCard");
            // 银行卡号
            String card = bizParams.getString("bankCardNum");
            if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(card) || StringUtils.isEmpty(cid)) {
                return ProjectErrorInformation.businessError5(title);
            }
            try {
                name = URLDecoder.decode(userName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("中文编码异常");
            }
            //接口名
            String method = "verifybankcard3";


            JSONObject jsonparam = new JSONObject();
            jsonparam.put("name",name);
            jsonparam.put("idCard",cid);
            jsonparam.put("bankCard",card);

            String response = getRespose(jsonparam,method);
            logger.info(" response >> " + response);

            String code = "B0001";
            String desc = "调用失败";
            String isBilling="0";
            if (response == null || "".equals(response)) {
                return ProjectErrorInformation.businessError1(title);
            }

            JSONObject result = JSONObject.parseObject(response);
            String resCode = result.getString("code");
            String resMsg = result.getString("message");

            logger.info("调用信息："+resCode+"| "+resMsg);
            switch (resCode){
                case "1":
                    code = "1";
                    desc = "认证信息匹配";isBilling = "1";break;
                case "2":
                    code = "2";
                    desc = "认证信息不匹配";isBilling = "1";break;
                case "3":
                    code = "3";
                    desc = "无记录";isBilling = "1";break;
                case "-1":
                    code = "-1";
                    desc = "异常情况";
            }

            JSONObject json = new JSONObject();
            json.put("interface",title);
            json.put("code",code);
            json.put("desc",desc);
            json.put("isbilling",isBilling);
            return json.toString();
        }

    @Override
    public String getMobilecardInfo(JSONObject bizParams) {

        String title = "手机号-姓名-身份证验证";
        String method = "verifyMobileIdCardName";

        String idCard = bizParams.getString("idCard");
        String mobile = bizParams.getString("mobile");
        String userName = bizParams.getString("userName");

        String name = "";

        if (StringUtils.isEmpty(userName)||(StringUtils.isEmpty(mobile))||(StringUtils.isEmpty(idCard))){
            return ProjectErrorInformation.businessError5(title);
        }
        try {
            name = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }

        String oprator = service.getPhoneProvider(mobile);
        if ("YD".equals(oprator)){
            oprator = "移动";
        } else if ("LT".equals(oprator)){
            oprator = "联通";
        } else {
            oprator = "电信";
        }
        JSONObject jsonparam = new JSONObject();
        jsonparam.put("name",name);
        jsonparam.put("mobile",mobile);
        jsonparam.put("idCard",idCard);

        String response = getRespose(jsonparam,method);
        logger.info(" response >> " + response);

        String code = "B0001";
        String desc = "调用失败";
        String isBilling="0";
        String unmatch = "";
        if (StringUtils.isEmpty(response)){
            return ProjectErrorInformation.businessError1(title);
        }
        JSONObject result = JSONObject.parseObject(response);
        String resCode = result.getString("code");
        String resMsg = result.getString("message");

        logger.info("调用信息："+resCode+"| "+"| "+resMsg);

        switch (resCode){
            case "1": //全一致
                desc = "验证一致，"+oprator;
                code = "1";
                isBilling = "1";
                break;
            case "2":
                desc = "部分匹配，";
                unmatch = result.getJSONObject("data").getString("unmatched");
                    if ("idNo".equals(unmatch)){
                        desc += "身份证-手机号不一致，姓名-手机号一致，"+oprator;
                        code = "2";
                    } else if ("name".equals(unmatch)){
                        desc += "身份证-手机号一致，姓名-手机号不一致，"+oprator;
                        code = "3";
                    } else {
                        desc += "具体不一致字段未知，"+oprator;
                        code = "4";
                    }
                isBilling = "1";break;
            case "3":
                code = "0";
                desc = "验证不一致，"+oprator;
                isBilling = "1";break;
            case "-1":
                code = "-1";
                desc = "库无记录，"+oprator;
                isBilling = "1";break;
        }

        return sentJson(title,code,desc,isBilling);
    }

    @Override
    public String getVerifyBankcard4(JSONObject bizParams) {
        String title = "手机号-姓名-身份证-银行卡号验证";
        String method = "verifybankcard4";

        String idCard = bizParams.getString("idCard");
        String mobile = bizParams.getString("mobile");
        String userName = bizParams.getString("userName");
        String bankCardNum = bizParams.getString("bankCardNum");

        String name = "";

        if (StringUtils.isEmpty(userName)||(StringUtils.isEmpty(mobile))||(StringUtils.isEmpty(idCard))
                ||StringUtils.isEmpty(bankCardNum)){
            return ProjectErrorInformation.businessError5(title);
        }
        try {
            name = URLDecoder.decode(userName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("中文编码异常");
        }

        JSONObject jsonparam = new JSONObject();
        jsonparam.put("name",userName);
        jsonparam.put("mobile",mobile);
        jsonparam.put("idCard",idCard);
        jsonparam.put("bankCard",bankCardNum);

        String response = getRespose(jsonparam,method);
        logger.info(" response >> " + response);

        String code = "B0001";
        String desc = "调用失败";
        String isBilling="0";
        if (StringUtils.isEmpty(response)){
            return ProjectErrorInformation.businessError1(title);
        }
        JSONObject result = JSONObject.parseObject(response);
        String resCode = result.getString("code");
        String resMsg = result.getString("message");

        logger.info("调用信息："+resCode+"| "+resMsg);

        switch (resCode){
            case "1":
                desc = "认证信息匹配";
                code = "1";
                isBilling = "1";
                break;
            case "2":
                code = "2";
                desc = "认证信息不匹配";
                isBilling = "1";break;
            case "3":
                code = "3";
                desc = "无法验证";
                isBilling = "1";break;
            case "-1":
                code = "-1";
                desc = "异常情况";
                isBilling = "1";break;
        }

        return sentJson(title,code,desc,isBilling);
    }


    /**
     * 获取访问结果
     * @param json
     * @param method
     * @return
     */
    public String getRespose (JSONObject json,String method){
        Map<String,Object> map = new HashMap<>();
        map.put("loginName", BaiChuanHelper.account);
        map.put("pwd", BaiChuanHelper.password);
        map.put("serviceName",method);
        map.put("param",json);

        String param = JSONUtils.toJSONString(map);
        String response = BaiChuanHelper.getResponse(param);
        return response;
    }

    /**
     * 返回结果
     */
    public String sentJson(String title,String code,String desc,String isBilling){
        JSONObject json2 = new JSONObject();
        json2.put("interface",title);
        json2.put("code",code);
        json2.put("desc",desc);
        json2.put("isbilling",isBilling);

        return json2.toString();
    }

}
