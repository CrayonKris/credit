package com.bonc.util;

import com.google.common.base.Strings;
import com.google.common.net.MediaType;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * HttpClient工具类
 */
public class HttpClientUtil {
    public final static String CONTENT_TYPE_APPLICATION_JSON= MediaType.JSON_UTF_8.toString();
	
	private HttpClientUtil() {
	}
	
	/**
	 * 
	 * @param url
	 * @param content
	 * @param contentType
	 * @return
	 */
	public static String sendByPost(String url, String content, String contentType) {
		StringEntity resEntity=new StringEntity(content, Consts.UTF_8);
	    List<Header> headers=new ArrayList<Header>();
	    Header header=null;
	    if(!Strings.isNullOrEmpty(contentType)){
	        header=new BasicHeader("Content-type",contentType) ;
	        headers.add(header);
	    }
		
		HttpPost httpPost = null;
	    httpPost = new HttpPost(url);
	    httpPost.setEntity(resEntity);
	    return sendRequest(httpPost,resEntity,headers);
	}
	
	
   /**
    * 
    * @param httpRequest
    * @param entity
    * @param headers
    * @return
    */
	private static String sendRequest(HttpRequestBase httpRequest, HttpEntity entity, List<Header> headers) {

		httpRequest.setHeader("User-Agent","okHttp");
        if(httpRequest instanceof HttpEntityEnclosingRequestBase){
            checkArgument(null!=entity,"HttpEntity请求体不能为空");
            ((HttpEntityEnclosingRequestBase)httpRequest).setEntity(entity);
        }
        if(null!=headers){
            //添加请求头
            for (Header header : headers) {
                httpRequest.addHeader(header);
            }
        }

        //忽略证书
        SSLContext sslContext = SSLContexts.createDefault();

        X509TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] xcs,
                                           String string) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] xcs,
                                           String string) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        try {
            sslContext.init(null, new TrustManager[]{tm}, null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        X509HostnameVerifier hostnameVerifier = new AllowAllHostnameVerifier();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

        Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .build();

        HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();


        CloseableHttpResponse response = null;
        String resString=null;
        try {
            response = httpClient.execute(httpRequest);
            HttpEntity resEntity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            resString= EntityUtils.toString(resEntity,"UTF-8");
            System.err.println(statusCode);
           if(statusCode!= HttpStatus.SC_OK){
               System.out.println("响应码状态不是200");
           }
            return resString;
        } catch (Exception e) {
            throw new RuntimeException(resString, e);
        } finally {
            try {
                if (response != null) {
                        response.close();
                }
                if (httpRequest != null) {
                    httpRequest.releaseConnection();
                }
                if (httpClient != null) {
                        httpClient.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
