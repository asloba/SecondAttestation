package ext;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import ru.inno.api.EmployeeService;
import ru.inno.api.EmployeeServiceImpl;

import java.util.Properties;

import static ext.PropertyProvider.getInstance;

public class EmployeeServiceResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(EmployeeService.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Properties properties = getInstance().getProps();
        String baseUri = properties.getProperty("test.url");
        return new EmployeeServiceImpl(baseUri);
    }
}