package com.devkbil.mtssbj.config;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;

import java.lang.reflect.Method;
import java.util.List;

/**
 * ProxyDataSourceInterceptor 클래스는 MethodInterceptor 인터페이스를 구현한 클래스로,
 * 메서드 호출을 가로채고 쿼리 실행을 로깅 및 모니터링하기 위해 Proxy DataSource를 활용합니다.
 * 특히 느린 SQL 쿼리를 식별하는 데 중점을 둡니다.
 *
 * 이 클래스는 DataSource를 래핑하여 쿼리 실행 시간을 모니터링하고
 * 정의된 임계치를 초과하는 느린 SQL을 로깅합니다.
 *
 * 주요 특징:
 * - SLF4J를 사용하여 INFO 레벨로 모든 SQL 쿼리를 로깅합니다.
 * - 밀리초 단위의 정의된 임계치를 초과하는 느린 SQL 쿼리를 식별하고 로깅합니다.
 * - 리스너를 이용하여 커스텀 후처리를 구현합니다.
 *
 * 생성 방식:
 * 생성자는 DataSource 객체를 넣으며 느린 쿼리 감지를 위한
 * SLF4JQueryLoggingListener를 사용자 정의하여 Proxy DataSource를 초기화합니다.
 *
 * 메서드 호출:
 * 이 클래스는 invoke 메서드를 사용하여 원본 DataSource에서 동일한 메서드를 호출하거나,
 * 해당 메서드를 찾지 못한 경우 기본 호출을 처리합니다.
 */
public class DataSourceProxyInterceptor implements MethodInterceptor {

    private final DataSource dataSource;
    private static final long THRESHOLD_MILLIS = 5; // 느린쿼리 임계시간 (밀리초)

    /**
     * 지정된 DataSource를 래핑하는 ProxyDataSourceInterceptor 인스턴스를 생성합니다.
     * 이는 SQL 쿼리를 로깅하고 지정된 실행 시간 임계치를 초과하는 쿼리를
     * 감지 및 로깅하기 위해 사용자 정의 SLF4JQueryLoggingListener로 Proxy DataSource를 초기화합니다.
     *
     * SLF4J를 통한 로깅 기능을 제공하며, 밀리초 단위로 정의된
     * 임계치를 초과하는 느린 SQL이 WARN 레벨로 캡처되고 보고됩니다.
     *
     * @param dataSource 원본 DataSource로, Proxy 인스턴스에 의해 래핑되고 모니터링됩니다.
     */
    public DataSourceProxyInterceptor(final DataSource dataSource) {
        super();

        // 느린쿼리 감지 프로세스
        SLF4JQueryLoggingListener listener = new SLF4JQueryLoggingListener() {

            /**
             * 쿼리 실행 후 호출되는 메서드로, 쿼리 실행 시간이 정의된
             * 임계치를 초과한 경우 쿼리 실행 정보를 로깅합니다.
             * WARN 레벨에서 느린 SQL 문을 강조하여 로깅합니다.
             *
             * @param execInfo {@link ExecutionInfo} 객체로, 쿼리 실행 시간, 성공/실패 여부 등
             *                 쿼리 실행에 대한 세부 정보를 포함합니다.
             * @param queryInfoList 실행된 쿼리에 대한 정보(쿼리 문자열, 매개변수 등)를 포함하는
             *                      {@link QueryInfo} 객체 리스트입니다.
             */
            @Override
            public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
                // 쿼리 실행 시간이 임계치를 초과한 경우만 로깅 로직 호출
                if (THRESHOLD_MILLIS <= execInfo.getElapsedTime()) {
                    logger.info("느린 SQL 감지됨...");
                    super.afterQuery(execInfo, queryInfoList);
                }

            }
        };

        listener.setLogLevel(SLF4JLogLevel.WARN);

        this.dataSource = ProxyDataSourceBuilder.create(dataSource)
            .name("DATA_SOURCE_PROXY")
            .logQueryBySlf4j(SLF4JLogLevel.INFO)
            .multiline()
            .listener(listener) // 임계치 쿼리 확인 수행
            .build();
    }

    /**
     * Proxied DataSource에서 메소드 호출을 인터셉트하며,
     * 원본 DataSource에서 동일한 메서드를 호출하거나,
     * 해당 메서드를 찾지 못할 경우 기본 호출을 진행합니다.
     *
     * @param invocation 가로챈 메서드 호출로,
     *                   메서드와 매개변수에 접근할 수 있도록 제공합니다.
     * @return 원본 DataSource에서 동일 메서드를 호출한 결과 또는,
     *         원래 호출이 진행된 결과를 반환합니다.
     * @throws Throwable 원본 메서드 호출 또는 기본 호출이 예외를 발생시킨 경우 예외가 발생합니다.
     */
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Method proxyMethod = ReflectionUtils.findMethod(this.dataSource.getClass(), invocation.getMethod().getName());

        if (proxyMethod != null) {
            return proxyMethod.invoke(this.dataSource, invocation.getArguments());
        }

        return invocation.proceed();
    }
}
