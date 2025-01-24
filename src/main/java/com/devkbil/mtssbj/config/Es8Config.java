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
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Elasticsearch Configuration
 */
//@Configuration
//@EnableElasticsearchRepositories(basePackages = "com.devkbil")
//@ComponentScan(basePackages = {"com.devkbil"})
public class Es8Config {

    @Value("${elasticsearch.host}")
    String ELASTIC_HOST = "localhost";
    @Value("${elasticsearch.port}")
    int ELASTIC_PORT = 9200;
    @Value("${elasticsearch.scheme}")
    String ELASTIC_SCHEME = "http";
    @Value("${elasticsearch.credentials.id}")
    String ELASTIC_CREDENTILS_ID = "elastic";
    @Value("${elasticsearch.credentials.passwd}")
    String ELASTIC_CREDENTILS_PASSWD = "manager";

    /**
     * Elasticsearch Connection client
     *
     * @return
     */
    @Bean
    public ElasticsearchClient client() {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ELASTIC_CREDENTILS_ID, ELASTIC_CREDENTILS_PASSWD));
        RestClient restClient = RestClient.builder(new HttpHost(ELASTIC_HOST, ELASTIC_PORT, ELASTIC_SCHEME))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                }).build();

        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient elasticsearchClient = new ElasticsearchClient(transport);

        return elasticsearchClient;
    }
}