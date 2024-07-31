package com.splitterorg.splitter.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  public SecurityFilterChain configure(HttpSecurity chainBuilder) throws  Exception {
    chainBuilder.csrf().ignoringRequestMatchers("/api/**");
    chainBuilder.authorizeHttpRequests(configurer -> configurer
            .requestMatchers("/api/**").permitAll()
            .anyRequest().authenticated())
        .oauth2Login();
    return chainBuilder.build();
  }

}
