package com.bonc.credit.service.tianyizhengxin;

import com.alibaba.fastjson.JSONObject;

public interface TianyiService {
    String getJobInfo(JSONObject bizParams);

    String getHomeInfo(JSONObject bizParams);

    String getPhoneNameInfo(JSONObject bizParams);

    String getCardNameInfo(JSONObject bizParams);

    /**
     * 公安身份验证
     * @param bizParams
     * @return
     */
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

    /**
     * 全网运营商手机在网状态查询
     * @param bizParams
     * @return
     */
    String getUserState(JSONObject bizParams);

    /**
     * 全网运营商手机入网时长查询
     * @param bizParams
     * @return
     */
    String getOnlineTime(JSONObject bizParams);
}
