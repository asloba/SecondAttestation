package ext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitInfo;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import ru.inno.db.EmployeeRepository;
import ru.inno.db.EmployeeRepositoryJPA;

import java.util.Properties;

public class EmployeeRepositoryJPAResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(EmployeeRepository.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Properties props = PropertyProvider.getInstance().getProps();

        Object em = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).get("em");
        if (em == null) {
            PersistenceUnitInfo persistenceUnitInfo = new TestPersistenceUnitInfo(props);
            HibernatePersistenceProvider hibernatePersistenceProvider = new HibernatePersistenceProvider();
            EntityManagerFactory factory = hibernatePersistenceProvider.createContainerEntityManagerFactory(persistenceUnitInfo, props);
            em = factory.createEntityManager();
            extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).put("em", em);
        }
        return new EmployeeRepositoryJPA((EntityManager) em);
    }
}