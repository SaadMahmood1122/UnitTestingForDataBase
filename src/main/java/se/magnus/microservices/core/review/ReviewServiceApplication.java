package se.magnus.microservices.core.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import se.magnus.microservices.util.http.ServiceUtil;

@SpringBootApplication
public class ReviewServiceApplication {

	String port;
	@Bean
	public ServiceUtil serviceUtil() {
		return new ServiceUtil("7003");
	}

	public static void main(String[] args) {
		SpringApplication.run(ReviewServiceApplication.class, args);
	}

}
