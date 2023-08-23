package ext;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitInfo;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Properties;

public class ContextManager {
    public static Object getEntityManager(ExtensionContext extensionContext) {
        Object em = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).get("em");
        if (em == null) {
            Properties props = PropertyProvider.getInstance().getProps();
            PersistenceUnitInfo persistenceUnitInfo = new TestPersistenceUnitInfo(props);
            HibernatePersistenceProvider hibernatePersistenceProvider = new HibernatePersistenceProvider();
            EntityManagerFactory factory =
                    hibernatePersistenceProvider
                            .createContainerEntityManagerFactory(persistenceUnitInfo, props);
            em = factory.createEntityManager();
            extensionContext.getStore(ExtensionContext.Namespace.GLOBAL).put("em", em);
        }
        return em;
    }
}
