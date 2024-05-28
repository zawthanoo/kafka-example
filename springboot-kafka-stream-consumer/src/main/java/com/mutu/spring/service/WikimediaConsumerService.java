package com.mutu.spring.service;

import java.io.Closeable;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mutu.spring.config.AppConfig;

import jakarta.annotation.PostConstruct;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@Service
public class WikimediaConsumerService {
	private Logger logger = LogManager.getLogger(WikimediaConsumerService.class);
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
    private ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate;
	
	@PostConstruct
	public void init() {
		
		
	}
	
	private Flux<String> wikimedaiSteam;
	private Disposable subscription;

	private Flux<String> initWikimediaStreamData() {
		if(wikimedaiSteam == null) {
			wikimedaiSteam = reactiveKafkaConsumerTemplate
                .receiveAutoAck()
                .doOnCancel(() -> {
                	logger.info("Stop consumming wikimedia stream data");
//                	close();
                })
                .doOnNext(consumerRecord -> {
//                	logger.info("received key={}, value={} from topic={}, offset={}",
//                            consumerRecord.key(),
//                            consumerRecord.value(),
//                            consumerRecord.topic(),
//                            consumerRecord.offset());
                })
                .map(ConsumerRecord::value)
	                .doOnNext(message -> {
				    	JsonObject jsonObject = new Gson().fromJson(message, JsonObject.class);
				    	logger.info("Consume to Kafka, ID: " + jsonObject.get("id") + " : " + jsonObject.get("title"));
	                	
	                })
                .doOnError(throwable -> logger.error("something went wrong while consuming : {}", throwable.getMessage()));
		}
		return wikimedaiSteam;
    }
	
	public Flux<String> getDataSteam() {
		if(wikimedaiSteam == null) {
			initWikimediaStreamData();
			subscription = wikimedaiSteam.subscribe();
		}
		return wikimedaiSteam;
	}
	
	public void destory() {
		subscription.dispose();
		if(subscription.isDisposed()) {
			logger.info("Consume Flux is shutdown");	
		}		
	}
}
