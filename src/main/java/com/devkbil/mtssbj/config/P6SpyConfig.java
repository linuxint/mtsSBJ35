package com.devkbil.mtssbj.config;

import com.github.gavlyukovskiy.boot.jdbc.decorator.dsproxy.ConnectionIdManagerProvider;
import com.p6spy.engine.common.ConnectionInformation;
import com.p6spy.engine.event.JdbcEventListener;

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

import lombok.extern.slf4j.Slf4j;

/**
 * P6Spy 데이터베이스 모니터링 및 로깅을 위한 설정 클래스입니다.
 * JDBC 이벤트 리스너, 쿼리 실행 리스너, 파라미터 변환기 등을 설정합니다.
 */
@Configuration
@Slf4j
public class P6SpyConfig {

    /**
     * P6Spy 이벤트 리스너를 생성합니다.
     * SQL 실행과 관련된 이벤트를 처리하고 로깅합니다.
     *
     * @return 커스텀 P6Spy 이벤트 리스너 인스턴스
     */
    @Bean
    public P6SpyEventListener p6SpyCustomEventListener() {
        return new P6SpyEventListener();
    }

    /**
     * P6Spy SQL 포맷터를 생성합니다.
     * SQL 로그 출력 형식을 커스터마이즈합니다.
     *
     * @return 커스텀 P6Spy 포맷터 인스턴스
     */
    @Bean
    public P6SpyFormatter p6SpyCustomFormatter() {
        return new P6SpyFormatter();
    }

    /**
     * JDBC 이벤트 리스너를 생성합니다.
     * 데이터베이스 연결의 생성과 종료를 모니터링하고 로깅합니다.
     *
     * @return JDBC 이벤트 리스너 인스턴스
     */
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

    /**
     * 쿼리 실행 리스너를 생성합니다.
     * SQL 쿼리 실행 전후의 이벤트를 처리하고 로깅합니다.
     *
     * @return 쿼리 실행 리스너 인스턴스
     */
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

    /**
     * SQL 파라미터 변환기를 생성합니다.
     * SQL 쿼리의 파라미터를 변환하거나 가공합니다.
     *
     * @return 파라미터 변환기 인스턴스
     */
    @Bean
    public ParameterTransformer parameterTransformer() {
        return new MyParameterTransformer();
    }

    /**
     * SQL 쿼리 변환기를 생성합니다.
     * SQL 쿼리문을 변환하거나 가공합니다.
     *
     * @return 쿼리 변환기 인스턴스
     */
    @Bean
    public QueryTransformer queryTransformer() {
        return new MyQueryTransformer();
    }

    /**
     * 데이터베이스 연결 ID 관리자 제공자를 생성합니다.
     * 각 데이터베이스 연결에 대한 고유 식별자를 관리합니다.
     *
     * @return 연결 ID 관리자 제공자 인스턴스
     */
    @Bean
    public ConnectionIdManagerProvider connectionIdManagerProvider() {
        return MyConnectionIdManager::new;
    }
}
