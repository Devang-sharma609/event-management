package com.devang.abhyudaya;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class AbhyudayaApplication {

	public static void main(String[] args) {
		// Load .env file if it exists and set system properties before Spring Boot starts
		Dotenv dotenv = Dotenv.configure()
			.directory(".")
			.ignoreIfMissing()
			.load();
		
		// Set all environment variables as system properties
		dotenv.entries().forEach(entry -> 
			System.setProperty(entry.getKey(), entry.getValue())
		);
		
		// Set the default timezone to UTC to prevent PostgreSQL timezone issues
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		SpringApplication.run(AbhyudayaApplication.class, args);
	}

}
