package ru.epta;

import java.io.InputStream;
import java.util.Collections;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.jdbc.DriverDataSource;
import org.yaml.snakeyaml.Yaml;
import ru.epta.config.ApplicationConfig;
import ru.epta.config.ApplicationConfig.Datasource;

public class MigrationApp {

    public static void main(String[] args) {

        Yaml yaml = new Yaml();
        InputStream inputStream = MigrationApp.class
            .getClassLoader()
            .getResourceAsStream("application.yml");

        ApplicationConfig config = yaml.loadAs(inputStream, ApplicationConfig.class);

        Datasource datasource = config.getDatasource();

        Flyway flyway = Flyway.configure()
            .dataSource(datasource.getUrl(), datasource.getUsername(), datasource.getPassword())
            .locations("classpath:migration")
            .load();

        flyway.migrate();
    }
}