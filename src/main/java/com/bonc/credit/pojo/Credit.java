package com.bonc.credit.pojo;

/**
 * 天翼征信报文体credit
 * @author xupengfei@chinasofti.com
 * @date 2017/3/31 14:21
 */
public class Credit{

    public Header header;

    public Object body;

    public String mac;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}