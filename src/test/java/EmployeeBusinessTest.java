import com.github.javafaker.Faker;
import ext.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.inno.api.AuthorizeServiceImpl;
import ru.inno.api.EmployeeService;
import ru.inno.api.EmployeeServiceImpl;
import ru.inno.db.CompanyRepository;
import ru.inno.db.EmployeeRepository;
import ru.inno.model.Employee;
import ru.inno.model.EmployeeEntity;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({PropertiesResolver.class, EmployeeRepositoryJPAResolver.class,
        CompanyRepositoryJPAResolver.class, JDBCConnectionResolver.class, EmployeeServiceResolver.class})
public class EmployeeBusinessTest {

    static Faker faker = new Faker();
    private static int companyId;
    private static int employeeId;
    Properties properties = PropertyProvider.getInstance().getProps();
    private EmployeeService employeeService = new EmployeeServiceImpl(properties.getProperty("test.url"));
    private String token = new AuthorizeServiceImpl().getToken();

    @BeforeAll
    public static void setUp(CompanyRepository companyRepository) {
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
    public void shouldUpdateEmployee(EmployeeService employeeService, EmployeeRepository employeeRepository) throws InterruptedException {
        //Генерируем тестовые данные
        Employee employeeData = employeeService.getRandomEmployee(companyId);
        Employee employeeUpdatedData = employeeService.getRandomEmployee(companyId);
        //Создаём сотрудника
        employeeId = employeeService.create(employeeData, token);
        Employee employeeCreated = employeeService.getById(employeeId);
        //Присваиваем созданному employee новое имя, фамилию и телефон
        employeeCreated.setFirstName(employeeUpdatedData.getFirstName());
        employeeCreated.setLastName(employeeUpdatedData.getLastName());
        employeeCreated.setPhone(employeeUpdatedData.getPhone());
        //Обновляем employee
        Employee updatedEmployeeAPI = employeeService.update(employeeCreated, token);
//        Employee updatedEmp = employeeService.getById(updatedEmployeeAPI.getId());
        System.out.println(updatedEmployeeAPI);
        Thread.sleep(500);
        //Запрашиваем информацию о employee из бд
        EmployeeEntity employeeFromDB = employeeRepository.getById(updatedEmployeeAPI.getId());
        Thread.sleep(500);
        //Сравниваем, что обновленная информация о employee соответствует информации о нём из бд
        assertEquals(updatedEmployeeAPI.getFirstName(), employeeFromDB.getFirstName());
        assertEquals(updatedEmployeeAPI.getLastName(), employeeFromDB.getLastName());
        assertEquals(updatedEmployeeAPI.getPhone(), employeeFromDB.getPhone());
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
        employeeService.update(employeeUpdated, token);
        //Пытаемся получить employee по новому id и проверяем, что employee с таким id не существует
        EmployeeEntity employeeFromDB = employeeRepository.getById(fakeEmployeeId);
        assertNull(employeeFromDB);
    }
}