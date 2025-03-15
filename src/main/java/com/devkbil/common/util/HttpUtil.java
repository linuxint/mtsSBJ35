package com.devkbil.common.util;

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
 * HTTP 작업을 처리하기 위한 유틸리티 클래스입니다.
 * GET 및 POST 요청을 실행하고, 요청 설정 및 헤더 설정을 위한 메소드를 제공합니다.
 *
 * 주요 기능:
 * - HTTP GET/POST 요청 실행
 * - 요청 타임아웃 설정
 * - HTTP 헤더 관리
 * - 문자 인코딩 처리
 */
public class HttpUtil {

    /**
     * HTTP 요청에 대한 기본 설정을 반환합니다.
     *
     * 설정 항목:
     * - 소켓 타임아웃: 5초
     * - 연결 타임아웃: 5초
     * - 연결 요청 타임아웃: 10초
     *
     * @return HTTP 요청 설정이 포함된 RequestConfig 객체
     */
    public static RequestConfig getRequestConfig() {
        RequestConfig requestConfig = RequestConfig.custom()//
                .setSocketTimeout(5000)//
                .setConnectTimeout(5000)//
                .setConnectionRequestTimeout(10000)//
                .build();
        return requestConfig;
    }

    /**
     * 미리 정의된 HTTP 헤더 배열을 반환합니다.
     *
     * 포함된 헤더:
     * - Accept-Encoding: 압축 방식 지정 (gzip, deflate, sdch)
     * - Connection: 연결 유지 설정 (keep-alive)
     * - Accept-Language: 선호하는 언어 설정 (한국어 우선)
     * - Accept: 수용 가능한 컨텐츠 타입
     * - User-Agent: 브라우저 정보
     *
     * @return 기본 HTTP 헤더가 설정된 Header 배열
     */
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
     * 지정된 URL로 POST 요청을 보내고 응답을 반환합니다.
     * 요청 본문에 파라미터를 포함하고, 사용자 정의 헤더와 문자 인코딩을 지정할 수 있습니다.
     *
     * @param url 요청을 보낼 대상 URL
     * @param params 요청 본문에 포함할 파라미터 목록 ({@link NameValuePair} 객체의 리스트)
     * @param headers 요청에 포함할 HTTP 헤더 배열
     * @param charset 응답 본문을 읽을 때 사용할 문자 인코딩
     * @return 응답 본문 문자열
     * @throws RuntimeException POST 요청 실패 또는 실행 중 오류 발생시
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
            throw new RuntimeException("POST 요청이 실패하였습니다.", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                throw new RuntimeException("HTTP 클라이언트 종료 중 오류가 발생하였습니다.", e);
            }
        }
    }

    /**
     * 지정된 URL로 POST 요청을 보내고 응답을 반환합니다.
     * 기본 HTTP 헤더와 UTF-8 문자 인코딩을 사용합니다.
     *
     * @param url 요청을 보낼 대상 URL
     * @param params 요청 본문에 포함할 파라미터 목록 ({@link NameValuePair} 객체의 리스트)
     * @return 응답 본문 문자열
     * @throws RuntimeException POST 요청 실패 또는 실행 중 오류 발생시
     * @see #getHeaders() 사용되는 기본 헤더 정보
     */
    public static String post(String url, List<NameValuePair> params) {
        Header[] headers = HttpUtil.getHeaders();
        return post(url, params, headers, "UTF-8");
    }

    /**
     * 지정된 URL로 POST 요청을 보내고 응답을 반환합니다.
     * UTF-8 문자 인코딩을 사용하며, 사용자 정의 헤더를 지정할 수 있습니다.
     *
     * @param url 요청을 보낼 대상 URL
     * @param params 요청 본문에 포함할 파라미터 목록 ({@link NameValuePair} 객체의 리스트)
     * @param headers 요청에 포함할 HTTP 헤더 배열
     * @return 응답 본문 문자열
     * @throws RuntimeException POST 요청 실패 또는 실행 중 오류 발생시
     */
    public static String post(String url, List<NameValuePair> params, Header[] headers) {
        return post(url, params, headers, "UTF-8");
    }

    /**
     * 지정된 URL로 GET 요청을 보내고 응답을 반환합니다.
     * 기본 HTTP 헤더와 UTF-8 문자 인코딩을 사용합니다.
     *
     * @param url 요청을 보낼 대상 URL
     * @return 응답 본문 문자열
     * @throws RuntimeException GET 요청 실패 또는 실행 중 오류 발생시
     */
    public static String get(String url) {
        return get(url, null, "UTF-8");
    }

    /**
     * 지정된 URL로 GET 요청을 보내고 응답을 반환합니다.
     * 사용자 정의 헤더와 문자 인코딩을 지정할 수 있습니다.
     *
     * @param url 요청을 보낼 대상 URL
     * @param headers 요청에 포함할 HTTP 헤더 배열
     * @param charset 응답 본문을 읽을 때 사용할 문자 인코딩
     * @return 응답 본문 문자열
     * @throws RuntimeException GET 요청 실패 또는 실행 중 오류 발생시
     */
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
            throw new RuntimeException("GET 요청이 실패하였습니다.", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                throw new RuntimeException("HTTP 클라이언트 종료 중 오류가 발생하였습니다.", e);
            }
        }
    }

    /**
     * 지정된 URL로 POST 요청을 보내고 응답을 반환합니다.
     * 사용자 정의 헤더, 요청 본문, 문자 인코딩을 지정할 수 있습니다.
     *
     * @param url 요청을 보낼 대상 URL
     * @param headers 요청에 포함할 HTTP 헤더 배열
     * @param requestBody POST 요청에 포함할 본문 내용
     * @param charset 응답 본문을 읽을 때 사용할 문자 인코딩
     * @return 응답 본문 문자열
     * @throws RuntimeException POST 요청 실패 또는 실행 중 오류 발생시
     */
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
            throw new RuntimeException("POST 요청이 실패하였습니다.", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                throw new RuntimeException("HTTP 클라이언트 종료 중 오류가 발생하였습니다.", e);
            }
        }
    }
}