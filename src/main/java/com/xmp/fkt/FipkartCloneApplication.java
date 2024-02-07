package com.xmp.fkt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@SpringBootApplication
public class FipkartCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(FipkartCloneApplication.class, args);
		log.info("Running sucessfuly");
		log.error("Error in runnung the application");
		
	}

}
