package com.postblog.userservice.configuration;

import com.postblog.userservice.entities.Role;
import com.postblog.userservice.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

  @Autowired
  private RoleRepository roleRepository;

  @PostConstruct
  public void init() {
    if (!roleRepository.findByName("USER").isPresent()) {
      Role userRole = new Role();
      userRole.setName("USER");
      roleRepository.save(userRole);
    }
  }
}
