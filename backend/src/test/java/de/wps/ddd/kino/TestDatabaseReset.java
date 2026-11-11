package de.wps.ddd.kino;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class TestDatabaseReset {

    private static final String TEST_DATABASE = "kinosoft_test";
    private static final String[] SCHEMAS = {
            "filmauswahl",
            "kartenverkauf",
            "einlasskontrolle",
            "nutzerverwaltung"
    };

    private TestDatabaseReset() {
    }

    public static void main(String[] args) throws SQLException {
        var testUrl = System.getenv().getOrDefault(
                "SPRING_TEST_DATASOURCE_URL",
                "jdbc:postgresql://localhost:5432/" + TEST_DATABASE);
        var username = System.getenv().getOrDefault("SPRING_TEST_DATASOURCE_USERNAME", "postgres");
        var password = System.getenv().getOrDefault("SPRING_TEST_DATASOURCE_PASSWORD", "kinosoft-dev");
        var adminUrl = testUrl.substring(0, testUrl.lastIndexOf('/') + 1) + "postgres";

        try (var connection = DriverManager.getConnection(adminUrl, username, password);
             var statement = connection.createStatement()) {
            statement.execute("""
                    SELECT pg_terminate_backend(pid)
                    FROM pg_stat_activity
                    WHERE datname = 'kinosoft_test' AND pid <> pg_backend_pid()
                    """);
            statement.executeUpdate("DROP DATABASE IF EXISTS " + TEST_DATABASE);
            statement.executeUpdate("CREATE DATABASE " + TEST_DATABASE);
        }

        try (var connection = DriverManager.getConnection(testUrl, username, password);
             var statement = connection.createStatement()) {
            for (var schema : SCHEMAS) {
                statement.executeUpdate("CREATE SCHEMA " + schema);
            }
        }
    }
}
