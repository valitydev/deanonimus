package dev.vality.deanonimus.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaContainerExtension implements BeforeAllCallback, AfterAllCallback {

    private static final String CONFLUENT_IMAGE_NAME = "confluentinc/cp-kafka";
    private static final String CONFLUENT_PLATFORM_VERSION = "5.0.1";

    public static KafkaContainer KAFKA;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        KAFKA = new org.testcontainers.containers.KafkaContainer(DockerImageName
                .parse(CONFLUENT_IMAGE_NAME)
                .withTag(CONFLUENT_PLATFORM_VERSION))
                .withEmbeddedZookeeper();
        KAFKA.start();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        KAFKA.stop();
    }
}
