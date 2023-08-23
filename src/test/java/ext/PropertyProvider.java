package ext;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertyProvider {
    private static PropertyProvider providerInstance;
    private Properties properties;

    private PropertyProvider() {
    }

    public static PropertyProvider getInstance() {
        if (providerInstance == null) {
            providerInstance = new PropertyProvider();
            providerInstance.loadProperties();
        }
        return providerInstance;
    }

    public Properties getProps() {
        return properties;
    }

    private void loadProperties() {
        properties = new Properties();
        try {
            String env = System.getProperty("env", "dbTest");
            properties.load(new FileReader("src/main/resources/" + env + ".properties"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}