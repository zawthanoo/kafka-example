package com.mutu.spring.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

	Logger log = LogManager.getLogger(EventProducer.class);

	public void send(String event) {
		ThreadContext.put("correlationId", "3846548e-f7d6-4265-be58-456db78ad15a");		
		ThreadContext.put("service", "springboot-kafka-producer");
		ThreadContext.put("initrator", "postman");
		log.debug(event);
	}

	public void error() {
		ThreadContext.put("correlationId", "bddf7a18-aa82-421a-9d5e-8b89f935fc5b");		
		ThreadContext.put("service", "springboot-kafka-producer");
		ThreadContext.put("initrator", "postman");
		try {
			String[] arr = new String[5];
			arr[6] = "test";

		} catch (Exception e) {
			log.error("Error Test", e);
		}
	}
}
