package com.devkbil.mtssbj.config;

import com.github.gavlyukovskiy.boot.jdbc.decorator.dsproxy.ConnectionIdManagerProvider;
import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;

import lombok.extern.slf4j.Slf4j;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.QueryExecutionListener;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.QueryTransformer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Configuration
@Slf4j
public class P6SpyConfig {

    @Bean
    public P6SpyEventListener p6SpyCustomEventListener() {
        return new P6SpyEventListener();
    }

    @Bean
    public P6SpyFormatter p6SpyCustomFormatter() {
        return new P6SpyFormatter();
    }

    @Bean
    public JdbcEventListener myListener() {
        return new JdbcEventListener() {

            private void logConnection(String msg, SQLException ex) {
                String message = Optional.ofNullable(ex).map(Throwable::getMessage).orElse("ex is null");
                log.info(msg, message);
            }

            @Override
            public void onAfterGetConnection(
                ConnectionInformation connectionInformation, SQLException ex) {
                logConnection("got connection : {}", ex);
            }

            @Override
            public void onAfterConnectionClose(
                ConnectionInformation connectionInformation, SQLException ex) {
                logConnection("connection closed : {}", ex);
            }
        };
    }

    @Bean
    public QueryExecutionListener queryExecutionListener() {
        return new QueryExecutionListener() {
            @Override
            public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                log.info("beforeQuery");
            }

            @Override
            public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                log.info("afterQuery");
            }
        };
    }

    @Bean
    public ParameterTransformer parameterTransformer() {
        return new MyParameterTransformer();
    }

    @Bean
    public QueryTransformer queryTransformer() {
        return new MyQueryTransformer();
    }

    @Bean
    public ConnectionIdManagerProvider connectionIdManagerProvider() {
        return MyConnectionIdManager::new;
    }
}
