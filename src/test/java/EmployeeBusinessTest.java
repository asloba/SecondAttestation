import com.github.javafaker.Faker;
import ext.CompanyRepositoryJPAResolver;
import ext.EmployeeRepositoryJPAResolver;
import ext.PropertiesResolver;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.inno.api.AuthorizeServiceImpl;
import ru.inno.api.EmployeeService;
import ru.inno.api.EmployeeServiceImpl;
import ru.inno.db.CompanyRepository;
import ru.inno.db.EmployeeRepository;
import ru.inno.model.CompanyEntity;
import ru.inno.model.Employee;
import ru.inno.model.EmployeeEntity;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({PropertiesResolver.class, EmployeeRepositoryJPAResolver.class, CompanyRepositoryJPAResolver.class})
public class EmployeeBusinessTest {

    private static EmployeeService employeeService = new EmployeeServiceImpl();

    private static int companyId;
    private static int employeeId;
    Faker faker = new Faker();
    private String token = new AuthorizeServiceImpl().getToken();


    public EmployeeBusinessTest() throws IOException {
    }


    @BeforeAll
    public static void setUp(CompanyRepository companyRepository) {
        RestAssured.baseURI = "https://x-clients-be.onrender.com/";
        try {
            companyId = companyRepository.create("AL-company", "AL-company");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void tearDown(CompanyRepository companyRepository) {
        companyRepository.deleteById(companyId);
    }

    @AfterEach
    public void cleanData(EmployeeRepository employeeRepository) {
        employeeRepository.deleteById(employeeId);
    }

    @Test
    @DisplayName("Добавление нового сотрудника и проверка записи его в БД")
    public void shouldCreateEmployeeAndCheckSavingToDB(EmployeeRepository repository, CompanyRepository companyRepository) throws SQLException {
        Employee createEmployee = employeeService.createRandomEmployee(companyId);
        employeeId = employeeService.create(createEmployee, token);
        EmployeeEntity employeeInDb = repository.getById(employeeId);
        assertEquals(employeeId, employeeInDb.getId());
    }

    @Test
    @DisplayName("Добавление нового сотрудника и проверка записи его в БД")
    public void shouldGetAllEmployeesByCompanyId(EmployeeService employeeApiService, EmployeeRepository employeeRepository, CompanyEntity company) {
        List<Employee> startList = employeeApiService.getAll(companyId);


    }

}
