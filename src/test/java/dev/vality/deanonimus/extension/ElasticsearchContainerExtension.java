package dev.vality.deanonimus.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public class ElasticsearchContainerExtension implements BeforeAllCallback, AfterAllCallback {

    private static final String ELASTIC_SEARCH_IMAGE_NAME = "docker.elastic.co/elasticsearch/elasticsearch";
    private static final String ELASTIC_SEARCH_VERSION = "7.8.0";

    public static ElasticsearchContainer ELASTIC_SEARCH;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        ELASTIC_SEARCH = new ElasticsearchContainer(DockerImageName
                .parse(ELASTIC_SEARCH_IMAGE_NAME)
                .withTag(ELASTIC_SEARCH_VERSION));
        ELASTIC_SEARCH.addExposedPort(9200);
        ELASTIC_SEARCH.start();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        ELASTIC_SEARCH.stop();
    }
}
