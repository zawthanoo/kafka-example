package com.mutu.spring.service;

import java.time.Duration;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mutu.spring.config.AppConfig;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Component
public class WikimediaProducerService {
	private Logger logger = LogManager.getLogger(WikimediaProducerService.class);
	
	@Autowired
	private KafkaTemplate<String, String> kafkaProducer;
	@Autowired
	private AppConfig appConfig;
	
	
	private Flux<ServerSentEvent<String>> wikimediaEventStream;
	private Disposable subscription;
	
	
	public void produceWikimediaStreamData() {
		if(wikimediaEventStream == null) {
			ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<ServerSentEvent<String>>() {};
		    wikimediaEventStream = WebClient.create()
					.get()
					.uri("https://stream.wikimedia.org/v2/stream/recentchange")
					.retrieve()
					.bodyToFlux(type)
					.delaySubscription(Duration.ofSeconds(5))
					.repeat();
		    subscription= wikimediaEventStream
	    		.doOnCancel(() -> {
                	logger.info("Stop producing wikimedia stream data to kafka");
                	kafkaProducer.destroy();
                })
	    		.doOnNext(content -> {
			    	String jsonString = content.data();
			    	if(!StringUtils.isEmpty(jsonString)) {
				    	JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
				    	logger.info("Produce to Kafka, ID: " + jsonObject.get("id") + " : " + jsonObject.get("title"));
				    	kafkaProducer.send(new ProducerRecord<>(appConfig.getWikimediaTopic(), jsonString));
			    	}
	    		})
	    		.subscribe(
			    content -> {
			    },
			    error -> {
			    	logger.error("Error receiving SSE: {}", error);
			    },
			    () -> {
			    	logger.info("Completed!!!");
			    }
	        );			
		}
	}
	
	public void destory() {
		subscription.dispose();
		if(subscription.isDisposed()) {
			logger.info("Proceed Flux is shutdown");	
		}
	}
}
