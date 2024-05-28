package com.mutu.spring;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootApplication
public class Application {
	
	private static final String BOT_COUNT_STORE = "bot-count-store";
    private static final String BOT_COUNT_TOPIC = "wikimedia.stats.bots";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
	@Bean
    public KafkaProducer producerListener() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "192.168.50.119:9092");
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }
	
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
