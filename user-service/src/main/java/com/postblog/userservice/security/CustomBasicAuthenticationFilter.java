package com.postblog.userservice.security;

import static com.postblog.userservice.utils.Constants.PASSWORD_DOES_NOT_MATCH;
import static com.postblog.userservice.utils.Constants.USER_NOT_FOUND;

import com.postblog.userservice.entities.UserEntity;
import com.postblog.userservice.exceptions.HttpException;
import com.postblog.userservice.repository.UserRepository;
import com.postblog.userservice.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class CustomBasicAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION = "Authorization";
  private static final String BASIC = "Basic ";
  private final UserRepository userRepository;
  private final UserService userService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if (isBasicAuthentication(request)) {
      String base64Credentials = getHeader(request).replace(BASIC, "");
      String[] credentials = decodeBase64(base64Credentials).split(":");
      String username = credentials[0];
      String password = credentials[1];

      if (!request.getRequestURI().equals("/api/users")) {

        UserEntity user = userRepository.findByUsernameFetchRoles(username);

        if (user == null) {
          throw new HttpException(USER_NOT_FOUND, HttpServletResponse.SC_UNAUTHORIZED);
        }

        boolean valid = userService.checkPassword(user.getEmail(), password);

        if (!valid) {
          throw new HttpException(PASSWORD_DOES_NOT_MATCH, HttpServletResponse.SC_UNAUTHORIZED);
        }

        setAuthentication(user);
      }
    }

    filterChain.doFilter(request, response);
  }

  private void setAuthentication(UserEntity user) {
    Authentication authentication = createAuthenticationToken(user);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private Authentication createAuthenticationToken(UserEntity user) {
    UserPrincipal userPrincipal = UserPrincipal.create(user);
    return new UsernamePasswordAuthenticationToken(userPrincipal, null,
        userPrincipal.getAuthorities());
  }

  private String decodeBase64(String base64) {
    byte[] decodeBytes = Base64.getDecoder().decode(base64);
    return new String(decodeBytes);
  }

  private boolean isBasicAuthentication(HttpServletRequest request) {
    String header = getHeader(request);
    return header != null && header.startsWith(BASIC);
  }

  private String getHeader(HttpServletRequest request) {
    return request.getHeader(AUTHORIZATION);
  }
}
