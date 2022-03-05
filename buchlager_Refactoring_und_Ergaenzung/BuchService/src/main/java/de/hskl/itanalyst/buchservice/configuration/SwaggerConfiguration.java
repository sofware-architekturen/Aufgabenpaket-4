package de.hskl.itanalyst.buchservice.configuration;

import de.hskl.itanalyst.buchservice.domain.model.BookEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/api/**"))
                .build()
                .ignoredParameterTypes(BookEntity.class)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "REST API für das Buchlager",
                "Software-Architekturen: Buchservice",
                "1.0",
                "Nutzungsbedingungen",
                new Contact("Vitaly Chouliak", null, "vich0002@stud.hs-kl.de"),
                "Apache License 2.0", "https://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());
    }
}
