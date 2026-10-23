package com.example.demo;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

// 1. Поднимаем ВЕСЬ контекст Spring и запускаем РЕАЛЬНЫЙ сервер на случайном порту
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityIntegrationTest {

    // 2. Spring автоматически подставит сюда порт, на котором запустился тестовый сервер
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        // Говорим RestAssured использовать этот случайный порт для всех запросов
        RestAssured.port = port;
    }

    @Test
    void testHelloWithoutAuth_ShouldReturn401() {
        // Проверяем, что без пароля доступ закрыт
        given()
                .when().get("/hello")
                .then()
                .statusCode(401); // Ожидаем Unauthorized
    }

    @Test
    void testHelloWithCorrectAuth_ShouldReturn200() {
        // Проверяем, что с правильным паролем доступ открыт
        given()
                .auth().preemptive().basic("admin", "123") // Передаем логин и пароль
                .when().get("/hello")
                .then()
                .statusCode(200) // Ожидаем успех
                .body(containsString("Привет, admin!")); // Проверяем текст ответа
    }

    @Test
    void testHelloWithWrongAuth_ShouldReturn401() {
        // Проверяем, что с неправильным паролем доступ всё равно закрыт
        given()
                .auth().preemptive().basic("admin", "wrong_password")
                .when().get("/hello")
                .then()
                .statusCode(401);
    }
}