package com.mutu.spring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
public class AppConfig {
	private String bootstrapServers;
	private String wikimediaTopic;
}