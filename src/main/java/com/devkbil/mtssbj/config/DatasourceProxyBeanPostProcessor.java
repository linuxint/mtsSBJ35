package com.devkbil.mtssbj.config;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.logging.Logger;

/**
 * DataSource 빈을 프록시로 감싸 쿼리 로깅 및 느린 쿼리 감지 기능을 추가하는 BeanPostProcessor 구현체입니다.
 *
 * 이 프로세서는 Spring 애플리케이션 컨텍스트에서 DataSource 타입의 빈을 식별하고 ProxyFactory를 사용해 프록시를 생성합니다.
 * 프록시된 DataSource 빈에는 SQL 쿼리 로깅 및 지정된 임계값에 따른 느린 쿼리 감지 기능이 포함된 인터셉터가 추가됩니다. 
 * 느린 쿼리의 기본 임계값은 5ms입니다.
 *
 * 기능:
 * - INFO 레벨로 SLF4J를 통해 SQL 쿼리를 로깅합니다.
 * - 실행 시간이 설정된 임계값을 초과할 경우 느린 쿼리를 감지하고 WARN 레벨로 로깅합니다.
 * - 어드바이스를 사용해 DataSource 동작을 사용자 정의할 수 있는 기능을 지원합니다.
 *
 * 메서드:
 * - `postProcessBeforeInitialization`: 이 단계에서는 처리 없이 빈을 그대로 반환합니다.
 * - `postProcessAfterInitialization`: DataSource 빈에 대해 쿼리 로깅 및 느린 쿼리 감지 기능을 추가하는 프록시를 생성합니다.
 *   DataSource가 아닌 빈은 수정 없이 그대로 반환됩니다.
 *
 * 내부 클래스:
 * - `ProxyDataSourceInterceptor`: DataSource를 데코레이트하며, SQL 로깅 및 임계값 기반 느린 쿼리 감지 기능을 추가하는
 *   MethodInterceptor입니다.
 *
 * 느린 쿼리 감지를 위해 SLF4JQueryLoggingListener가 특정 로그 레벨 및 기타 사용자 정의 설정으로 구성됩니다.
 */
@Component
public class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {

    private static final Logger logger = Logger.getLogger(DatasourceProxyBeanPostProcessor.class.getName());

    /**
     * 초기화 후 주어진 빈 인스턴스를 후처리합니다.
     * 빈이 {@link DataSource}의 인스턴스인 경우, 해당 빈을 프록시로 감싸
     * SQL 쿼리 로깅 및 느린 쿼리 감지를 활성화하는 {@link DataSourceProxyInterceptor}를 추가합니다.
     *
     * @param bean 초기화된 빈 인스턴스
     * @param beanName 빈의 이름
     * @return 처리된 빈 인스턴스. 원래의 빈이거나 프록시 인스턴스입니다.
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof DataSource) {

            logger.info(() -> "DataSource 빈이 발견되었습니다: " + bean);

            final ProxyFactory proxyFactory = new ProxyFactory(bean);

            proxyFactory.setProxyTargetClass(true);
            proxyFactory.addAdvice(new DataSourceProxyInterceptor((DataSource)bean));

            return proxyFactory.getProxy();
        }
        return bean;
    }

    /**
     * 초기화 로직이 실행되기 전에 주어진 빈 인스턴스를 후처리합니다.
     * 이 메서드는 BeanPostProcessor 인터페이스의 일부로, 초기화 콜백 이전에 새로운 빈 인스턴스를 사용자 정의할 수 있도록 합니다.
     *
     * @param bean 초기화될 새로운 빈 인스턴스
     * @param beanName 빈의 이름
     * @return 수정된 빈 인스턴스. 수정이 없다면 동일한 빈을 반환합니다.
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

}