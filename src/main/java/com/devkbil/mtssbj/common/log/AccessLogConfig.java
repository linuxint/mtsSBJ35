package com.devkbil.mtssbj.common.log;

import org.apache.catalina.valves.AccessLogValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * 내장된 Tomcat 서버의 액세스 로그 설정을 사용자 정의하기 위한 설정 클래스.
 * 이 클래스는 {@link WebServerFactoryCustomizer} 인터페이스를 구현하여
 * 웹 서버 팩토리에 사용자 정의 구성을 추가할 수 있도록 합니다.
 */
@Configuration
public class AccessLogConfig implements WebServerFactoryCustomizer {

    /**
     * 제공된 WebServerFactory를 사용자 정의하여 추가 서버 설정을 구성합니다.
     * 특히 AccessLogValve를 추가하여 특정 로깅을 무시하도록 설정합니다.
     *
     * @param factory 사용자 정의할 웹 서버 팩토리
     */
    @Override
    public void customize(final WebServerFactory factory) {
        final TomcatServletWebServerFactory containerFactory = (TomcatServletWebServerFactory) factory;

        final AccessLogValve accessLogValve = new AccessLogValve();
        //accessLogValve.setDirectory();
        accessLogValve.setPattern("%{yyyy-MM-dd HH:mm:ss}t\t%s\t%r\t%{User-Agent}i\t%{Referer}i\t%a\t%b");
        //accessLogValve.setDirectory(".");
        //accessLogValve.setSuffix(".log");
        accessLogValve.setCondition("ignoreLogging");
        containerFactory.addContextValves(accessLogValve);
    }
}
