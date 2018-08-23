package com.bonc.credit.service.shuzun;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.bonc.util.HttpRequest;
import com.bonc.util.MD5Builder;

/**
 * 数尊服务类
 *
 * @author zhijie.ma
 * @date 2018年6月20日
 */
public class ShuZunHelper {
    // 测试账号
//    public static final String accountID = "bonc_test";
//    public static final String privateKey = "e961b261-8543-4256-87f3-45e0a57b886d";
//    public static final String url = "http://test.shuzunbao.com/api/v3/searchreport";

    // 正式账号
	public static final String accountID = "bonc";
	public static final String privateKey = "ba31ae47-2be5-46f7-8769-d2a6663d924c";
	public static final String url = "http://openapi.shuzunbao.com/api/v3/searchreport";


    /**
     * 日期格式
     */
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 获取签名
     *
     * @param method 访问的方法
     * @param map    业务参数
     * @return
     */
    public static String getSign(Map<String, String> map) {

        StringBuffer sb = new StringBuffer();

        Object[] keys = map.keySet().toArray();
        Arrays.sort(keys);

        for (Object key : keys) {
            sb.append(key).append(map.get(key));
        }

        sb.append(privateKey);

        return MD5Builder.EncoderByMd5(sb.toString());
    }

    /**
     * 获取最终的url
     *
     * @param sign
     * @param map
     * @param method
     * @return
     */
    public static String getUrlResult(String sign, Map<String, String> map) {
        map.put("sign", sign);

        String urlResult = HttpRequest.getUrl(map, url);

        return urlResult;
    }


    /**
     * 获取tranNo
     * 格式建议  yyyyMMddHHmmss+10位随机字符串
     *
     * @return
     */
    private static String getTranNo() {
        return DATE_FMT.format(new Date()) + UUID.randomUUID().toString().substring(0, 10);
    }


    /**
     * 数尊码表
     *
     * @param code
     * @return
     */
    public static String getCodeTable(String code) {
        switch (code) {
            case "0000":
                return "成功";
            case "1000":
                return "部分请求成功";
            case "2006":
                return "请求没有查询到结果";
            case "1001":
                return "请求失败";
            case "2000":
                return "请求账号不存在";
            case "2001":
                return "请求帐号被冻结";
            case "2002":
                return "请求没有此接口访问权限";
            case "2003":
                return "请求请求的资源不存在";
            case "2004":
                return "请求参数为空或格式错误";
            case "2005":
                return "请求验签失败";
            case "2007":
                return "每秒请求次数超过限制";
            case "2008":
                return "请求重复的请求";
            case "2009":
                return "请求参数中存在无效数据";
            case "2010":
                return "权限不足";
            case "2011":
                return "请求账号余额不足";
            case "2012":
                return "剩余次数不足";
            case "2013":
                return "请求url不正确";
            case "2014":
                return "请求ip不正确";
            case "2015":
                return "请求select参数为空或格式不对";
            case "2016":
                return "请求的查询包ID不存在";
            case "2017":
                return "对查询包的权限不足";
            case "2018":
                return "多个非必选参数中必须有一个";
            case "2019":
                return "电信数据源错误";
            case "2020":
                return "移动数据源错误";
            case "2021":
                return "联通数据源错误";
            case "2022":
                return "银联数据源错误";
            case "2023":
                return "其他数据源错误";
            case "2024":
                return "浙江移动接口返回错误";
            case "2026":
                return "数据请求超时";
            case "2031":
                return "该指标不支持电信号码查询";
            case "2032":
                return "该指标不支持移动号码查询";
            case "2033":
                return "该指标不支持联通号码查询";
            case "2034":
                return "全国交通违章查询，数据源错误";
            case "2035":
                return "该指标不支持的此手机号段查询";
            case "3000":
                return "上传文件过大";
            case "3001":
                return "数据正在处理中";
            case "3002":
                return "无效信息";
            case "4001":
                return "银行卡账号为空或格式不对";
            case "4002":
                return "身份证号码为空或格式不对";
            case "4003":
                return "组织机构代码为空或格式不对";
            case "4004":
                return "消费ID为空或格式不对";
            case "4005":
                return "车牌号为空或格式不对";
            case "4006":
                return "车架号为空或格式不对";
            case "4007":
                return "发动机号为空或格式不对";
            case "4008":
                return "营业执照号码为空或格式不对";
            case "4009":
                return "固话号码为空或格式不对";
            case "4010":
                return "手机号码为空或格式不对";
            case "4011":
                return "车辆类型为空或格式不对";
            case "4012":
                return "姓名为空或格式不对";
            case "4013":
                return "月份为空或格式不对";
            case "4014":
                return "查询类型为空或格式不对";
            case "4015":
                return "组织代码名称为空或格式不对";
            case "4016":
                return "param为空或格式不对";
            case "4017":
                return "请求未完全执行成功";
            case "4018":
                return "企业注册号格式不对";
            case "4019":
                return "查询日期格式不正确";
            case "4020":
                return "查询时间格式不正确";
            case "4021":
                return "查询地址格式不正确";
            case "9999":
                return "系统错误";
            default:
                return "超出码表范围";
        }
    }

}
