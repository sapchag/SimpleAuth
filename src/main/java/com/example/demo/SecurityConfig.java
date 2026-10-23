package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1. Правило: требовать пароль для всего
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults()); // Включает окно ввода пароля
        return http.build();
    }

    // 2. База пользователей (прямо в коде, без базы данных)
    @Bean
    public UserDetailsService userDetailsService() {
        var user = User.withUsername("admin")
                .password("{noop}123") // {noop} значит пароль не зашифрован
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}