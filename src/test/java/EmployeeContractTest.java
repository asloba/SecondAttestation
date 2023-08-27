import com.github.javafaker.Faker;
import ext.*;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.inno.api.AuthorizeServiceImpl;
import ru.inno.api.EmployeeService;
import ru.inno.db.CompanyRepository;
import ru.inno.db.EmployeeRepository;
import ru.inno.model.Employee;

import java.util.List;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({PropertiesResolver.class, EmployeeRepositoryJPAResolver.class,
        CompanyRepositoryJPAResolver.class, JDBCConnectionResolver.class, EmployeeServiceResolver.class})
public class EmployeeContractTest {

    private static final String PATH = "/employee";
    static Properties properties = PropertyProvider.getInstance().getProps();
    private static final String BASE_URI = properties.getProperty("test.url");
    static Employee employee = new Employee();
    static Faker faker = new Faker();
    private static int companyId;
    private static int employeeId;
    private String token = new AuthorizeServiceImpl().getToken();

    @BeforeAll
    public static void setUp(CompanyRepository companyRepository, EmployeeService employeeService) {
        companyId = companyRepository.create("AL-" + faker.company().name(), "AL-" + faker.twinPeaks().location());
        employee = employeeService.getRandomEmployee(companyId);
    }

    @AfterAll
    public static void tearDown(CompanyRepository companyRepository, EmployeeRepository employeeRepository) {
        employeeRepository.deleteById(employeeId);
        companyRepository.deleteById(companyId);

    }

    @Test
    @DisplayName("Проверка получения кода ответа 201 на запрос создания сотрудника авторизованным пользователем")
    public void shouldReceive201AfterCreation() {
        employeeId = given()
                .baseUri(BASE_URI + PATH)
                .log().ifValidationFails()
                .header("accept", "application/json")
                .contentType("application/json; charset=utf-8")
                .header("x-client-token", token)
                .body(employee)
                .when()
                .post()
                .then()
                .log().ifValidationFails()
                .statusCode(201)
                .extract().path("id");
    }

    @Test
    @DisplayName("Проверка получения кода ответа 200 на запрос получения всех сотрудников по id компании")
    public void shouldReceive200AfterGetAll(EmployeeService employeeService) {
        employeeId = employeeService.create(employee, token);
        given()
                .baseUri(BASE_URI + PATH)
                .header("accept", "application/json")
                .param("company", companyId)
                .when()
                .get()
                .then()
                .log().ifValidationFails()
                .statusCode(200);
    }

    @Test
    @DisplayName("Проверка получения верного количества элеметров в ответе на запрос сотрудников компании")
    public void shouldReceiveOneElementWhileGetAll(EmployeeService employeeService) {
        employeeId = employeeService.create(employee, token);
        List<Employee> employeeList = given()
                .baseUri(BASE_URI + PATH)
                .header("accept", "application/json")
                .param("company", companyId)
                .when()
                .get()
                .then()
                .log().ifValidationFails()
                .extract().body().as(new TypeRef<List<Employee>>() {
                });
        assertEquals(1, employeeList.size());
    }

    @Test
    @DisplayName("Проверка получения кода ответа 200 на запрос получения сотрудника по id")
    public void shouldReceive200AfterGetEmployee(EmployeeService employeeService) {
        employeeId = employeeService.create(employee, token);
        given()
                .baseUri(BASE_URI + PATH + "/" + employeeId)
                .header("accept", "application/json")
                .when()
                .get()
                .then()
                .log().ifValidationFails()
                .statusCode(200);
    }

    @Test
    @DisplayName("Проверка получения ответа в формате JSON на запрос получения сотрудника по id")
    public void shouldReceiveJson(EmployeeService employeeService) {
        employeeId = employeeService.create(employee, token);
        given()
                .baseUri(BASE_URI + PATH + "/" + employeeId)
                .header("accept", "application/json")
                .when()
                .get()
                .then()
                .log().ifValidationFails()
                .contentType("application/json");
    }

    @Test
    @DisplayName("Проверка получения кода ответа 401 на запрос создания сотрудника неавторизованным пользователем")
    public void shouldReceive401AfterCreationUnauthorized() {
        given()
                .baseUri(BASE_URI + PATH)
                .log().ifValidationFails()
                .header("accept", "application/json")
                .contentType("application/json; charset=utf-8")
                .body(employee)
                .when()
                .post()
                .then()
                .log().ifValidationFails()
                .statusCode(401);
    }

    @Test
    @DisplayName("Проверка получения кода ответа 500 на запрос создания сотрудника без обязательного поля")
    public void shouldReceive500WithoutNecessaryField() {
        employee.setFirstName(null);
        given()
                .baseUri(BASE_URI + PATH)
                .log().ifValidationFails()
                .header("accept", "application/json")
                .contentType("application/json; charset=utf-8")
                .header("x-client-token", token)
                .body(employee)
                .when()
                .post()
                .then()
                .log().ifValidationFails()
                .statusCode(500);
    }

    //Баг: на запрос сотрудника по несуществующему id приходит ответ без тела со статус-кодом 200 (противоречит документации в Swagger)
    @Test
    @DisplayName("Проверка получения кода ответа 404 на запрос получения несуществуюего сотрудника")
    public void shouldReceive404GettingNonExistentEmployee(EmployeeService employeeService) {
        employeeId = employeeService.create(employee, token);
        long nonExistentId = faker.number().randomNumber(5, true);
        given()
                .baseUri(BASE_URI + PATH + "/" + nonExistentId)
                .header("accept", "application/json")
                .when()
                .get()
                .then()
                .log().ifValidationFails()
                .statusCode(404);
    }
}