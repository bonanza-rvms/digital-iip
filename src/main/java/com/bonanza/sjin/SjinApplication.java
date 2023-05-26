package com.bonanza.sjin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.bonanza.sjin.config.WsClientConfig;

@SpringBootApplication
@EnableScheduling
public class SjinApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(SjinApplication.class, args);
		WsClientConfig client = ctx.getBean(WsClientConfig.class);
		client.start();

	}
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
}
