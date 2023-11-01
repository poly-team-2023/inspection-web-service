package com.service.inspection;

import com.service.inspection.document.DocumentProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InspectionApplication {

	static final Logger log =
			LoggerFactory.getLogger(InspectionApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(InspectionApplication.class, args);
		SpringApplication.run(CommandLineRunner.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext context) {
		return args -> {
			DocumentProcessor documentProcessor = new DocumentProcessor();
			documentProcessor.fillTemplate("                                                       Общество с ограниченной\n" +
					"ответственностью «КАНТ»\n" +
					"191023, г. Санкт-Петербург, Банковский пер, дом 3,\n" +
					"литер Б, пом 30-Н, офис 301\n" +
					"ИНН 7804493623\n" +
					"КПП 784001001\n" +
					"Лицензия МКРФ 00546 от 22 февраля 2013 г.,\n" +
					"                 Тел: 8 (812) 777-53-52\n");
			};
		}

}
