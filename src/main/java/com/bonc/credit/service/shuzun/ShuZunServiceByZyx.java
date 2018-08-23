package com.bonc.credit.service.shuzun;

import com.alibaba.fastjson.JSONObject;

/**
*
* @author zhangyixuan
* @date 2018年7月26日
*
*/
public interface ShuZunServiceByZyx {

    /**
     * 验证联通定制-经纬度验证
     * @param params
     * @return
     */
    String getLatitudeAndLongitude(JSONObject params);

    /**
     * 居住地址位置验证
     * @param params
     * @return
     */
    String getResidentialAddress(JSONObject params);

    /**
     * 联通定制-是否二次放号查询
     * @param params
     * @return
     */
    String getTwoNumbers(JSONObject params);


    /**
     * 调用百川的两元素验证
     * @param params
     * @return
     */
    String getBaichuanInfo(JSONObject params);

    /**
     * 调用中诚信的两元素验证
     * @param params
     * @return
     */
    String getZhongchengxinInfo(JSONObject params);


}
