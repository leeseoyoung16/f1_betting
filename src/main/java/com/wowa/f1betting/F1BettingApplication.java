package com.wowa.f1betting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class F1BettingApplication {

	public static void main(String[] args) {
		SpringApplication.run(F1BettingApplication.class, args);
	}

}
