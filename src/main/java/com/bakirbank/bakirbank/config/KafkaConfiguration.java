package com.bakirbank.bakirbank.config;


import com.bakirbank.bakirbank.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ErrorHandlingUtils;
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

    /*@Bean
    public DefaultErrorHandler defaultErrorHandler() {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                dltKafkaTemplate(), // Burada doğru KafkaTemplate sağlanmalı
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition())
        );

        FixedBackOff fixedBackOff = new FixedBackOff(1000L, 3); // 1 saniye arayla 3 kez deneme

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, fixedBackOff);

        // Özel durumlar için loglama
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("Hata işleniyor! Record: {}, Attempt: {}, Exception: {}",
                    record.value(), deliveryAttempt, ex.getMessage());
        });

        // İşlenemeyen mesajları loglamak veya farklı bir aksiyon almak için
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

        return errorHandler;
    }*/

    /*@Bean
    public KafkaTemplate<String, Exception> dltKafkaTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        ProducerFactory<String, Exception> producerFactory = new DefaultKafkaProducerFactory<>(configProps);
        return new KafkaTemplate<>(producerFactory);
    }*/

    /*@Bean
    public ConsumerFactory<String, Exception> stringConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "dlt-group");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }*/

    /*@Bean
    public ConcurrentKafkaListenerContainerFactory<String, Exception> dltKafkaListenerContainerFactory(
            ConsumerFactory<String, Exception> consumerFactory,
            DefaultErrorHandler defaultErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Exception> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(defaultErrorHandler);
        return factory;
    }*/

}