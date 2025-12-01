package za.co.flash.demo.sanitize;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "za.co.flash.demo.sanitize")
@EnableJpaRepositories(basePackages = "za.co.flash.demo.sanitize")
@EnableCaching
public class SanitizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SanitizerApplication.class, args);
	}

}
