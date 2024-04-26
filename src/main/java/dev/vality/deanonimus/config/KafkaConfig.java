package dev.vality.deanonimus.config;

import dev.vality.deanonimus.kafka.serde.SinkEventDeserializer;
import dev.vality.kafka.common.util.ExponentialBackOffDefaultErrorHandlerFactory;
import dev.vality.machinegun.eventsink.MachineEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;
    @Value("${kafka.topics.party-management.consumer.group-id}")
    private String partyConsumerGroup;
    @Value("${kafka.topics.wallet.consumer.group-id}")
    private String walletConsumerGroup;
    @Value("${kafka.topics.identity.consumer.group-id}")
    private String identityConsumerGroup;
    @Value("${kafka.consumer.party-management.concurrency}")
    private int partyConcurrency;
    @Value("${kafka.topics.wallet.consumer.concurrency}")
    private int walletConcurrency;
    @Value("${kafka.topics.wallet.consumer.concurrency}")
    private int identityConcurrency;

    @Bean
    public Map<String, Object> consumerConfigs() {
        return createConsumerConfig(SinkEventDeserializer.class);
    }

    private <T> Map<String, Object> createConsumerConfig(Class<T> clazz) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, clazz);
        return props;
    }

    @Bean
    public ConsumerFactory<String, MachineEvent> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public KafkaListenerContainerFactory
            <ConcurrentMessageListenerContainer<String, MachineEvent>> partyManagementContainerFactory() {
        Map<String, Object> configs = consumerConfigs();
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, partyConsumerGroup);
        ConsumerFactory<String, MachineEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(configs);
        return createConcurrentFactory(consumerFactory, partyConcurrency);
    }

    @Bean
    public KafkaListenerContainerFactory
            <ConcurrentMessageListenerContainer<String, MachineEvent>> walletContainerFactory() {
        var configs = consumerConfigs();
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, walletConsumerGroup);
        var consumerFactory = new DefaultKafkaConsumerFactory<String, MachineEvent>(configs);
        return createConcurrentFactory(consumerFactory, walletConcurrency);
    }

    @Bean
    public KafkaListenerContainerFactory
            <ConcurrentMessageListenerContainer<String, MachineEvent>> identityContainerFactory() {
        var configs = consumerConfigs();
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, identityConsumerGroup);
        var consumerFactory = new DefaultKafkaConsumerFactory<String, MachineEvent>(configs);
        return createConcurrentFactory(consumerFactory, identityConcurrency);
    }

    private KafkaListenerContainerFactory
            <ConcurrentMessageListenerContainer<String, MachineEvent>> createConcurrentFactory(
            ConsumerFactory<String, MachineEvent> consumerFactory, int threadsNumber) {
        ConcurrentKafkaListenerContainerFactory<String, MachineEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        initFactory(consumerFactory, threadsNumber, factory);
        return factory;
    }

    private <T> void initFactory(ConsumerFactory<String, T> consumerFactory, int threadsNumber,
                                 ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setCommonErrorHandler(kafkaErrorHandler());
        factory.setConcurrency(threadsNumber);
    }

    public CommonErrorHandler kafkaErrorHandler() {
        return ExponentialBackOffDefaultErrorHandlerFactory.create();
    }

}
