package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;

@SpringBootApplication
@RestController
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    // Этот метод просто возвращает имя того, кто вошел
    @GetMapping("/hello")
    public String hello(Principal principal) {
        return "Привет, " + principal.getName() + "!";
    }
}