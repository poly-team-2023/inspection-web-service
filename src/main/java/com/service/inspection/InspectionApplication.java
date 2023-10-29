package com.service.inspection;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InspectionApplication {

	public static void main(String[] args) {
//		SpringApplication.run(InspectionApplication.class, args);
//		SpringApplication.run(CommandLineRunner.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext context) {
		return args -> {
			System.out.printf("Check App runner");
		};
	}

}
