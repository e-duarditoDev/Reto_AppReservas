package com.appeventos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AppEventosApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppEventosApplication.class, args);
	}

}
