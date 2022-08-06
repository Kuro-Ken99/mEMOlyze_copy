package com.memolyze.config;

import org.passay.PasswordGenerator;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.formLogin(login -> login
        .loginProcessingUrl("/login")
        .loginPage("/login")
        .defaultSuccessUrl("/top", true)
        .failureUrl("/login?error")
        .permitAll()
    ).logout(logout -> logout
        .logoutSuccessUrl("/login")
    ).authorizeHttpRequests(authz -> authz
          .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
            .permitAll()
          .mvcMatchers("/")
            .permitAll()
          .mvcMatchers("/files/**")
            .permitAll()
          .mvcMatchers("/register")
            .permitAll()
          .mvcMatchers("/validate")
            .permitAll()
          .mvcMatchers("/resend")
            .permitAll()
          .mvcMatchers("/address_update")
            .permitAll()
          .mvcMatchers("/pass_reissue")
            .permitAll()
          .mvcMatchers("/pass_validate")
            .permitAll()
          .anyRequest().authenticated()
      );
    
    return http.build();
  }
  
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public PasswordGenerator passwordGenerator() {
    return new PasswordGenerator();
  }
}