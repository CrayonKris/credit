package com.bonc.credit.service.baichuan;

import com.alibaba.fastjson.JSONObject;


public interface BaiChuanService {

    /**
     * 银行三要素验证
     * @param bizParams
     * @return
     */
    String getVerifyBankcard3(JSONObject bizParams);

    /**
     * 验证手机号身份证姓名是否一致
     * @param bizParams
     * @return
     */
    String getMobilecardInfo(JSONObject bizParams);

    String getVerifyBankcard4(JSONObject bizParams);

}
