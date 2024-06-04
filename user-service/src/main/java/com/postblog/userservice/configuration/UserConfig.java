package com.postblog.userservice.configuration;

import com.postblog.userservice.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class UserConfig {

  @Bean
  public ModelMapper modelMapperBean() {
    return new ModelMapper();
  }

  @Bean
  public UserService userServiceBean() {
    return new UserService();
  }
}
