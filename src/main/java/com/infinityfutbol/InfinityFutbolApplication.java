package com.infinityfutbol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class InfinityFutbolApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfinityFutbolApplication.class, args);
	}

}
