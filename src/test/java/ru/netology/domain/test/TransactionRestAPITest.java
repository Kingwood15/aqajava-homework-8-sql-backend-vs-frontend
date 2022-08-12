package ru.netology.domain.test;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.netology.domain.data.Card;
import ru.netology.domain.data.DataHelper;

import static io.restassured.RestAssured.given;

public class TransactionRestAPITest {
    final private RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            //.setBasePath("/api/v1")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @Test
    void shouldTransferFromCard1ToCard2Test() {

        //авторизуемся
        // Given - When - Then
        // Предусловия
        given()
                .spec(requestSpec) // со спецификацией проще (особенно когда много тестов)
                // Выполняемые действия
                .body(DataHelper.getAuthInfoRestApi())
                .when()
                .post("/api/auth")
                // Проверки
                .then()
                .statusCode(200);

        var testUser = DataHelper.getAuthInfo("vasya");
        String verifyCode = DataHelper.getVerificationCodeFor(testUser);

        //верифицируемся
        String token =
        given()
                .spec(requestSpec)
                .body(DataHelper.getVerificationInfoFor(testUser.getLogin(),verifyCode))
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .path("token");

        //смотрим баланс карт до перевода
        Card[] cardsBefore =
                given() // "дано"
                        .spec(requestSpec) // указываем, какую спецификацию используем
                        .header("Authorization", "Bearer "+ token)
                        .when() // "когда"
                        .get("/api/cards") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .as(Card[].class);

        int balanceCard1 = Integer.parseInt(cardsBefore[0].getBalance());
        int balanceCard2 = Integer.parseInt(cardsBefore[1].getBalance());
        int sum = 100;

        //производим транзакцию
        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .header("Authorization", "Bearer "+ token)
                .body(DataHelper.getTransaction("5559 0000 0000 0001", "5559 0000 0000 0002", sum)) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/transfer") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK

        //смотрим баланс после перевода
        Card[] cardsAfter =
                given() // "дано"
                        .spec(requestSpec) // указываем, какую спецификацию используем
                        .header("Authorization", "Bearer "+ token)
                        .when() // "когда"
                        .get("/api/cards") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .as(Card[].class);

        int actualBalanceCard1 = Integer.parseInt(cardsAfter[0].getBalance());
        int actualBalanceCard2 = Integer.parseInt(cardsAfter[1].getBalance());

        int expectedCard1 = balanceCard1 + sum;
        int expectedCard2 = balanceCard2 - sum;

        //сравнение результата с ожидаемым значением
        Assertions.assertEquals(expectedCard1, actualBalanceCard1);
        Assertions.assertEquals(expectedCard2, actualBalanceCard2);
    }

    @Test
    void shouldTransferFromCard2ToCard1Test() {
        // Given - When - Then
        // Предусловия
        given()
                .spec(requestSpec) // со спецификацией проще (особенно когда много тестов)
                // Выполняемые действия
                .body(DataHelper.getAuthInfoRestApi())
                .when()
                .post("/api/auth")
                // Проверки
                .then()
                .statusCode(200);

        var testUser = DataHelper.getAuthInfo("vasya");
        String verifyCode = DataHelper.getVerificationCodeFor(testUser);

        String token =
                given()
                        .spec(requestSpec)
                        .body(DataHelper.getVerificationInfoFor(testUser.getLogin(),verifyCode))
                        .when()
                        .post("/api/auth/verification")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response()
                        .path("token");

        Card[] cardsBefore =
                given() // "дано"
                        .spec(requestSpec) // указываем, какую спецификацию используем
                        .header("Authorization", "Bearer "+ token)
                        .when() // "когда"
                        .get("/api/cards") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .as(Card[].class);

        int balanceCard1 = Integer.parseInt(cardsBefore[0].getBalance());
        int balanceCard2 = Integer.parseInt(cardsBefore[1].getBalance());
        int sum = 100;

        given() // "дано"
                .spec(requestSpec) // указываем, какую спецификацию используем
                .header("Authorization", "Bearer "+ token)
                .body(DataHelper.getTransaction("5559 0000 0000 0002", "5559 0000 0000 0001", sum)) // передаём в теле объект, который будет преобразован в JSON
                .when() // "когда"
                .post("/api/transfer") // на какой путь, относительно BaseUri отправляем запрос
                .then() // "тогда ожидаем"
                .statusCode(200); // код 200 OK

        Card[] cardsAfter =
                given() // "дано"
                        .spec(requestSpec) // указываем, какую спецификацию используем
                        .header("Authorization", "Bearer "+ token)
                        .when() // "когда"
                        .get("/api/cards") // на какой путь, относительно BaseUri отправляем запрос
                        .then() // "тогда ожидаем"
                        .statusCode(200) // код 200 OK
                        .extract()
                        .as(Card[].class);

        int actualBalanceCard1 = Integer.parseInt(cardsAfter[0].getBalance());
        int actualBalanceCard2 = Integer.parseInt(cardsAfter[1].getBalance());

        int expectedCard1 = balanceCard1 - sum;
        int expectedCard2 = balanceCard2 + sum;

        Assertions.assertEquals(expectedCard1, actualBalanceCard1);
        Assertions.assertEquals(expectedCard2, actualBalanceCard2);
    }
}
