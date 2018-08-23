package com.bonc.credit.service.zhongchengxin;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static java.net.Proxy.Type.HTTP;


public class HttpsHelper {
    /**
     * 获得KeyStore.
     *
     * @param keyStorePath 密钥库路径
     * @param password     密码
     * @return 密钥库
     * @throws Exception
     */
    public static KeyStore getKeyStore(String password, String keyStorePath)
            throws Exception {
        // 实例化密钥库  
        KeyStore ks = KeyStore.getInstance("JKS");
        // 获得密钥库文件流  
        FileInputStream is = new FileInputStream(keyStorePath);
        // 加载密钥库  
        ks.load(is, password.toCharArray());
        // 关闭密钥库文件流  
        is.close();
        return ks;
    }

    /**
     * 获得SSLSocketFactory.
     *
     * @param password       密码
     * @param keyStorePath   密钥库路径
     * @param trustStorePath 信任库路径
     * @return SSLSocketFactory
     * @throws Exception
     */
    public static SSLContext getSSLContext(String password,
                                           String keyStorePath, String trustStorePath) throws Exception {
        // 实例化密钥库  
        KeyManagerFactory keyManagerFactory = KeyManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
        // 获得密钥库  
        KeyStore keyStore = getKeyStore(password, keyStorePath);
        // 初始化密钥工厂  
        keyManagerFactory.init(keyStore, password.toCharArray());

        // 实例化信任库  
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // 获得信任库  
        KeyStore trustStore = getKeyStore(password, trustStorePath);
        // 初始化信任库  
        trustManagerFactory.init(trustStore);
        // 实例化SSL上下文  
        SSLContext ctx = SSLContext.getInstance("TLS");
        // 初始化SSL上下文  
        ctx.init(keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(), null);
        // 获得SSLSocketFactory  
        return ctx;
    }

    /**
     * 初始化HttpsURLConnection.
     *
     * @param password       密码
     * @param keyStorePath   密钥库路径
     * @param trustStorePath 信任库路径
     * @throws Exception
     */
    public static void initHttpsURLConnection(String password,
                                              String keyStorePath, String trustStorePath) throws Exception {
        // 声明SSL上下文  
        SSLContext sslContext = null;
        // 实例化主机名验证接口  
        HostnameVerifier hnv = new MyHostnameVerifier();
        try {
            sslContext = getSSLContext(password, keyStorePath, trustStorePath);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        if (sslContext != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
                    .getSocketFactory());
        }
        HttpsURLConnection.setDefaultHostnameVerifier(hnv);
    }

    /**
     * 发送请求.
     *
     * @param httpsUrl 请求的地址
     * @param xmlStr   请求的数据
     */
    public static String httPost(String httpsUrl, String xmlStr) {
        HttpsURLConnection urlCon = null;
        //始终信任服务器证书
        try {
            trustAllHttpsCertificates();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                return true;
            }
        };
        //使用https协议
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        try {
            urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
            urlCon.setDoInput(true);
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("POST");
            urlCon.setRequestProperty("Content-Length",
                    String.valueOf(xmlStr.getBytes().length));
            urlCon.setUseCaches(false);
            //设置为gbk可以解决服务器接收时读取的数据中文乱码问题  
            urlCon.getOutputStream().write(xmlStr.getBytes("gbk"));
            urlCon.getOutputStream().flush();
            urlCon.getOutputStream().close();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    urlCon.getInputStream()));
            String line;
            StringBuffer buff = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buff.append(line);
            }
            return buff.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }


    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        trustAllCerts[0] = new DefaultTrustManager();
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    public static String httpGet(String httpsUrl) {  
        HttpsURLConnection urlCon = null;  
        //始终信任服务器证书
        try {
			trustAllHttpsCertificates();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                //System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                return true;
            }
        };
        //使用https协议
        HttpsURLConnection.setDefaultHostnameVerifier(hv);

        try {
            urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
            urlCon.setDoInput(true);
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("GET");

            urlCon.setUseCaches(false);

            urlCon.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    urlCon.getInputStream()));
            String line;
            StringBuffer buff = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buff.append(line);
            }
            return buff.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static String httpGetByProxy(String httpsUrl, String proxyHost, int proxyPort) {
        HttpsURLConnection urlCon = null;
        //始终信任服务器证书
        try {
            trustAllHttpsCertificates();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                //System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                return true;
            }
        };
        //使用https协议
        HttpsURLConnection.setDefaultHostnameVerifier(hv);

        try {
            if (proxyHost != null && !"".equals(proxyHost)) {
                Proxy proxy = new Proxy(HTTP, new InetSocketAddress(proxyHost, proxyPort));
                urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection(proxy);
            } else {
                urlCon = (HttpsURLConnection) (new URL(httpsUrl)).openConnection();
            }
            urlCon.setDoInput(true);
            urlCon.setDoOutput(true);
            urlCon.setRequestMethod("GET");

            urlCon.setUseCaches(false);

            urlCon.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    urlCon.getInputStream()));
            String line;
            StringBuffer buff = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buff.append(line);
            }
            return buff.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    //使用自定义证书管理器
    static class DefaultTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    
        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }
    
        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }
    
        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            return;
        }
    
        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            return;
        }
    }

    /**
     * 测试方法.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 密码
        String password = "123456";
        // 密钥库
        String keyStorePath = "tomcat.keystore";
        // 信任库
        String trustStorePath = "tomcat.keystore";
        // 本地起的https服务
        String httpsUrl = "https://localhost:8443/service/httpsPost";
        // 传输文本
        String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><fruitShop><fruits><fruit><kind>萝卜</kind></fruit><fruit><kind>菠萝</kind></fruit></fruits></fruitShop>";
        HttpsHelper.initHttpsURLConnection(password, keyStorePath, trustStorePath);
        // 发起请求
        HttpsHelper.httPost(httpsUrl, xmlStr);

//        String url = "https://api.ccxcredit.com/data-service/telecom/identityverification?name=%E6%9D%8E%E6%B1%9F%E7%90%B3&mobile=18301112168&sign=DCA5AC8C78E33329B9C5831734BCEEFF&account=dongfangguoxin_test&cid=130182198607020020&reqId=BONC1531302178528R935";
//        String str = HttpsHelper.httpGetByProxy(url, "172.16.18.1", 9999);
//        System.out.println(str);
    }

}  