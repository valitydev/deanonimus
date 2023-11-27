package dev.vality.deanonimus.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchClientConfig {

    @Bean(destroyMethod = "close")
    public RestClient restClient(OpenSearchProperties openSearchProperties) {
        var httpHost = new HttpHost(openSearchProperties.getHostname(), openSearchProperties.getPort());
        return RestClient.builder(httpHost).build();
    }

    @Bean
    public OpenSearchClient openSearchClient(RestClient restClient) {
        var transport = new RestClientTransport(restClient,
                new JacksonJsonpMapper(new ObjectMapper().registerModule(new JavaTimeModule())));
        return new OpenSearchClient(transport);
    }

}
