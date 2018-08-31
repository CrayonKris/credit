package com.bonc.credit.service.tianyizhengxin;

import com.alibaba.fastjson.JSONObject;

public interface TianyiService {
    String getJobInfo(JSONObject bizParams);

    String getHomeInfo(JSONObject bizParams);

    String getPhoneNameInfo(JSONObject bizParams);

    String getCardNameInfo(JSONObject bizParams);

    String getCardInfo(JSONObject bizParams);

    /**
     * 全网运营商三要素验证，详版
     * @param bizParams
     * @return
     */
    String getMobilecardInfo(JSONObject bizParams);

    /**
     * 全网运营商三要素验证，简版
     * @param bizParams
     * @return
     */
    String getMobilecardInfoII(JSONObject bizParams);

    String getBankcard4Info(JSONObject bizParams);

    String getBankcard5Info(JSONObject bizParams);

    String getUserState(JSONObject bizParams);

    String getOnlineTime(JSONObject bizParams);
}
