package db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (in == null) {
                throw new IllegalStateException("Arquivo database.properties não encontrado.");
            }
            PROPS.load(in);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        String url = valor("BIBLIOTECH_DB_URL", "db.url");
        String user = valor("BIBLIOTECH_DB_USER", "db.user");
        String password = valor("BIBLIOTECH_DB_PASSWORD", "db.password");
        return DriverManager.getConnection(url, user, password);
    }

    private static String valor(String env, String prop) {
        String ambiente = System.getenv(env);
        if (ambiente != null && !ambiente.isBlank()) return ambiente;
        return PROPS.getProperty(prop, "").trim();
    }
}
