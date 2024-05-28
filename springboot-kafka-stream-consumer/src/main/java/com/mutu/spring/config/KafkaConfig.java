package com.mutu.spring.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;

import reactor.kafka.receiver.ReceiverOptions;

@Configuration
public class KafkaConfig {
	
	Logger logger = LogManager.getLogger(KafkaConfig.class);

	@Autowired
	private AppConfig appConfig;

	@Bean
	public ReceiverOptions<String, String> kafkaReceiverOptions() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, appConfig.getBootstrapServers());
		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "wikimedia-data-consumer");
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "beginning");

		ReceiverOptions<String, String> basicReceiverOptions = ReceiverOptions.create(config);
		return basicReceiverOptions.subscription(Collections.singletonList("wikimedia.recentchange"))
				.addAssignListener(partitions -> logger.info("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> logger.info("onPartitionsRevoked {}", partitions));
	}

	@Bean
	public ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate(ReceiverOptions<String, String> kafkaReceiverOptions) {
		return new ReactiveKafkaConsumerTemplate<String, String>(kafkaReceiverOptions);
	}
}
