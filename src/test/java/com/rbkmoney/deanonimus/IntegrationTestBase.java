package com.rbkmoney.deanonimus;


import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import com.rbkmoney.machinegun.eventsink.SinkEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(classes = DeanonimusApplication.class)
@ContextConfiguration(initializers = IntegrationTestBase.Initializer.class)
public abstract class IntegrationTestBase {

    private static final String TOPIC_NAME = "mg-events-party";

    private static final String CONFLUENT_PLATFORM_VERSION = "5.0.1";

    @ClassRule
    public static ElasticsearchContainer elastic = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.8.0");

    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(CONFLUENT_PLATFORM_VERSION).withEmbeddedZookeeper();


    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @SneakyThrows
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues
                    .of("kafka.bootstrap-servers=" + kafka.getBootstrapServers(),
                            "spring.elasticsearch.rest.uris=" + elastic.getHttpHostAddress())
                    .applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    public static void sendMessage(SinkEvent sinkEvent) {
        createProducer().send(new ProducerRecord<>(TOPIC_NAME, sinkEvent.getEvent().getSourceId(), sinkEvent));
    }

    public static void sendMessages(List<SinkEvent> sinkEvents) {
        final Producer<String, SinkEvent> producer = createProducer();
        sinkEvents.forEach(sinkEvent ->
                producer.send(new ProducerRecord<>(TOPIC_NAME, sinkEvent.getEvent().getSourceId(), sinkEvent)));
    }

    private static Producer<String, SinkEvent> createProducer() {
        final Map<String, Object> configs = KafkaTestUtils.producerProps(kafka.getBootstrapServers());
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, new ThriftSerializer<SinkEvent>().getClass());
        return new KafkaProducer<>(configs);
    }

}
