package dev.vality.deanonimus;

import dev.vality.deanonimus.extension.KafkaContainerExtension;
import dev.vality.deanonimus.extension.OpensearchContainerExtension;
import dev.vality.kafka.common.serialization.ThriftSerializer;
import dev.vality.machinegun.eventsink.SinkEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

@Slf4j
@ExtendWith({KafkaContainerExtension.class, OpensearchContainerExtension.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"kafka.topics.wallet.enabled=true", "kafka.topics.identity.enabled=true"})
public abstract class AbstractIntegrationTest {

    public static final String TOPIC_PARTY = "mg-events-party";
    public static final String TOPIC_WALLET = "mg-events-ff-wallet";
    public static final String TOPIC_IDENTITY = "mg-events-ff-identity";

    @DynamicPropertySource
    static void containersProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KafkaContainerExtension.KAFKA::getBootstrapServers);
        registry.add("opensearch.hostname", () -> OpensearchContainerExtension.OPENSEARCH.getHost());
        registry.add("opensearch.port", () -> OpensearchContainerExtension.OPENSEARCH.getFirstMappedPort());
    }

    public static void sendPartyMessages(List<SinkEvent> sinkEvents) {
        sendMessages(TOPIC_PARTY, sinkEvents);
    }

    public static void sendMessages(String topic, List<SinkEvent> sinkEvents) {
        var producer = createProducer();
        sinkEvents.forEach(sinkEvent ->
                producer.send(new ProducerRecord<>(topic, sinkEvent.getEvent().getSourceId(), sinkEvent)));
    }

    private static Producer<String, SinkEvent> createProducer() {
        final Map<String, Object> configs =
                KafkaTestUtils.producerProps(KafkaContainerExtension.KAFKA.getBootstrapServers());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ThriftSerializer.class);
        return new KafkaProducer<>(configs);
    }

}
