package es.codeurjc.webapp17;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springframework.cloud.function.context.config.ContextFunctionCatalogAutoConfiguration.class
})
public class Webapp17Application {

	public static void main(String[] args) {
		SpringApplication.run(Webapp17Application.class, args);
	}

}
