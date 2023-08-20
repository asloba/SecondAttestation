package ru.inno.api;

import io.restassured.http.ContentType;
import ru.inno.model.UserForAuthInfo;

import java.io.IOException;

import static io.restassured.RestAssured.given;

public class AuthorizeServiceImpl implements AuthorizeService {

    private static final String PATH = "auth/login";

    @Override
    public String getToken() throws IOException {
        String token = given()
                .basePath(PATH)
                .contentType(ContentType.JSON)
                .body(new UserForAuthInfo("flora", "nature-fairy"))
                .post()
                .then()
                .statusCode(201)
                .extract().path("userToken");
        return token;
    }
//    public static final MediaType APPLICATION_JSON = MediaType.parse("application/json; charset=UTF-8");
//    private static final String PATH = "auth/login";
//    private final String BASE_PATH;
//    private final OkHttpClient client;
//    private final ObjectMapper mapper;
//
//    public AuthorizeServiceImpl(String url, OkHttpClient client) {
//

//        this.BASE_PATH = url;
//        this.client = client;
//        this.mapper = new ObjectMapper();
//    }
//
//    @Override
//    public UserInfo auth(String username, String password) throws IOException {
//        String body = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
//        RequestBody jsonBody = RequestBody.create(body, APPLICATION_JSON);
//        Request request = new Request.Builder().post(jsonBody).url(BASE_PATH + PATH).build();
//        Response response = this.client.newCall(request).execute();
//        return mapper.readValue(response.body().string(), UserInfo.class);
//    }
}
