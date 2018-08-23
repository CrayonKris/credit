package com.bonc.credit.service.baichuan;

import com.alibaba.fastjson.JSONObject;

public interface BaiChuanServiceByZyx {

    String getCardNameInfo(JSONObject bizParams);

    String getXueli(JSONObject bizParams);

    String getTimeByPhone(JSONObject bizParams);

    String getPhoneStatus(JSONObject bizParams);
}
