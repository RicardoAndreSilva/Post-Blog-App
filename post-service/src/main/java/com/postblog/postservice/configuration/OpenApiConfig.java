package com.postblog.postservice.configuration;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(info());
  }

  private Info info() {
    return new Info()
        .title("PostBlog API")
        .description("API for managing posts and blog content in PostsBlog application")
        .version("v1")
        .license(
            new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
        .contact(new Contact().name("Ricardo .S"));
  }
}