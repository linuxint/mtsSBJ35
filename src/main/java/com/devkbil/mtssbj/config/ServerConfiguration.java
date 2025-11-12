package com.devkbil.mtssbj.config;

import org.apache.catalina.webresources.StandardRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class ServerConfiguration implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private static final Logger log = LoggerFactory.getLogger(ServerConfiguration.class);

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        // Context 커스터마이징
        factory.addContextCustomizers((TomcatContextCustomizer) context -> {
            StandardRoot standardRoot = new StandardRoot(context);
            standardRoot.setCachingAllowed(true);
            standardRoot.setCacheMaxSize(100 * 1024 * 1024); // 100M
            context.setResources(standardRoot);
            context.setReloadable(true);

            context.addLifecycleListener(event -> {
                if ("before_start".equals(event.getType())) {
                    context.addServletContainerInitializer((c, s) -> {
                        log.debug(s.getRealPath("/WEB-INF/lib"));
                        log.debug(s.getContextPath());
                    }, null);
                }
            });
        });

        // Connector 커스터마이징
        factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector ->
                connector.setProperty("maxHttpResponseHeaderSize", "100000")
        );
    }
}
