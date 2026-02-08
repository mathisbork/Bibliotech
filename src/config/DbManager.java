import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DbManager {
    private static DbManager instance;
    private final Properties p = new Properties();
    private String url;
    private String username;
    private String password;
    private Connection connection;

    private DbManager() {
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("ressources/db.properties");
            p.load(input);
            url = p.getProperty("db.url");
            username = p.getProperty("db.user");
            password = p.getProperty("db.password");
            connection = DriverManager.getConnection(url, username, password);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }

    public static DbManager getInstance() {
        if (instance == null) {
            instance = new DbManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
