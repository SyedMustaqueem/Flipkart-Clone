package com.xmp.fkt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@SpringBootApplication
@Async//enabling a sync in main sync
public class FipkartCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(FipkartCloneApplication.class, args);
		log.info("Running sucessfuly");
		 
	}

}
