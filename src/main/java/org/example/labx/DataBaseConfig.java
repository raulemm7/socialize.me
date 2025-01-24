package org.example.labx;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {
            if (input == null) {
                throw new IOException("Unable to find dbconfig.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static String getDBURL(){
        return properties.getProperty("db.url");
    }

    public static String getDBUser(){
        return properties.getProperty("db.username");
    }

    public static String getDBPassword(){
        return properties.getProperty("db.password");
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url, username, password);
    }
}