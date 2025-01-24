package com.devkbil.mtssbj.common.util;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * HttpUtil maven dependency
 *
 * <pre>
 * <!-- https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient -->
 *  <dependency>
 *    <groupId>org.apache.httpcomponents</groupId>
 *    <artifactId>httpclient</artifactId>
 *    <version>4.5.13</version>
 *  </dependency>
 * </pre>
 */
public class HttpUtil {

    public static RequestConfig getRequestConfig() {
        RequestConfig requestConfig = RequestConfig.custom()//
                .setSocketTimeout(5000)//
                .setConnectTimeout(5000)//
                .setConnectionRequestTimeout(10000)//
                .build();
        return requestConfig;
    }

    public static Header[] getHeaders() {
        List<Header> hederList = new ArrayList<>();
        hederList.add(new BasicHeader("Accept-Encoding", "gzip, deflate, sdch"));
        hederList.add(new BasicHeader("Connection", "keep-alive"));
        hederList.add(new BasicHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4"));
        hederList.add(new BasicHeader("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
        hederList.add(new BasicHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36"));

        Header[] headers = new BasicHeader[5];
        return hederList.toArray(headers);
    }

    /**
     * POST 요청
     */
    public static String post(String url, List<NameValuePair> params, Header[] headers, String charset) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(getRequestConfig());
        httpPost.setHeaders(headers);

        HttpClientContext context = HttpClientContext.create();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            CloseableHttpResponse response = httpclient.execute(httpPost, context);
            try {
                String responseBody = EntityUtils.toString(response.getEntity(), charset);
                return responseBody;
            } finally {
                response.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("POST요청 실패", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                new RuntimeException("POST요청 실패", e);
            }
        }
    }

    public static String post(String url, List<NameValuePair> params) {
        Header[] headers = HttpUtil.getHeaders();
        return post(url, params, headers, "UTF-8");
    }

    public static String post(String url, List<NameValuePair> params, Header[] headers) {
        return post(url, params, headers, "UTF-8");
    }

    public static String get(String url) {
        return get(url, null, "UTF-8");
    }

    public static String get(String url, Header[] headers, String charset) {
        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(getRequestConfig());
        httpget.setHeaders(headers);
        HttpClientContext context = HttpClientContext.create();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = httpclient.execute(httpget, context);
            try {
                String responseBody = EntityUtils.toString(response.getEntity(), charset);
                return responseBody;
            } finally {
                response.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("GET요청에 실패하였습니다.", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                throw new RuntimeException("GET요청에 실패하였습니다.", e);
            }
        }
    }

    public static String post(String url, Header[] headers, String requestBody, String charset) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(getRequestConfig());
        httpPost.setHeaders(headers);

        HttpClientContext context = HttpClientContext.create();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {

            httpPost.setEntity(new StringEntity(String.valueOf(requestBody)));
            CloseableHttpResponse response = httpclient.execute(httpPost, context);
            try {
                String responseBody = EntityUtils.toString(response.getEntity(), charset);
                return responseBody;
            } finally {
                response.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("POST요청 실패", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                new RuntimeException("POST요청 실패", e);
            }
        }
    }
}
