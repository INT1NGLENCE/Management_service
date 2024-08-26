package management.system;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "management.system.repository")
@EntityScan(basePackages = "management.system.model")
@EnableWebMvc
@OpenAPIDefinition
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}