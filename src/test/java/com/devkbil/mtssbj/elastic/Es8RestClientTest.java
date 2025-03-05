package com.devkbil.mtssbj.elastic;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.IndicesResponse;
import co.elastic.clients.elasticsearch.indices.*;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.*;

import java.io.IOException;

public class Es8RestClientTest {
    public static void main(String[] args) {

        // Use basic authentication for the Elasticsearch cluster.
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        // Use the username and password that are specified when you create the Elasticsearch cluster.
        // You can also use the username and password to log on to the Kibana console of the
        // Elasticsearch cluster.
        credentialsProvider.setCredentials(
            AuthScope.ANY, new UsernamePasswordCredentials("elastic", "manager"));
        // Create a Java REST client by using the builder and configure HttpClientConfigCallback for the
        // HTTP client.
        // Specify the public endpoint of the Elasticsearch cluster. You can obtain the endpoint from
        // the Basic Information page of the Elasticsearch cluster in the Elasticsearch console.
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http"))
            .setHttpClientConfigCallback(
                new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                        HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                })
            .build();

        // Create a transmission task by using JSON ObjectMapper.
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // Create a Java API client.
        ElasticsearchClient elasticsearchClient = new ElasticsearchClient(transport);
        // System.out.println("elasticsearchClient = " + elasticsearchClient);

        // Create and delete an index, and view information about all indexes.
        // Create an alias named foo and assign the alias to the index on which write operations need to
        // be performed. This way, all write operations, including indexing, updating, and deleting
        // documents, are automatically routed to the index that uses the alias.
        try {
            // Create an index.
            CreateIndexResponse indexRequest = elasticsearchClient
                .indices()
                .create(
                    createIndexBuilder -> createIndexBuilder
                        .index("mts")
                        .aliases("{foo}", aliasBuilder -> aliasBuilder.isWriteIndex(true)));
            // Check whether the operation that is specified in the IndexRequest request is confirmed by
            // the Elasticsearch cluster.
            boolean acknowledged = indexRequest.acknowledged();
            System.out.println("Index document successfully! " + acknowledged);

            // Delete the index.
            DeleteIndexResponse deleteResponse = elasticsearchClient
                .indices()
                .delete(createIndexBuilder -> createIndexBuilder.index("mts"));
            System.out.println("Delete document successfully! \n" + deleteResponse.toString());

            // View information about all indexes. The information includes the health status, running
            // status, index names, index IDs, number of primary shards, and number of replica shards.
            IndicesResponse indicesResponse = elasticsearchClient.cat().indices();
            indicesResponse
                .valueBody()
                .forEach(
                    info -> System.out.println(
                        info.health()
                            + "\t"
                            + info.status()
                            + "\t"
                            + info.index()
                            + "\t"
                            + info.uuid()
                            + "\t"
                            + info.pri()
                            + "\t"
                            + info.rep()));

            transport.close();
            restClient.close();
        } catch (IOException ioException) {
            // Handle exceptions.
        }
    }
}
