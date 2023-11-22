package dev.vality.deanonimus.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("LineLength")
public class OpenSearchClientConfig {

    @Bean(destroyMethod = "close")
    public RestClient restClient(OpenSearchProperties openSearchProperties) {
        var httpHost = new HttpHost(openSearchProperties.getHostname(), openSearchProperties.getPort(), "http");
        return RestClient.builder(httpHost).build();
    }

    @Bean
    public OpenSearchClient openSearchClient(RestClient restClient) {
        var transport = new RestClientTransport(restClient,
                new JacksonJsonpMapper(new ObjectMapper().registerModule(new JavaTimeModule())));
        return new OpenSearchClient(transport);
    }

}