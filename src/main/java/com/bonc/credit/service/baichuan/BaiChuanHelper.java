package com.bonc.credit.service.baichuan;

import com.bonc.credit.service.zhongchengxin.ZhongchengxinHelper;
import com.bonc.util.HttpRequest;
import com.bonc.util.HttpsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class BaiChuanHelper {
    private final static Logger logger = LoggerFactory.getLogger(ZhongchengxinHelper.class);

    // 测试环境
    public static final String account = "Test";
    public static final String password = "Test_pw";
    private static final String baseUrl = "https://116.62.203.146/apiv2";


    public static String getResponse( String param){

        String response = HttpsUtil.doPost(baseUrl,param);
        return response;
    }
}
