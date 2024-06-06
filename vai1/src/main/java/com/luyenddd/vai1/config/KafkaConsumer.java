package com.luyenddd.vai1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.UUID;

@Configuration
public class KafkaConsumer {
    @Bean
    public String kafkaGroupId() {
        return UUID.randomUUID().toString();
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(8); // Set concurrency to the number of cores
        factory.setBatchListener(true); // Enable batch processing
        factory.getContainerProperties().setPollTimeout(3000); // Adjust poll timeout if needed
        return factory;
    }
}
