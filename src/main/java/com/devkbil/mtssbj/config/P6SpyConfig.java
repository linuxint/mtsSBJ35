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
            @Override
            public void onAfterGetConnection(ConnectionInformation connectionInformation, SQLException e) {
                log.info("got connection");
            }

            @Override
            public void onAfterConnectionClose(ConnectionInformation connectionInformation, SQLException e) {
                log.info("connection closed");
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