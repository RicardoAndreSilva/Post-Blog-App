package com.postblog.postservice.auditing;

import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuditorAwareImplTest {

  @Test
  @DisplayName("Test getCurrentAuditor returns no-empty optional")
  void testGetCurrentAuditor_ReturnsNonEmptyOptional_WhenSuccessful() {
    AuditorAwareImpl auditorAware = new AuditorAwareImpl();

    Optional<String> auditor = auditorAware.getCurrentAuditor();

    Assertions.assertThat(auditor.get()).contains("Admin");
    Assertions.assertThat(auditor).isPresent();
  }
}