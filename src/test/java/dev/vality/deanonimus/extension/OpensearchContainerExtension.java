package dev.vality.deanonimus.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

public class OpensearchContainerExtension implements BeforeAllCallback, AfterAllCallback {

    public static GenericContainer<?> OPENSEARCH = new GenericContainer<>(
            DockerImageName.parse("opensearchproject/opensearch").withTag("1.2.4")
    );

    @Override
    public void afterAll(ExtensionContext context) {
        OPENSEARCH.stop();
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        OPENSEARCH.withExposedPorts(9200, 9600);
        OPENSEARCH.setWaitStrategy((new HttpWaitStrategy())
                .forPort(9200)
                .forStatusCodeMatching(response -> response == 200 || response == 401));
        OPENSEARCH.withEnv("discovery.type", "single-node");
        OPENSEARCH.withEnv("DISABLE_INSTALL_DEMO_CONFIG", "true");
        OPENSEARCH.withEnv("DISABLE_SECURITY_PLUGIN", "true");
        OPENSEARCH.start();
    }
}
