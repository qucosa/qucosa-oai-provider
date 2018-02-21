package de.qucosa.oai.provider.persistence.service.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.Format;
import de.qucosa.oai.provider.persistence.postgres.FormatService;

public class Format_IT {
    Connection connection = null;
    
    PersistenceServiceInterface service = new FormatService();
    
    @Before
    public void connect() {
        connection = new Connect("postgresql", "oaiprovider").connection();
        service.setConnection(connection);
    }
    
    @Test
    public void findAll_Test() {
        Set<Format> formats = service.findAll();
        formats.size();
    }
    
    @After
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
