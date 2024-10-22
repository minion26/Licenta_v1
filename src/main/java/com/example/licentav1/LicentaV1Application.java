package com.example.licentav1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LicentaV1Application {

	public static void main(String[] args) {
		SpringApplication.run(LicentaV1Application.class, args);
	}

}
