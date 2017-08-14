package com.example.api.sample.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(
//	scanBasePackages={
//		"com.example.api.sample.rest",
//		"com.example.common.file.config"
//	},
//	scanBasePackageClasses={
//		com.example.common.file.Application.class
//	}
//)
@SpringBootApplication
public class Application {
	
//	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
