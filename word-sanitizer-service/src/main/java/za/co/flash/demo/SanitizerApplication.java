package za.co.flash.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "za.co.flash.demo")
@EntityScan(basePackages = "za.co.flash.demo.sanitize.model")
public class SanitizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SanitizerApplication.class, args);
	}

}
