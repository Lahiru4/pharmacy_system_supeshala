package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {
    private final static String URL="jdbc:mysql://localhost:3306/pharmacy_system_supeshala";
    private final static Properties props=new Properties();
    private static DbConnection dbConnection;
    private Connection connection;
    static {
        props.setProperty("user","root");
        props.setProperty("password","1234");
    }

    public DbConnection() throws SQLException {
        connection= DriverManager.getConnection(URL,props);
    }

    public static DbConnection getInstance() throws SQLException {
        if (dbConnection==null) {
            return dbConnection=new DbConnection();
        }else {
            return dbConnection;
        }
    }

    public  Connection getConnection() {
        return connection;
    }
}
