package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

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

        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);
    }
}
