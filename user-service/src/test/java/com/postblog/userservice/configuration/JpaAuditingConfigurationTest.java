package com.postblog.userservice.configuration;

import com.postblog.userservice.auditing.AuditorAwareImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.AuditorAware;


@SpringBootTest
class JpaAuditingConfigurationTest {

  @Mock
  private AuditorAwareImpl auditorAware;

  @InjectMocks
  private JpaAuditingConfiguration jpaAuditingConfiguration;

  @Test
  @DisplayName("Test auditor provider bean creation")
  void testAuditorProviderBeanCreation() {
    MockitoAnnotations.openMocks(this);
    AuditorAware<String> auditorAware = jpaAuditingConfiguration.auditorProvider();

    Assertions.assertThat(auditorAware).isNotNull();
  }
}