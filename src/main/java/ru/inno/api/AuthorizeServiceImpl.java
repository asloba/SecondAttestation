package ru.inno.api;

import io.restassured.filter.log.LogDetail;

import static io.restassured.RestAssured.given;

public class AuthorizeServiceImpl implements AuthorizeService {

    private static final String URI = "https://x-clients-be.onrender.com/auth/login";

    @Override
    public String getToken() {
        String token = given()
                .baseUri(URI)
                .log().ifValidationFails(LogDetail.ALL)
                .contentType("application/json; charset=utf-8")
                .body("{\"username\": \"flora\", \"password\": \"nature-fairy\"}")
                .when()
                .post()
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .extract().path("userToken");
        return token;
    }
}
