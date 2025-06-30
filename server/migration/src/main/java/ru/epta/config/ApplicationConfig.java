package ru.epta.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationConfig {

    private Datasource datasource;

    @Getter
    @Setter
    public static class Datasource {
        private String url;
        private String username;
        private String password;
    }
}
