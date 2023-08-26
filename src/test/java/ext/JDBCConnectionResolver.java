package ext;

import org.junit.jupiter.api.extension.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConnectionResolver implements ParameterResolver, AfterAllCallback {
    Connection connection;
    public static String KEY = "connection";

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Connection.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {

        connection = (Connection) extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).get(KEY);
        try {
            if (connection != null) return connection;
            Properties properties = PropertyProvider.getInstance().getProps();
            String connectionString = properties.getProperty("connectionString");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");
            connection = DriverManager.getConnection(connectionString, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).put(KEY, connection);
        return connection;
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("disconnecting");
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
