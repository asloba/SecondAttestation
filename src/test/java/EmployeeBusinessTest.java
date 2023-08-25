import com.github.javafaker.Faker;
import ext.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterResolver;
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

import static java.util.function.Predicate.isEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({PropertiesResolver.class, EmployeeRepositoryJPAResolver.class,
        CompanyRepositoryJPAResolver.class})
public class EmployeeBusinessTest {

    private static EmployeeService employeeService = new EmployeeServiceImpl();
    private static int companyId;
    private static int employeeId;
    static Faker faker = new Faker();
    private String token = new AuthorizeServiceImpl().getToken();

    public EmployeeBusinessTest() throws IOException {
    }

    @BeforeAll
    public static void setUp(CompanyRepository companyRepository) {
        RestAssured.baseURI = "https://x-clients-be.onrender.com/";
        try {
            companyId = companyRepository.create("AL-" + faker.company().name()  , "AL-" + faker.twinPeaks().location());
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
    @DisplayName("Получение списка сотрудников по Id компании")
    public void shouldGetAllEmployeesByCompanyId(EmployeeService employeeService, EmployeeRepository repository) {
        List<Employee> startList = employeeService.getAll(companyId);
        assertEquals(0, startList.size());
        Employee employee = employeeService.createRandomEmployee(companyId);
        employeeId = employee.getId();
        List<Employee> endList = employeeService.getAll(companyId);
        assertEquals(1, endList.size());
        assertEquals(endList.get(0), employee);
    }
    @Test
    @DisplayName("Получение созданного сотрудника по id")
    public void shouldGetCreatedEmployeeById(EmployeeService employeeService, EmployeeRepository employeeRepository) {
        Employee employeeAPI = employeeService.createRandomEmployee(companyId);
        EmployeeEntity employeeFromDB = employeeRepository.getById(employeeAPI.getId());
//        assertThat(employeeAPI, isEqual(employeeFromDB));
    }


}