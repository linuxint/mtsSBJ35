package com.devkbil.common.util;

import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.io.ByteArrayInputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * SSL/TLS 작업을 처리하는 유틸리티 클래스입니다.
 * 특정 인증서로부터 SSL 컨텍스트를 생성하거나 모든 인증서를 신뢰하는 컨텍스트를 생성하는
 * 메서드를 제공합니다.
 * <p>
 * 애플리케이션 내에서 SSL/TLS 작업 설정을 단순화하기 위해 이 클래스가 제공됩니다.
 * 특히, 인증서 검증을 우회하는 메서드를 사용하는 경우, 프로덕션 환경에서는 특별히 주의를 기울여야 합니다.
 */
public class SSLUtils {

    private static final TrustManager[] trustAllCerts = new TrustManager[] {

        new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }
    };

    /**
     * 인증서로부터 SSL 컨텍스트를 생성합니다.
     *
     * @param certificate 바이트 배열로 제공되는 인증서
     * @return 생성된 SSL 컨텍스트
     */
    public static SSLContext createContextFromCaCert(byte[] certificate) {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Certificate trustedCa = factory.generateCertificate(new ByteArrayInputStream(certificate));
            KeyStore trustStore = KeyStore.getInstance("pkcs12");
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", trustedCa);
            SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial(trustStore, null);
            return sslContextBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates and returns an SSLContext instance that trusts all certificates.
     * This method disables certificate validation and can be utilized in scenarios
     * such as testing or development environments where certificate verification
     * is not necessary. Using this method in production is highly discouraged
     * due to potential security vulnerabilities.
     *
     * @return an SSLContext instance configured to trust all certificates
     */
    public static SSLContext createTrustAllCertsContext() {

        SSLContext sslContext;

        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        try {
            sslContext.init(null, trustAllCerts, new SecureRandom());
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        }
        return sslContext;
    }
}