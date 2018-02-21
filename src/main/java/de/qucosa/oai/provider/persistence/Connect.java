package de.qucosa.oai.provider.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connect {
    private Logger logger = LoggerFactory.getLogger(Connect.class);

    private Connection connection = null;
    
    private String dbName = null;
    
    private String user = null;
    
    private String passwd = null;
    
    private String host = "localhost";
    
    private String port = "5432";
    
    public Connect(String dbType, String dbName) {
        this.dbName = dbName;
        execute(dbType);
    }
    
    public Connect(String dbType, String dbName, String user, String passwd) {
        this.dbName = dbName;
        this.user = user;
        this.passwd = passwd;
        execute(dbType);
    }
    
    public Connect(String dbType, String host, String dbName, String user, String passwd) {
        this.host = host;
        this.dbName = dbName;
        this.user = user;
        this.passwd = passwd;
        
        execute(dbType);
    }
    
    public Connect(String dbType, String host, String port, String dbName, String user, String passwd) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.passwd = passwd;
        
        execute(dbType);
    }
    
    public Connection connection() {
        return connection;
    }
    
    private void execute(String dbType) {
        try {
            Method method = getClass().getDeclaredMethod(dbType);
            method.invoke(this);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unused")
    private void postgresql() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        
        if (user != null && passwd != null) {
            connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + dbName, user, passwd);
        } else {
            connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + dbName, "postgres", "");
        }
        
        if (connection == null) {
            logger.error("Connat connect to the postgres database.");
            throw new SQLException();
        }
    }
}
