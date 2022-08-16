package ru.netology.domain.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import static io.restassured.RestAssured.given;

public class RestAPIHelper {

    final private RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public void openLoginPage(User user) {
        given()
                .spec(requestSpec)
                .body(getAuthInfo(user))
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);
    }

    public String verificationCodePage(User user, String verifyCode) {
        String token =
                given()
                        .spec(requestSpec)
                        .body(getVerificationInfo(user, verifyCode))
                        .when()
                        .post("/api/auth/verification")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response()
                        .path("token");
        return token;
    }

    public Card[] checkBalance(String token) {
        Card[] cardsBalance =
                given()
                        .spec(requestSpec)
                        .header("Authorization", "Bearer " + token)
                        .when()
                        .get("/api/cards")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Card[].class);
        return cardsBalance;
    }

    public void makeTransaction(String token, String cardFrom, String cardTo, int sum) {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .body(getTransaction(cardFrom, cardTo, sum))
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(200);
    }

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo(User authInfo) {
        return new AuthInfo(authInfo.getLogin(), authInfo.getPassword());
    }

    @Value
    public static class VerificationInfo {
        private String login;
        private String code;
    }

    public static VerificationInfo getVerificationInfo(User authInfo, String verifyCode) {
        return new VerificationInfo(authInfo.getLogin(), verifyCode);
    }

    @Value
    public static class Transaction {
        private String from;
        private String to;
        private int amount;
    }

    public static Transaction getTransaction(String from, String to, int amount) {
        return new Transaction(from, to, amount);
    }
}
