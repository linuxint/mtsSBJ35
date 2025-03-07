package com.devkbil.mtssbj.config;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.webresources.StandardRoot;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ServerConfiguration
    implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        TomcatContextCustomizer tomcatContextCustomizer = context -> {
            StandardRoot standardRoot = new StandardRoot(context);
            standardRoot.setCachingAllowed(true);
            standardRoot.setCacheMaxSize(100 * 1024 * 1024); // 100M
            context.setResources(standardRoot);
            context.setReloadable(true);
            context.addLifecycleListener(
                event -> {
                    if (event.getType().equals("before_start")) {
                        context.addServletContainerInitializer(
                            (c, s) -> {
                                log.debug(s.getRealPath("/WEB-INF/lib"));
                                log.debug(s.getContextPath());
                            },
                            null);
                    }
                });
        };
        factory.addContextCustomizers(tomcatContextCustomizer);
        factory.addConnectorCustomizers(
            new TomcatConnectorCustomizer() {
                @Override
                public void customize(Connector connector) {
                    connector.setProperty("maxHttpResponseHeaderSize", "100000");
                }
            });
    }
}
