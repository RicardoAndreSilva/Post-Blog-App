package com.postblog.userservice.controllers;

import static com.postblog.userservice.utils.Constants.USER_NOT_FOUND;

import com.postblog.userservice.exceptions.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;


  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if (userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
      return new UsernamePasswordAuthenticationToken(username, password,
          userDetails.getAuthorities());
    } else {

      throw new HttpException(USER_NOT_FOUND, HttpStatus.NOT_FOUND.value());
    }
  }


  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
