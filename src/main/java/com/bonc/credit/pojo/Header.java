package com.bonc.credit.pojo;

/**
 * 天翼征信报文体header
 * @author xupengfei@chinasofti.com
 * @date 2017/3/31 14:22
 */
public class Header  {

    private String version;

    private Integer testFlag;//0：非测试交易   1：测试交易

    private String authorizationCode;//授权号  授信对象同意查询其信息的序号

    private String activityCode;

    private Integer actionCode;

    private String reqSys;//TODO 请写自己机构代码
    private String reqChannel;
    private String reqTransID;//必须为32位且不重复

    private String reqDate;//取值格式为YYYYMMDD

    private String reqDateTime;//发起方时间戳格式为YYYYMMDD24HHMMSS
    private String rcvSys;
    private String rcvTransID;

    private String rcvDate;
    private String rcvDateTime;//接收方时间戳
    private String rspCode;
    private String rspDesc;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getTestFlag() {
        return testFlag;
    }

    public void setTestFlag(Integer testFlag) {
        this.testFlag = testFlag;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public Integer getActionCode() {
        return actionCode;
    }

    public void setActionCode(Integer actionCode) {
        this.actionCode = actionCode;
    }

    public String getReqSys() {
        return reqSys;
    }

    public void setReqSys(String reqSys) {
        this.reqSys = reqSys;
    }

    public String getReqChannel() {
        return reqChannel;
    }

    public void setReqChannel(String reqChannel) {
        this.reqChannel = reqChannel;
    }

    public String getReqTransID() {
        return reqTransID;
    }

    public void setReqTransID(String reqTransID) {
        this.reqTransID = reqTransID;
    }

    public String getReqDate() {
        return reqDate;
    }

    public void setReqDate(String reqDate) {
        this.reqDate = reqDate;
    }

    public String getReqDateTime() {
        return reqDateTime;
    }

    public void setReqDateTime(String reqDateTime) {
        this.reqDateTime = reqDateTime;
    }

    public String getRcvSys() {
        return rcvSys;
    }

    public void setRcvSys(String rcvSys) {
        this.rcvSys = rcvSys;
    }

    public String getRcvTransID() {
        return rcvTransID;
    }

    public void setRcvTransID(String rcvTransID) {
        this.rcvTransID = rcvTransID;
    }

    public String getRcvDate() {
        return rcvDate;
    }

    public void setRcvDate(String rcvDate) {
        this.rcvDate = rcvDate;
    }

    public String getRcvDateTime() {
        return rcvDateTime;
    }

    public void setRcvDateTime(String rcvDateTime) {
        this.rcvDateTime = rcvDateTime;
    }

    public String getRspCode() {
        return rspCode;
    }

    public void setRspCode(String rspCode) {
        this.rspCode = rspCode;
    }

    public String getRspDesc() {
        return rspDesc;
    }

    public void setRspDesc(String rspDesc) {
        this.rspDesc = rspDesc;
    }
}
