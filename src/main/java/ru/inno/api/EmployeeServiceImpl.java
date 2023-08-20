package ru.inno.api;

import com.github.javafaker.Faker;
import io.restassured.common.mapper.TypeRef;
import ru.inno.model.Employee;

import java.util.List;

import static io.restassured.RestAssured.given;

public class EmployeeServiceImpl implements EmployeeService {
    private static final String PATH = "/employee";
    private final static String prefix = "AL-";
    Faker faker = new Faker();
    private String uri;

    @Override
    public void setURI(String uri) {
        this.uri = uri;
    }

    @Override
    public Employee createRandomEmployee(int companyId) {
        String firstName = prefix + faker.name().firstName();
        String lastName = faker.name().lastName();
        String middleName = prefix + faker.funnyName();
        String email = faker.internet().emailAddress();
        String url = faker.internet().url();
        String phone = faker.phoneNumber().phoneNumber();
        return new Employee(firstName, lastName, middleName, companyId, email, url, phone, true);
    }

    @Override
    public List<Employee> getAll(int companyId) {
        return given()
                .baseUri(uri + PATH)
                .header("accept", "application/json")
                .when()
                .param("company", companyId)
                .get()
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().body().as(new TypeRef<List<Employee>>() {
                });
    }

    @Override
    public Employee getById(int id) {
        return given()
                .baseUri(uri + PATH + id)
                .header("accept", "application/json")
                .when()
                .get()
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract().body().as(Employee.class);
    }

    @Override
    public int create(Employee employee, String token) {
        return given()
                .baseUri(uri + PATH)
                .header("accept", "application/json")
                .header("x-client-token", token)
                .body(employee)
                .when()
                .post()
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .contentType("application/json; charset=utf-8")
                .extract().path("id");

    }

    @Override
    public int update(Employee employee, String token) {
        return 0;
    }
}
