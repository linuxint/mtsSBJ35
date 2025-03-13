package com.devkbil.mtssbj.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch Configuration
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.devkbil")
@ComponentScan(basePackages = {"com.devkbil"})
public class EsConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.host}")
    String elasticHost = "localhost";
    @Value("${elasticsearch.port}")
    int elasticPort = 9200;
    @Value("${elasticsearch.scheme}")
    String elasticScheme = "http";
    @Value("${elasticsearch.credentials.id}")
    String elasticCredentialsId = "elastic";
    @Value("${elasticsearch.credentials.passwd}")
    String elasticCredentialsPasswd = "manager";

    /**
     * Spring Data Elasticsearch 클라이언트 설정을 구성합니다.
     * 설정된 호스트, 포트, 인증 정보를 사용하여 ClientConfiguration을 구성합니다.
     *
     * @return 구성된 ClientConfiguration 인스턴스
     */
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
            .connectedTo(elasticHost + ":" + elasticPort)
            .withBasicAuth(elasticCredentialsId, elasticCredentialsPasswd)
            .build();
    }

    /**
     * Elasticsearch 클라이언트를 생성합니다.
     *
     * @return 구성된 ElasticsearchClient 인스턴스
     */
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        final CredentialsProvider credentialProvider = new BasicCredentialsProvider();
        credentialProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(elasticCredentialsId, elasticCredentialsPasswd));

        RestClient restClient = RestClient.builder(
                new HttpHost(elasticHost, elasticPort, elasticScheme))
            .setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialProvider))
            .build();

        ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());

        return new ElasticsearchClient(transport);
    }

    /**
     * ElasticsearchOperations 빈을 생성합니다.
     *
     * @return 구성된 ElasticsearchOperations 인스턴스
     */
    @Bean
    public ElasticsearchOperations elasticsearchOperations() {
        return new ElasticsearchTemplate(elasticsearchClient());
    }

    /**
     * Elasticsearch 서버 실행 여부 확인
     *
     * @return 서버 상태 (true: 실행 중, false: 실행되지 않음)
     */
    public boolean isElasticsearchRunning() {
        try {
            return elasticsearchClient().ping().value();
        } catch (Exception e) {
            return false;
        }
    }
}