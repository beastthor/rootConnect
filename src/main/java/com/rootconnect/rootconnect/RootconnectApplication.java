package com.rootconnect.rootconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class RootconnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(RootconnectApplication.class, args);
	}

}
