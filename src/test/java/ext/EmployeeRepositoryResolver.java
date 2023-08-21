package ext;

import org.junit.jupiter.api.extension.*;
import ru.inno.db.EmployeeRepository;
import ru.inno.db.EmployeeRepositoryJPA;

import java.sql.Connection;
import java.sql.DriverManager;

public class EmployeeRepositoryResolver implements ParameterResolver, BeforeAllCallback, AfterAllCallback {
    private Connection connection = null;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(EmployeeRepository.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return new EmployeeRepositoryJPA(connection);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("disconnecting");
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("connecting");
        //Данные для подключения удалены в целях безопасности
        String connectionString = "";
        String user = "";
        String pass = "";
        connection = DriverManager.getConnection(connectionString, user, pass);
    }

}
