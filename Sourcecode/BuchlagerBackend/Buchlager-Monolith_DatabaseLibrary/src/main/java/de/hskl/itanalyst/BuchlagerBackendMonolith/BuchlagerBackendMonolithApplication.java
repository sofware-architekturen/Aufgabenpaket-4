package de.hskl.itanalyst.BuchlagerBackendMonolith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BuchlagerBackendMonolithApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuchlagerBackendMonolithApplication.class, args);
	}

}
