package com.postblog.postservice.configuration;

import com.postblog.postservice.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-sensitive.properties")
public class PostConfig {

  @Bean
  public ModelMapper modelMapperBeanComment() {
    return new ModelMapper();
  }

  @Bean
  public ModelMapper modelMapperBeanPost() {
    return new ModelMapper();
  }

  @Bean
  public PostService postServiceBean() {
    return new PostService();
  }
}
