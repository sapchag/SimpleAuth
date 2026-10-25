package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@Controller
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Привет! Вы успешно вошли.";
    }

    @Bean
    public CustomBasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
        CustomBasicAuthenticationEntryPoint entryPoint = new CustomBasicAuthenticationEntryPoint();
        entryPoint.setRealmName("Demo Realm"); // <-- ЭТО обязательно: иначе будет ошибка при старте
        return entryPoint;
    }

    static class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            String authHeader = request.getHeader("Authorization");
            String login = "неизвестный пользователь";

            if (authHeader != null && authHeader.startsWith("Basic ")) {
                try {
                    String base64Part = authHeader.substring("Basic ".length());
                    byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Part);
                    String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

                    if (credentials.contains(":")) {
                        login = credentials.split(":", 2)[0];
                    }
                } catch (IllegalArgumentException e) {
                    login = "ошибка декодирования";
                }
            }

            String message = "Привет, " + login + ", неверный пароль!";
            System.out.println("[LOG] Ошибка входа: " + message);

            // ВАЖНО: Не ставь response.setStatus и не добавляй заголовок WWW-Authenticate вручную!
            // Родительский класс (BasicAuthenticationEntryPoint) уже сделает это правильно.
            // Нам нужно только записать тело ответа.
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write(message);
        }
    }

}
