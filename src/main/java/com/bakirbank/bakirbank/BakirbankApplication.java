package com.bakirbank.bakirbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class BakirbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BakirbankApplication.class, args);
	}

}
