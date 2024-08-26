package management.system.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.ui.AbstractSwaggerResourceResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public AbstractSwaggerResourceResolver swaggerResourceResolver(
            SwaggerUiConfigProperties swaggerUiConfigProperties) {
        return new AbstractSwaggerResourceResolver(swaggerUiConfigProperties);
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("management-system")
                .packagesToScan("management.system")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Management System API")
                        .description("Management system API documentation")
                        .version("1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(new Server().url("/actuator")));
    }

    @Bean
    public GroupedOpenApi swaggerUi() {
        return GroupedOpenApi.builder()
                .group("swagger-ui")
                .pathsToMatch("/swagger-ui/**")
                .build();
    }
}