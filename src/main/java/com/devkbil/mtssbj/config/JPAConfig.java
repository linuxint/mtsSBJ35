package com.devkbil.mtssbj.config;

import com.devkbil.mtssbj.MtssbjApplication;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;

/**
 * JPA 관련 컴포넌트를 설정하고 관리하는 구성 클래스.
 * 이를 통해 트랜잭션 매니저와 엔티티 매니저 팩토리 등을 Spring 컨텍스트에 통합.
 */
@Configuration
public class JPAConfig {

    /**
     * JPA 트랜잭션 매니저를 설정하는 메서드.
     *
     * @param entityManagerFactory 엔티티 매니저 팩토리 객체
     * @return JpaTransactionManager 객체 반환
     */
    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    /**
     * 엔티티 매니저 팩토리를 생성하는 메서드.
     *
     * @param builder    EntityManagerFactoryBuilder 객체
     * @param dataSource 데이터 소스 객체
     * @return LocalContainerEntityManagerFactoryBean 객체 반환
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
        EntityManagerFactoryBuilder builder, DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = builder
            .dataSource(dataSource)
            .packages(MtssbjApplication.class)
            .persistenceUnit("default")
            .build();
        // 명시적으로 jakarta.persistence.EntityManagerFactory 사용 설정
        factoryBean.setEntityManagerFactoryInterface(EntityManagerFactory.class);
        return factoryBean;
    }
}
