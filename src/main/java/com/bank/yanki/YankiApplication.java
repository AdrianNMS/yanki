package com.bank.yanki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class YankiApplication {

	public static void main(String[] args) {
		SpringApplication.run(YankiApplication.class, args);
	}

}
