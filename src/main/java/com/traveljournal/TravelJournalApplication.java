package com.traveljournal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class TravelJournalApplication {

	public static void main(String[] args) {
		SpringApplication.run(TravelJournalApplication.class, args);
	}

}
