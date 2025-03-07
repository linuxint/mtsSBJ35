package com.devkbil.mtssbj.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AbstractAjpProtocol;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfiguration {

    @Value("${tomcat.ajp.protocol}")
    String ajpProtocol;
    @Value("${tomcat.ajp.port}")
    int ajpPort;
    @Value("${tomcat.ajp.enable}")
    boolean ajpEnabled;
    @Value("${tomcat.ajp.schema}")
    String ajpSchema;
    @Value("${tomcat.ajp.secure}")
    boolean ajpSecure;
    @Value("${tomcat.ajp.allowtrace}")
    boolean ajpAllowTrace;
    @Value("${tomcat.ajp.secretrequired}")
    boolean ajpSecretRequired;
    @Value("${server.port.http}")
    int httpPort;
    @Value("${server.port}")
    int serverPort;

    @Bean
    public ServletWebServerFactory servletContainer() {

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                //SecurityCollection collection = new SecurityCollection();
                //collection.addPattern("/*");
                //securityConstraint.addCollection(collection);
                //scontext.addConstraint(securityConstraint);
            }
        };

        tomcat.addAdditionalTomcatConnectors(createAjpConnector()); // ajp 포트
        //tomcat.addAdditionalTomcatConnectors(redirectConnector()); // 서비스 기본포트
        //createStandardConnector(8081); // http 추가포트

        return tomcat;
    }

    /**
     * tomcat Multi Port
     * HTTP/1.1 프로토콜을 사용하는 추가 커넥터를 생성합니다.
     *
     * @param port 커넥터가 사용할 포트 번호
     * @return 지정된 포트에서 HTTP/1.1 NIO 프로토콜을 사용하도록 구성된 Tomcat Connector 인스턴스
     */
    private Connector createStandardConnector(int port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(port);
        return connector;
    }

    /**
     * redirect 설정
     *
     * @return connector
     */
    private Connector redirectConnector() {

        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");

        connector.setScheme(ajpSchema);
        connector.setPort(httpPort);
        connector.setRedirectPort(serverPort);

        return connector;
    }

    /**
     * ajpConnector
     *
     * @return connector
     */
    private Connector createAjpConnector() {

        Connector ajpConnector = new Connector(ajpProtocol);

        ajpConnector.setPort(ajpPort);
        ajpConnector.setSecure(ajpSecure);
        ajpConnector.setAllowTrace(ajpAllowTrace);
        ajpConnector.setScheme(ajpSchema);
        ((AbstractAjpProtocol) ajpConnector.getProtocolHandler()).setSecretRequired(ajpSecretRequired); // 해당 줄을 추가함

        return ajpConnector;
    }
}
