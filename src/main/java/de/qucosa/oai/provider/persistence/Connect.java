/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qucosa.oai.provider.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    private Logger logger = LoggerFactory.getLogger(Connect.class);

    private Connection connection = null;
    
    private String dbName;
    
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

        switch (dbType) {
            case "postgresql":
                this.postgresql();
                break;
        }
    }
    
    private void postgresql() {
        try {
            Class.forName("org.postgresql.Driver");

            if (user != null && passwd != null) {
                connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + dbName, user, passwd);
            } else {
                connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + dbName, "postgres", "");
            }
        } catch (ClassNotFoundException e) {
            logger.error("Cannot find psotgresql driver class.", e);
            throw new RuntimeException();
        } catch (SQLException e) {
            logger.error("Cannot connect to postgres (" + dbName + ") database.", e);
            throw new RuntimeException();
        }
    }
}
