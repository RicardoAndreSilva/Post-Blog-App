package com.postblog.userservice.security;

import com.postblog.userservice.entities.UserEntity;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public class UserPrincipal {

  private final Collection<? extends GrantedAuthority> authorities;

  private UserPrincipal(UserEntity user) {
    this.authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
        .toList();
  }

  public static UserPrincipal create(UserEntity user) {
    return new UserPrincipal(user);
  }
}


