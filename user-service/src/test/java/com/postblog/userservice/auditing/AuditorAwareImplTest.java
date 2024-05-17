package com.postblog.userservice.auditing;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

 class AuditorAwareImplTest {

   @Test
   @DisplayName("Test getCurrentAuditor returns non-empty optional")
   void testGetCurrentAuditor_ReturnsNonEmptyOptional_WhenSuccessful() {
     List<GrantedAuthority> authorities = new ArrayList<>();
     authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

     Authentication authentication = new UsernamePasswordAuthenticationToken("Admin", "password", authorities);
     SecurityContextHolder.getContext().setAuthentication(authentication);

     Authentication authInContext = SecurityContextHolder.getContext().getAuthentication();
     assertThat(authInContext).isNotNull();
     assertThat(authInContext.getName()).isEqualTo("Admin");
     assertThat(authInContext.isAuthenticated()).isTrue();

     AuditorAwareImpl auditorAware = new AuditorAwareImpl();

     Optional<String> auditor = auditorAware.getCurrentAuditor();


     assertThat(auditor).contains("Admin").isPresent();

     SecurityContextHolder.clearContext();
   }
 }
