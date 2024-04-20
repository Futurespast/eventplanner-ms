package com.eventplanner.events;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class EventsServiceApplication {

	@Bean
	RestTemplate restTemplate()
	{
		return new RestTemplate();
	}
	public static void main(String[] args) {
		SpringApplication.run(EventsServiceApplication.class, args);
	}

}
