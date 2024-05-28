package com.mutu.spring.controller;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mutu.spring.service.EventProducer;
import com.mutu.spring.service.WikimediaProducerService;

import reactor.core.publisher.Flux;

@RestController
public class EventController {
	private Logger logger = LogManager.getLogger(EventController.class);
	
	@Autowired
	private EventProducer eventProducer;
	@Autowired
	private WikimediaProducerService wikimediaProducerService;
	
	@PostMapping("/publish")
	public String sendEvent(@RequestParam("event") String event) {
		eventProducer.send(event);
		return "Sent event: " + event;
	}

	@GetMapping("/error")
	public String testError() {
		eventProducer.error();
		return "Success";
	}

	@GetMapping(path = "/wikimedia-stream-data", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<String> getWikiSteamData() {
		return WebClient.create()
			.get()
			.uri("https://stream.wikimedia.org/v2/stream/recentchange")
			.retrieve()
			.bodyToFlux(String.class)
			.delaySubscription(Duration.ofSeconds(5))
			.repeat();
	}

	
	@GetMapping(path= "/wikimedia-stream-data-produce")
	public String produceSteamDAta() {
		wikimediaProducerService.produceWikimediaStreamData();
		return "Wikimedai data stream is producing to kafka";
	}
	
	@GetMapping(path= "/stop-produce")
	public String stopProduce() {
		wikimediaProducerService.destory();
		return "Wikimedai data producing is stop";
	}
}
