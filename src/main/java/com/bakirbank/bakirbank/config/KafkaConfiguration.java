package com.bakirbank.bakirbank.config;


import com.bakirbank.bakirbank.api.request.MoneyProcessFailed;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import com.bakirbank.bakirbank.api.request.MoneyTransferRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@EnableKafka
public class KafkaConfiguration {

    @Bean
    public ProducerFactory<String, MoneyTransferRequest> transactionProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka broker adresi
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, MoneyTransferRequest> transactionKafkaTemplate() {
        KafkaTemplate<String, MoneyTransferRequest> kafkaTemplate = new KafkaTemplate<>(transactionProducerFactory());
        kafkaTemplate.setProducerListener(new TransactionKafkaProducerListener());
        return kafkaTemplate;
    }

    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka broker adresi
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(stringProducerFactory());
        kafkaTemplate.setProducerListener(new StringKafkaProducerListener());
        return kafkaTemplate;
    }

    // Consumer Configuration
    @Bean
    public ConsumerFactory<String, MoneyTransferRequest> transactionConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka broker adresi
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "transfer-group"); // Consumer group ID
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Paket güvenliği
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.bakirbank.bakirbank.api.request.MoneyTransferRequest");
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(MoneyTransferRequest.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MoneyTransferRequest> transactionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MoneyTransferRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(transactionConsumerFactory());
        // DefaultErrorHandler ile ilişkilendir
        factory.setCommonErrorHandler(defaultErrorHandler());
        return factory;
    }


    @Bean
    public DefaultErrorHandler defaultErrorHandler(){
        FixedBackOff fixedBackOff = new FixedBackOff(3000L,0);
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(fixedBackOff);

        return defaultErrorHandler;
    }


    //DEAD LETTER TOPIC
    @Bean
    public DefaultErrorHandler deadLetterErrorHandler() {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                dltKafkaTemplate(),
                (record, ex) -> new TopicPartition("money-process.DLT", record.partition())
        );

        FixedBackOff fixedBackOff = new FixedBackOff(3000L, 3); // 3 kez dene, her 3 saniyede bir
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("Retry attempt: {}, record: {}, exception: {}", deliveryAttempt, record.value(), ex.getMessage());
        });

        return errorHandler;
    }

    @Bean
    public ProducerFactory<String, MoneyProcessFailed> deadLetterProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        ProducerFactory<String, Exception> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
//        return new KafkaTemplate<>(producerFactory);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /*@Bean
    public ConsumerFactory<String,MoneyProcessFailed> deadLetterConsumerFactory(){
        Map<String,Object> configProps = new HashMap<>();
        JsonDeserializer<MoneyProcessFailed> deserializer = new JsonDeserializer<>(MoneyProcessFailed.class);

        deserializer.addTrustedPackages("*");
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG,"dlt-group");

        return new DefaultKafkaConsumerFactory<>(configProps,new StringDeserializer(),deserializer);
    }*/
    @Bean
    public ConsumerFactory<String, MoneyProcessFailed> deadLetterConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "dlt-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(MoneyProcessFailed.class));
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, MoneyProcessFailed> dltKafkaTemplate(){
        return new KafkaTemplate<>(deadLetterProducerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MoneyProcessFailed> dltKafkaListenerContainerFactory(
            ConsumerFactory<String, MoneyProcessFailed> consumerFactory,
            DefaultErrorHandler deadLetterErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, MoneyProcessFailed> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(deadLetterErrorHandler);
        return factory;
    }
}