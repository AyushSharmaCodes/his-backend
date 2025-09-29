package com.ayush.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class HisApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(HisApiGatewayApplication.class, args);
	}

}
