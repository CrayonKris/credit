package com.bonc.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.bonc.credit.mapper.CreditMapper;
import com.bonc.credit.service.baichuan.BaiChuanInterfaceRegist;
import com.bonc.credit.service.bdcsc.BdcscInterfaceRegist;
import com.bonc.credit.service.huadao.HuaDaoInterfaceRegist;
import com.bonc.credit.service.liantong.LiantongInterfaceRegist;
import com.bonc.credit.service.mobi.MobiInterfaceRegist;
import com.bonc.credit.service.shuzun.ShuZunInterfaceRegist;
import com.bonc.credit.service.tianchuang.TianChuangInterfaceRegist;
import com.bonc.credit.service.tianyizhengxin.TianYiInterfaceRegist;
import com.bonc.credit.service.zhongchengxin.ZhongChengXinInterfaceRegist;
import com.bonc.credit.service.zhongsheng.ZhongShengInterfaceRegist;
import com.bonc.redis.RedisUtil;
import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;
import com.bonc.util.ProjectErrorInformation;
import com.bonc.wo_key.WoMd5;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author zhijie.ma
 * @date 2017年5月2日
 */
@Service
@Transactional
public class CreditService {

    private final static Logger logger = LoggerFactory.getLogger(CreditService.class);
//    private static String netphone_baseUrl = "http://192.168.0.11/netphone/";
    private static String netphone_baseUrl = "http://san.511860.com/netphone/";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CreditMapper creditMapper;

    @Autowired
    private BdcscInterfaceRegist bdcscInterfaceRegist;

    @Autowired
    private ZhongShengInterfaceRegist zhongShengInterfaceRegist;

    @Autowired
    private HuaDaoInterfaceRegist huaDaoInterfaceRegist;

    @Autowired
    private MobiInterfaceRegist mobiInterfaceRegist;

    @Autowired
    private ZhongChengXinInterfaceRegist zhongChengXinInterfaceRegist;

    @Autowired
    private TianChuangInterfaceRegist tianChuangInterfaceRegist;

    @Autowired
    private TianYiInterfaceRegist tianYiInterfaceRegist;

    @Autowired
    private ShuZunInterfaceRegist shuZunInterfaceRegist;

    @Autowired
    private BaiChuanInterfaceRegist baiChuanInterfaceRegist;

    @Autowired
    private LiantongInterfaceRegist liantongInterfaceRegist;

    /**
     * 征信用户信息查询--对外服务 -- 请求接口 【老】用户专用通道
     *
     * @param bizParams
     * @param method    方法名称
     * @param account   用户名称
     * @param token     令牌
     * @param ipAddress ip地址
     * @return
     */
    public String oldVilidateChannel(String bizParams, String method, String account, String token, String ipAddress, String uuid) {
        if (account == null || account.equals("") || token == null || token.equals("") || method == null
                || method.equals("") || bizParams == null || bizParams.equals("")) {
            String businessError5 = ProjectErrorInformation.businessError5("");
            //analysisInformation(businessError5, "isEmpty", bizParams, method, account, null, null);
            return businessError5;
        }


        logger.info(account + "【老】用户   >>> ip: " + ipAddress + ">>> token " + token + ">>> " + "携带的参数：" + bizParams
                + "访问了" + method + "接口");
        try {
            Integer.parseInt(account);
        } catch (NumberFormatException e) {
            String businessError5 = ProjectErrorInformation.businessError5("");
            analysisInformation(businessError5, "isEmpty", bizParams, method, account, null, null,uuid);
            return businessError5;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("account", account);

        Map<String, Object> channelByAccount = creditMapper.getChannelByAccount(map);
        if (channelByAccount == null) {
            String systemError13 = ProjectErrorInformation.systemError13("用户不存在");
            analysisInformation(systemError13, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError13;
        }

        String checkStatus = (String) channelByAccount.get("check_status");
        String channelStatus = (String) channelByAccount.get("channel_status");
        String appKey = (String) channelByAccount.get("appKey");

        if (!checkStatus.equals("1") || !channelStatus.equals("1")) {
            String systemError1 = ProjectErrorInformation.systemError1("");
            analysisInformation(systemError1, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError1;
        }

        // token验证
        String channelToken = null;
        if (Integer.parseInt(account) >= 1902)
            channelToken = MD5Builder.md5(account + appKey + method + bizParams);
        else
            channelToken = WoMd5.encode(account + appKey + method + bizParams);

        if (channelToken == null || !channelToken.equals(token)) {
            String systemError4 = ProjectErrorInformation.systemError4("");
            analysisInformation(systemError4, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError4;
        }

        // 判断接口是否存在
        List<Map<String, String>> interfaceInformation = creditMapper.getInterfaceInformation(method, null);
        if (interfaceInformation.isEmpty()) {
            String systemError11 = ProjectErrorInformation.systemError11(method);
            analysisInformation(systemError11, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError11;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(bizParams);
        } catch (Exception e) {
            String systemError6 = ProjectErrorInformation.systemError6("");
            analysisInformation(systemError6, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError6;
        }

        // 若接口不需要手机号，需要在这里登记
        if (method.equals("CSM170719_1658") || method.equals("CSM170719_1660") || method.equals("CSM171017_1126")
                ||method.equals("CSM180710_1628")||method.equals("CSM180706_1731")||"CSM180801_1531".equals(method)||
                "CSM180801_1537".equals(method)) {
            jsonObject.put("mobile", "17600000000");
        }

        // 验证手机是否合法
        String providerType = vilidatePhone(jsonObject, method);

        List<Map<String, String>> interfaceInformation2 = creditMapper.getInterfaceInformation(method, providerType);
        if (interfaceInformation2.isEmpty()) {

            String supportPhone = isSupportPhone(providerType);

            analysisInformation(supportPhone, "isEmpty", bizParams, method, account, null, null,uuid);

            return supportPhone;

        }

        String providerCode = null;
        String enabled = null;
        boolean sign = false;

        for (Map<String, String> map2 : interfaceInformation2) {
            if (interfaceInformation2.size() == 1) {
                providerCode = map2.get("PROVIDER_CODE");
                enabled = map2.get("ENABLED");
                if (enabled.equals("0")) {

                    String systemError12 = ProjectErrorInformation.systemError12(method);
                    analysisInformation(systemError12, providerCode, bizParams, method, account, null, null,uuid);
                    return systemError12;
                }
                sign = true;
            } else {
                if (map2.get("ENABLED").equals("1")) {
                    enabled = map2.get("ENABLED");
                    providerCode = map2.get("PROVIDER_CODE");
                    sign = true;
                    break;
                }
            }
        }

        if (sign == false) {
            String systemError12 = ProjectErrorInformation.systemError12(method);
            analysisInformation(systemError12, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError12;
        }

        logger.info("***请求的接口信息为***" + providerCode + "公司的" + method + "接口 ,参数为：" + jsonObject);

        String visitResult = checkMethod(providerCode, method, jsonObject,uuid).toString();

        if ("CSM180706_1628".equals(method)){
            providerCode = "zhongchengxin";
        }if ("CSM180706_1731".equals(method)){
            providerCode = "baichuan";
        }
        analysisInformation(visitResult, providerCode, bizParams, method, account, null, null,uuid);

        return visitResult;

    }

    /**
     * 征信用户信息查询--对外服务 -- 请求接口 新用户专用
     *
     * @param bizParams 业务参数
     * @param method    接口名称
     * @param account   用户名称
     * @param token     通行证
     * @param ipAddress ip地址
     * @return
     */
    public String vilidateChannel(String bizParams, String method, String account, String token, String ipAddress,String uuid) {
        if (account == null || account.equals("") || token == null || token.equals("") || method == null
                || method.equals("") || bizParams == null || bizParams.equals("")) {
            String businessError5 = ProjectErrorInformation.businessError5("");
//			analysisInformation(businessError5, "isEmpty", bizParams, method, account, null, null);
            return businessError5;
        }

        logger.info(account + "用户   >>> ip: " + ipAddress + ">>> token " + token + ">>> " + "携带的参数：" + bizParams + "访问了"
                + method + "接口");

        String getToken = redisUtil.getString(account);

        if (getToken == null || !getToken.equals(token)) {
            String systemError4 = ProjectErrorInformation.systemError4("");
            analysisInformation(systemError4, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError4;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("account", account);

        Map<String, Object> channelByAccount = creditMapper.getChannelByAccount(map);
        if (channelByAccount == null) {
            String systemError13 = ProjectErrorInformation.systemError13("");
            analysisInformation(systemError13, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError13;
        }

        String checkStatus = (String) channelByAccount.get("check_status");
        String channelStatus = (String) channelByAccount.get("channel_status");

        if (!checkStatus.equals("1") || !channelStatus.equals("1")) {
            String systemError1 = ProjectErrorInformation.systemError1("");
            analysisInformation(systemError1, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError1;
        }

        String channelCode = (String) channelByAccount.get("channel_code");

        map.put("channel_code", channelCode);
        String result = null;
        List<Map<String, String>> iPvilidateList = creditMapper.getIPvilidate(map);
        for (Map<String, String> map2 : iPvilidateList) {
            String ip = map2.get("ip_address");

            if (ipAddress.equals(ip)) {
                result = "ok";
                break;
            }
        }

        if (result == null) {
            String systemError3 = ProjectErrorInformation.systemError3("");
            analysisInformation(systemError3, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError3;
        }

        // 判断该产品是否被配置
        Map<String, String> productInformation = creditMapper.getProductInformation(method);
        if (productInformation == null) {
            String systemError14 = ProjectErrorInformation.systemError14(method);
            analysisInformation(systemError14, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError14;
        }

        String productNumber = productInformation.get("PRODUCT_NUMBER");

        // 判断接口是否存在
        List<Map<String, String>> interfaceInformation = creditMapper.getInterfaceInformation(method, null);
        if (interfaceInformation.isEmpty()) {
            String systemError11 = ProjectErrorInformation.systemError11(method);
            analysisInformation(systemError11, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError11;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(bizParams);
        } catch (Exception e) {
            String systemError6 = ProjectErrorInformation.systemError6("");
            analysisInformation(systemError6, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError6;
        }

        // 若接口不需要手机号，需要在这里登记
        if (method.equals("CSM170719_1658") || method.equals("CSM170719_1660") || method.equals("CSM171017_1126")||
                method.equals("CSM180710_1628")||method.equals("CSM180706_1731")) {
            jsonObject.put("mobile", "17600000000");
        }

        // 验证手机是否合法
        String providerType = vilidatePhone(jsonObject, method);

        List<Map<String, String>> interfaceInformation2 = creditMapper.getInterfaceInformation(method, providerType);
        if (interfaceInformation2.isEmpty()) {
            String supportPhone = isSupportPhone(providerType);

            analysisInformation(supportPhone, "isEmpty", bizParams, method, account, null, null,uuid);

            return supportPhone;
        }

        String providerCode = null;
        String enabled = null;
        boolean sign = false;

        for (Map<String, String> map3 : interfaceInformation2) {
            if (interfaceInformation2.size() == 1) {
                providerCode = map3.get("PROVIDER_CODE");
                enabled = map3.get("ENABLED");
                if (enabled.equals("0")) {

                    String systemError12 = ProjectErrorInformation.systemError12(method);
                    analysisInformation(systemError12, providerCode, bizParams, method, account, null, null,uuid);
                    return systemError12;
                }
                sign = true;
            } else {
                if (map3.get("ENABLED").equals("1")) {
                    enabled = map3.get("ENABLED");
                    providerCode = map3.get("PROVIDER_CODE");
                    sign = true;
                    break;
                }
            }
        }

        if (sign == false) {

            String systemError12 = ProjectErrorInformation.systemError12(method);
            analysisInformation(systemError12, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError12;
        }

        HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
        hashMap2.put("channel_code", channelCode);
        hashMap2.put("product_number", productNumber);
        Integer selectVisitCount = creditMapper.selectVisitCount(hashMap2);

        if (selectVisitCount == null) {
            String systemError7 = ProjectErrorInformation.systemError7(method);
            analysisInformation(systemError7, providerCode, bizParams, method, account, null, null,uuid);
            return systemError7;
        }

        if (selectVisitCount <= 0) {
            String systemError10 = ProjectErrorInformation.systemError10(method);

            analysisInformation(systemError10, providerCode, bizParams, method, account, null, null,uuid);
            return systemError10;
        }

        logger.info("***请求的接口信息为***" + providerCode + "公司的" + method + "接口 ,参数为：" + jsonObject);

        String visitResult = checkMethod(providerCode, method, jsonObject,uuid).toString();

        analysisInformation(visitResult, providerCode, bizParams, method, account, selectVisitCount, hashMap2,uuid);

        return visitResult;
    }

    /**
     * 征信用户信息查询--对内服务 <br/>
     * bonc验证服务参数信息是否合法
     *
     * @param bizParams 业务参数
     * @param method    接口名称
     * @param account   用户名称
     * @return
     */
    public String vilidateInformation(String bizParams, String method, String account,String uuid) {
        if (null != bizParams) {
            try {
                bizParams = URLDecoder.decode(bizParams, "utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // 验证参数，参数错误验证
        if (method == null || method.equals("") || bizParams == null || bizParams.equals("")) {
            return ProjectErrorInformation.businessError5("");

        }
        // 判断接口是否存在
        List<Map<String, String>> interfaceInformation = creditMapper.getInterfaceInformation(method, null);
        if (interfaceInformation.isEmpty()) {

            return ProjectErrorInformation.systemError11(method);
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(bizParams);
        } catch (Exception e) {

            return ProjectErrorInformation.systemError6("");
        }

        // 若接口不需要手机号，需要在这里登记
        if (method.equals("CSM170719_1658") || method.equals("CSM170719_1660") || method.equals("CSM171017_1126") ||
                "CSM180706_1731".equals(method)||"CSM180710_1628".equals(method)||"CSM180801_1531".equals(method)||
                "CSM180801_1537".equals(method)||"CSM180816_1848".equals(method)) {
            jsonObject.put("mobile", "17600000000");
        }

        // 验证手机是否合法
        String providerType = vilidatePhone(jsonObject, method);

        List<Map<String, String>> interfaceInformation2 = creditMapper.getInterfaceInformation(method, providerType);
        if (interfaceInformation2.isEmpty()) {

            String supportPhone = isSupportPhone(providerType);

            analysisInformation(supportPhone, "isEmpty", bizParams, method, account, null, null,uuid);

            return supportPhone;

        }

        String providerCode = null;
        String enabled = null;
        boolean sign = false;

        for (Map<String, String> map : interfaceInformation2) {
            if (interfaceInformation2.size() == 1) {
                providerCode = map.get("PROVIDER_CODE");
                enabled = map.get("ENABLED");
                if (enabled.equals("0")) {

                    String systemError12 = ProjectErrorInformation.systemError12(method);
                    analysisInformation(systemError12, providerCode, bizParams, method, account, null, null,uuid);
                    return systemError12;
                }
                sign = true;
            } else {
                if (map.get("ENABLED").equals("1")) {

                    enabled = map.get("ENABLED");
                    providerCode = map.get("PROVIDER_CODE");
                    sign = true;
                    break;
                }
            }
        }

        if (sign == false) {
            String systemError12 = ProjectErrorInformation.systemError12(method);
            analysisInformation(systemError12, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError12;
        }
        logger.info("***请求的接口信息为***" + providerCode + "公司的" + method + "接口 ,参数为：" + jsonObject);

        //验证完成开始调用服务
        String visitResult = checkMethod(providerCode, method, jsonObject,uuid).toString();

        if ("CSM180706_1628".equals(method)){
            providerCode = "zhongchengxin";
        }if ("CSM180706_1731".equals(method)){
            providerCode = "baichuan";
        }
        analysisInformation(visitResult, providerCode, bizParams, method, account, null, null,uuid);

        return visitResult;
    }

    /**
     * 将最终返回的结果进行业务分析
     *
     * @param visitResult  访问的结果
     * @param providerCode 提供的运营商
     * @param bizParams    用户输入的参数
     * @param method       用户访问的接口
     * @param who          谁调用的接口，是内部调用还是外部调用
     * @param visitCount   查询当前接口剩余访问次数
     * @param hashMap2     将当前的map作为新数据修改到数据库中
     */
    public void analysisInformation(String visitResult, String providerCode, String bizParams, String method,
                                    String who, Integer visitCount, HashMap<String, Object> hashMap2,String uuid) {
        JSONObject parseObject = JSONObject.parseObject(visitResult);
        String isbilling = parseObject.getString("isbilling");
        Map<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("record_id", uuid);
        hashMap.put("channel_code", who);
        hashMap.put("interface_name", method);
        hashMap.put("time", new Date());
        hashMap.put("request_parameter", bizParams);

        hashMap.put("response_parameter", visitResult);
        hashMap.put("provider", providerCode);
        hashMap.put("isbilling", isbilling);

        // 添加接口访问记录
        rabbitTemplate.convertAndSend("addRecord", hashMap);

        // 更新渠道访问次数
        if (!who.equals("bonc")) {
            if (visitCount != null) {
                if (isbilling.equals("1")) {
                    // hashMap2.put("access_count", visitCount - 1);
                    rabbitTemplate.convertAndSend("editRecord", hashMap2);
                }
            }
        }
    }

    /**
     * 判断用户想要调用哪个接口
     *
     * @param providerCode 上游编码
     * @param method       接口名称
     * @param jsonObject   业务参数
     * @return
     */
    public String checkMethod(String providerCode, String method, JSONObject jsonObject,String uuid) {
        logger.info("验证完成！开始调用业务信息...");
        if (providerCode.equals("bdcsc")) {
            // 电信云项目接口注册
            return bdcscInterfaceRegist.distributeInterfaceRegist(method, jsonObject,uuid);
        } else if (providerCode.equals("huadao")) {

            return huaDaoInterfaceRegist.distributeInterfaceRegist(method, jsonObject,uuid);
        } else if (providerCode.equals("mobi")) {

            return mobiInterfaceRegist.distributeInterfaceRegist(method, jsonObject,uuid);
        } else if (providerCode.equals("zhongsheng")) {

            return zhongShengInterfaceRegist.distributeInterfaceRegist(method, jsonObject,uuid);
        } else if (providerCode.equals("zhongchengxin")) {

            return zhongChengXinInterfaceRegist.distributeInterfaceRegist(method, jsonObject,uuid);
        } else if (providerCode.equals("tianchuang")) {

            return tianChuangInterfaceRegist.distributeInterfaceRegist(method, jsonObject,uuid);
        } else if ("shuzun".equals(providerCode)) {
            return shuZunInterfaceRegist.distributeInterfaceRegist(method, jsonObject,uuid);
        }else if ("baichuan".equals(providerCode)) {
            return baiChuanInterfaceRegist.distributeInterfaceRegist(method, jsonObject,uuid);
        }else if ("tianyi".equals(providerCode)) {
            return tianYiInterfaceRegist.distributeInterfaceRegist(method, jsonObject,uuid);
        } else if ("liantong".equals(providerCode)) {
            return liantongInterfaceRegist.distributeInterfaceRegist(method, jsonObject,uuid);
        } else {
            return ProjectErrorInformation.systemError9("");
        }
    }

    /**
     * 根据code获取城市名称
     *
     * @param cityCode 城市编码
     * @return
     */
    // TODO 只有BdcscServicePart1、BdcscServicePart2、BdcscServicePart3调用此方法
    public String getCityNameByCode(String cityCode) {

        String cityName = creditMapper.getCityName(cityCode);
        return cityName;
    }

    /**
     * 根据code获取省份名称
     *
     * @param provinceCode 省份编码
     * @return
     */
    // TODO 只有BdcscServicePart1的getProvince()调用了此方法用于手机号归属省
    public String getProvinceNameByCode(String provinceCode) {
        String provinceName = creditMapper.getProvinceName(provinceCode);
        return provinceName;
    }

    static List<String> ltList = Arrays
            .asList(new String[]{"130", "131", "132", "145", "155", "156", "186", "185", "176", "175", "171","166"});

    static List<String> ydList = Arrays.asList(new String[]{"134", "135", "136", "137", "138", "139", "147", "150",
            "151", "152", "157", "158", "159", "178", "182", "183", "184", "187", "188","198"});

    static List<String> dxList = Arrays.asList(new String[]{"173", "177", "180", "181", "189", "133", "153", "149","199"});

    /**
     * 判断手机号码属于哪个运营商
     *
     * @param mobile
     * @return
     */
    public String getPhoneProvider2(String mobile) {
        if (null == mobile || "".equals(mobile) || mobile.length() < 5)
            return "error";

        String prefix = mobile.substring(0, 3);
        if (ltList.contains(prefix))
            return "LT";
        else if (ydList.contains(prefix))
            return "YD";
        else if (dxList.contains(prefix))
            return "DX";
        return "error";
    }

    /**
     * 获取手机号码提供商
     *
     * @param mobile
     * @return
     */
    public String getPhoneProvider(String mobile) {
        String url = netphone_baseUrl + "netphone.jsp?phone=" + mobile;
        String result = HttpRequest.sendGet(url, null);
        if (null != result && result.contains("provider")) {
            JSONObject jsonObj = JSONObject.parseObject(result);
            String provider = jsonObj.getString("provider");
            logger.info(mobile + " >> 手机号码识别：" + result);
            if (provider.contains("中国移动"))
                return "YD";
            else if (provider.contains("中国联通"))
                return "LT";
            else if (provider.contains("中国电信"))
                return "DX";
            else {
                String s = mobile.substring(0, 3);
                if ("155".equals(s))
                    return "LT";
                return null;
            }

        } else
            return null;
    }

    /**
     * 验证手机号码是否合法
     *
     * @param jsonObject 业务参数
     * @param method     接口名称
     * @return
     */
    public String vilidatePhone(JSONObject jsonObject, String method) {
        // 判断手机是否合法
        String mobile = jsonObject.getString("mobile");
        if (mobile == null || mobile.equals("")) {

            return ProjectErrorInformation.businessError5("");
        }
        String providerType = getPhoneProvider(mobile);
//		String providerType = null;

        if (null == providerType) {
            providerType = getPhoneProvider2(mobile);
            logger.warn(mobile + " >> Unable to identify the phone number.");
        }

        if (providerType.equals("error") || mobile.length() != 11) {

            if (jsonObject.containsKey("province")) {

                List<Map<String, String>> interfaceMobile = creditMapper.getInterfaceMobile(method);
                if (interfaceMobile.isEmpty()) {
                    //暂时先默认为电信
                    providerType = "DX";
                } else {
                    Map<String, String> map = interfaceMobile.get(0);
                    providerType = map.get("PROVIDER_TYPE");
                }

            } else {

                return ProjectErrorInformation.businessError6("");
            }

        }

        return providerType.toUpperCase();
    }

    /**
     * 接口不支持的运营商
     *
     * @param providerType 运营商类型
     * @return
     */
    public String isSupportPhone(String providerType) {
        if (providerType.equalsIgnoreCase("DX")) {

            return ProjectErrorInformation.businessError3("", "电信");
        } else if (providerType.equalsIgnoreCase("LT")) {

            return ProjectErrorInformation.businessError3("", "联通");
        } else if (providerType.equalsIgnoreCase("YD")) {

            return ProjectErrorInformation.businessError3("", "移动");
        } else {

            return ProjectErrorInformation.businessError6("");
        }
    }

    /**
     * 验证用户信息 获取token
     *
     * @param account   用户帐号
     * @param appKey    密钥
     * @param ipAddress ip地址
     * @return
     */
    public String vilidateChannelToken(String account, String appKey, String ipAddress) {

        if (account == null || account.equals("") || appKey == null || appKey.equals("")) {
            return ProjectErrorInformation.businessError5("");
        }
        logger.info(account + "用户来获取了token,他的ip为" + ipAddress + ",appKey为： " + appKey);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("account", account);

        Map<String, Object> channelByAccount = creditMapper.getChannelByAccount(map);
        if (channelByAccount == null) {
            return ProjectErrorInformation.systemError13("");
        }
        String selectAppKey = (String) channelByAccount.get("appKey");
        String channelCode = (String) channelByAccount.get("channel_code");
        String checkStatus = (String) channelByAccount.get("check_status");
        String channelStatus = (String) channelByAccount.get("channel_status");

        map.put("channel_code", channelCode);
        String result = null;
        List<Map<String, String>> iPvilidateList = creditMapper.getIPvilidate(map);
        for (Map<String, String> map2 : iPvilidateList) {
            String ip = map2.get("ip_address");

            if (ipAddress.equals(ip)) {
                result = "ok";
                break;
            }
        }

        if (result == null) {
            return ProjectErrorInformation.systemError3("");
        }

        if (!selectAppKey.equals(appKey)) {
            return ProjectErrorInformation.systemError13("");
        }

        if (!checkStatus.equals("1") || !channelStatus.equals("1")) {
            return ProjectErrorInformation.systemError1("");
        }

        String token = account + appKey + ipAddress + new Date();
        String tokenMD5 = MD5Builder.md5(token);
        redisUtil.setStringTime(account, tokenMD5, 60 * 60);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "200");
        jsonObject.put("account", account);
        jsonObject.put("requestTime", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        jsonObject.put("token", tokenMD5);
        logger.info(account + "获取到的token为：" + tokenMD5 + "time:>>  "
                + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        return jsonObject.toString();
    }

    public String oldVilidateChannelII(String bizParams, String method, String account, String ipAddr,String uuid) {
        if (account == null || account.equals("") || method == null
                || method.equals("") || bizParams == null || bizParams.equals("")) {
            String businessError5 = ProjectErrorInformation.businessError5("");
            //analysisInformation(businessError5, "isEmpty", bizParams, method, account, null, null);
            return businessError5;
        }


        logger.info(account + "【老】用户   >>> ip: " + ipAddr + ">>> token " + ">>> " + "携带的参数：" + bizParams
                + "访问了" + method + "接口");
        try {
            Integer.parseInt(account);
        } catch (NumberFormatException e) {
            String businessError5 = ProjectErrorInformation.businessError5("");
            analysisInformation(businessError5, "isEmpty", bizParams, method, account, null, null,uuid);
            return businessError5;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("account", account);

        Map<String, Object> channelByAccount = creditMapper.getChannelByAccount(map);
        if (channelByAccount == null) {
            String systemError13 = ProjectErrorInformation.systemError13("用户不存在");
            analysisInformation(systemError13, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError13;
        }

        String checkStatus = (String) channelByAccount.get("check_status");
        String channelStatus = (String) channelByAccount.get("channel_status");
        String appKey = (String) channelByAccount.get("appKey");

        if (!checkStatus.equals("1") || !channelStatus.equals("1")) {
            String systemError1 = ProjectErrorInformation.systemError1("");
            analysisInformation(systemError1, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError1;
        }

        // 判断接口是否存在
        List<Map<String, String>> interfaceInformation = creditMapper.getInterfaceInformation(method, null);
        if (interfaceInformation.isEmpty()) {
            String systemError11 = ProjectErrorInformation.systemError11(method);
            analysisInformation(systemError11, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError11;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(bizParams);
        } catch (Exception e) {
            String systemError6 = ProjectErrorInformation.systemError6("");
            analysisInformation(systemError6, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError6;
        }

        // 若接口不需要手机号，需要在这里登记
        if (method.equals("CSM170719_1658") || method.equals("CSM170719_1660") || method.equals("CSM171017_1126")
                ||method.equals("CSM180710_1628")||method.equals("CSM180706_1731")||"CSM180801_1531".equals(method)||
                "CSM180801_1537".equals(method)) {
            jsonObject.put("mobile", "17600000000");
        }

        // 验证手机是否合法
        String providerType = vilidatePhone(jsonObject, method);

        List<Map<String, String>> interfaceInformation2 = creditMapper.getInterfaceInformation(method, providerType);
        if (interfaceInformation2.isEmpty()) {

            String supportPhone = isSupportPhone(providerType);

            analysisInformation(supportPhone, "isEmpty", bizParams, method, account, null, null,uuid);

            return supportPhone;

        }

        String providerCode = null;
        String enabled = null;
        boolean sign = false;

        for (Map<String, String> map2 : interfaceInformation2) {
            if (interfaceInformation2.size() == 1) {
                providerCode = map2.get("PROVIDER_CODE");
                enabled = map2.get("ENABLED");
                if (enabled.equals("0")) {

                    String systemError12 = ProjectErrorInformation.systemError12(method);
                    analysisInformation(systemError12, providerCode, bizParams, method, account, null, null,uuid);
                    return systemError12;
                }
                sign = true;
            } else {
                if (map2.get("ENABLED").equals("1")) {
                    enabled = map2.get("ENABLED");
                    providerCode = map2.get("PROVIDER_CODE");
                    sign = true;
                    break;
                }
            }
        }

        if (sign == false) {
            String systemError12 = ProjectErrorInformation.systemError12(method);
            analysisInformation(systemError12, "isEmpty", bizParams, method, account, null, null,uuid);
            return systemError12;
        }

        logger.info("***请求的接口信息为***" + providerCode + "公司的" + method + "接口 ,参数为：" + jsonObject);

        String visitResult = checkMethod(providerCode, method, jsonObject,uuid).toString();

        if ("CSM180706_1628".equals(method)){
            providerCode = "zhongchengxin";
        }if ("CSM180706_1731".equals(method)){
            providerCode = "baichuan";
        }
        analysisInformation(visitResult, providerCode, bizParams, method, account, null, null,uuid);

        return visitResult;
    }
}
