package com.devkbil.mtssbj.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.io.IOException;

/**
 * Elasticsearch Configuration
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.devkbil")
@ComponentScan(basePackages = {"com.devkbil"})
public class EsConfig {

    @Value("${elasticsearch.host}")
    String elasticHost = "localhost";
    @Value("${elasticsearch.port}")
    int elasticPort = 9200;
    @Value("${elasticsearch.scheme}")
    String elasticScheme = "http";
    @Value("${elasticsearch.credentials.id}")
    String elasticCredentilsId = "elastic";
    @Value("${elasticsearch.credentials.passwd}")
    String elasticCredentilsPasswd = "manager";

    /**
     * Elasticsearch Connection client
     *
     * @return
     */
    @Bean
    public RestHighLevelClient client() {
        /*
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .withBasicAuth("elastic","manager")
                .build();

        return RestClients.create(clientConfiguration).rest();
         */
        final CredentialsProvider credentialProvider = new BasicCredentialsProvider();
        credentialProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(elasticCredentilsId, elasticCredentilsPasswd));

        return new RestHighLevelClient(
                RestClient.builder(
                    new HttpHost(elasticHost, elasticPort, elasticScheme)
                ).setHttpClientConfigCallback(
                        httpAsyncClientBuilder -> {
                            HttpAsyncClientBuilder httpAsyncClientBuilder1 = httpAsyncClientBuilder.setDefaultCredentialsProvider(
                                    credentialProvider);
                            return httpAsyncClientBuilder1;
                        }
                )
        );

    }

    /**
     * Elasticsearch 서버 실행 여부 확인
     *
     * @param client Elasticsearch RestHighLevelClient 객체
     * @return 서버 상태 (true: 실행 중, false: 실행되지 않음)
     */
    public boolean isElasticsearchRunning(RestHighLevelClient client) {
        try {
            return client.ping(RequestOptions.DEFAULT);
        } catch (IOException e) {
            return false;
        }
    }

}
