package com.example.demo;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import io.restassured.http.ContentType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

// 1. Поднимаем ВЕСЬ контекст Spring и запускаем РЕАЛЬНЫЙ сервер на случайном порту
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class SecurityIntegrationTest {

    // 2. Spring автоматически подставит сюда порт, на котором запустился тестовый сервер
    @LocalServerPort
    private int port;

    private String token;

    @BeforeEach
    void setUp() {
        // Говорим RestAssured использовать этот случайный порт для всех запросов
        RestAssured.port = port;

        // Получаем токен для тестов
        token = given()
                .contentType(ContentType.JSON)
                .body(new App.LoginRequest("admin", "123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    @Test
    void testHelloWithoutAuth_ShouldReturn403() {
        // Проверяем, что без токена доступ закрыт (403 Forbidden)
        given()
                .when().get("/hello")
                .then()
                .statusCode(403);
    }

    @Test
    void testHelloWithCorrectAuth_ShouldReturn200() {
        // Проверяем, что с правильным токеном доступ открыт
        given()
                .header("Authorization", "Bearer " + token)
                .when().get("/hello")
                .then()
                .statusCode(200) // Ожидаем успех
                .body(containsString("Привет, admin!")); // Проверяем текст ответа
    }

    @Test
    void testHelloWithInvalidToken_ShouldReturn403() {
        // Проверяем, что с невалидным токеном доступ закрыт (403 Forbidden)
        given()
                .header("Authorization", "Bearer invalid_token_here")
                .when().get("/hello")
                .then()
                .statusCode(403);
    }

    @Test
    void testLoginWithWrongPassword_ShouldReturn401() {
        // Проверяем, что с неправильным паролем токен не выдаётся
        given()
                .contentType(ContentType.JSON)
                .body(new App.LoginRequest("admin", "wrong_password"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }
}
