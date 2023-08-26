import com.github.javafaker.Faker;
import ext.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.inno.api.AuthorizeServiceImpl;
import ru.inno.api.EmployeeService;
import ru.inno.api.EmployeeServiceImpl;
import ru.inno.db.CompanyRepository;
import ru.inno.db.EmployeeRepository;
import ru.inno.db.EmployeeRepositoryJPA;
import ru.inno.model.Employee;
import ru.inno.model.EmployeeEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({PropertiesResolver.class, EmployeeRepositoryJPAResolver.class,
        CompanyRepositoryJPAResolver.class, JDBCConnectionResolver.class, EmployeeServiceResolver.class})
public class EmployeeBusinessTest {

    Properties properties = PropertyProvider.getInstance().getProps();
    private EmployeeService employeeService = new EmployeeServiceImpl(properties.getProperty("test.url"));
    private static int companyId;
    private static int employeeId;
    static Faker faker = new Faker();
    private String token = new AuthorizeServiceImpl().getToken();

//    public EmployeeBusinessTest() throws IOException {
//    }

    @BeforeAll
    public static void setUp(CompanyRepository companyRepository) {
        RestAssured.baseURI = "https://x-clients-be.onrender.com/";

        companyId = companyRepository.create("AL-" + faker.company().name(), "AL-" + faker.twinPeaks().location());
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
    public void shouldCreateEmployeeAndCheckSavingToDB(EmployeeRepository repository) {
        Employee createEmployee = employeeService.getRandomEmployee(companyId);
        employeeId = employeeService.create(createEmployee, token);
        EmployeeEntity employeeInDb = repository.getById(employeeId);
        assertEquals(employeeId, employeeInDb.getId());
    }

    //Баг с получением полей url и email по апи
    @Test
    @DisplayName("Получение списка сотрудников по Id компании")
    public void shouldGetAllEmployeesByCompanyId(EmployeeService employeeService) {
        //Запрашиваем изначальный список employee и убеждаемся, что он пуст
        List<Employee> startList = employeeService.getAll(companyId);
        assertEquals(0, startList.size());
        //Создаём employee с конкретным companyId
        Employee employee = employeeService.getRandomEmployee(companyId);
        employeeId = employeeService.create(employee, token);
        employee.setId(employeeId);
        //Запрашиваем повторно список employee и убеждаемся, что в нём появился созданный employee
        List<Employee> endList = employeeService.getAll(employee.getCompanyId());
        assertEquals(1, endList.size());
        assertEquals(endList.get(0), employee);
    }

    @Test
    @DisplayName("Обновление информации о сотруднике")
    public void shouldUpdateEmployee(EmployeeService employeeService, EmployeeRepository employeeRepository) {
        //Генерируем тестовые данные
        Employee employeeAPI = employeeService.getRandomEmployee(companyId);
        Employee employeeUpdated = employeeService.getRandomEmployee(companyId);
        //Создаём сотрудника
        employeeId = employeeService.create(employeeAPI, token);
        //Присваиваем employee c обновлёнными данными id созданного ранее сотрудника
        employeeUpdated.setId(employeeId);
        //Обновляем employee
        Employee updatedEmployeeAPI = employeeService.update(employeeUpdated, token);
        EmployeeEntity employeeFromDB = employeeRepository.getById(updatedEmployeeAPI.getId());
        assert(employeeFromDB.isEqualEmployeeModel(updatedEmployeeAPI));
    }

    @Test
    @DisplayName("Добавление нового сотрудника с несуществующим companyId")
    public void shouldCreateEmployeeOfNonExistentCompany(EmployeeService employeeService, EmployeeRepository employeeRepository) {
        //Генерируем несуществующий companyId
        companyId = faker.number().randomDigitNotZero();
        //Запрашиваем размер списка employee до попытки создания employee с несуществующим companyId
        int startListSize = employeeRepository.getAll().size();
        //Генерируем тестовые данные для employee и проверяем, что при попытке создания через апи появляется ошибка
        Employee createEmployee = employeeService.getRandomEmployee(companyId);
        assertThrows(AssertionError.class, () -> employeeService.create(createEmployee, token));
        //Перезапрашиваем размер списка employee
        int endListSize = employeeRepository.getAll().size();
        //Проверяем, что размеры первого и второго списков равны
        assertEquals(startListSize, endListSize);
    }

    @Test
    @DisplayName("Получение списка сотрудников несуществующей компании")
    public void shouldGetEmployeesOfNonExistentCompany(EmployeeService employeeService) {
        //Генерируем несуществующий companyId
        companyId = faker.number().randomDigitNotZero();
        //Запрашиваем изначальный список employee
        List<Employee> startList = employeeService.getAll(companyId);
        //Генерируем тестовые данные для employee и проверяем, что при попытке создания через апи появляется ошибка
        Employee employee = employeeService.getRandomEmployee(companyId);
        assertThrows(AssertionError.class, () -> employeeService.create(employee, token));
        //Запрашиваем повторно список employee и убеждаемся, что размеры двух списков одинаковые
        List<Employee> endList = employeeService.getAll(employee.getCompanyId());
        assertEquals(startList.size(), endList.size());
    }

    @Test
    @DisplayName("Обновление информации о сотруднике по некорректному employeeId")
    public void shouldUpdateEmployeeByNonExistentId(EmployeeService employeeService, EmployeeRepository employeeRepository) {
        //Генерируем тестовые данные
        Employee employeeAPI = employeeService.getRandomEmployee(companyId);
        Employee employeeUpdated = employeeService.getRandomEmployee(companyId);
        //Создаём сотрудника
        employeeId = employeeService.create(employeeAPI, token);
        //Присваиваем employee c обновлёнными данными id созданного ранее сотрудника
        int fakeEmployeeId = faker.number().randomDigitNotZero();
        //Устанавливаем некорректный id
        employeeUpdated.setId(fakeEmployeeId);
        //Обновляем employee
//        assertThrows(AssertionError.class, () -> employeeService.create(employeeUpdated, token));
        Employee updatedEmployeeAPI = employeeService.update(employeeUpdated, token);

        EmployeeEntity employeeFromDB = employeeRepository.getById(fakeEmployeeId);
//        assert(employeeFromDB.isEqualEmployeeModel(updatedEmployeeAPI));
    }
}