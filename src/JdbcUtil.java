import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JdbcUtil {
    private static String driver = null;
    private static String url = null;
    private static String name = null;
    private static String password = null;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/jdbc.properties"));

            driver = properties.getProperty("driverClass");
            url = properties.getProperty("url");
            name = properties.getProperty("name");
            password = properties.getProperty("password");

            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, name, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static <T> void closeConnection(T... t) {
        for (T tmp : t) {
            if (tmp instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) tmp).close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
