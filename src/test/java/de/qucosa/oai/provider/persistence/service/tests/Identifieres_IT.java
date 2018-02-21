package de.qucosa.oai.provider.persistence.service.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.Identifier;
import de.qucosa.oai.provider.persistence.postgres.IndentifierService;

public class Identifieres_IT {
    Connection connection = null;
    
    PersistenceServiceInterface service = new IndentifierService();
    
    @Before
    public void connect() {
        connection = new Connect("postgresql", "oaiprovider").connection();
        service.setConnection(connection);
    }
    
    @Test
    public void findAll_Test() {
        Set<Identifier> identifiers = service.findAll();
        identifiers.size();
    }
    
    @Test
    public void find_Test() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT id, identifier, datestamp, SUBSTRING(identifier, 'qucosa:\\d+$') AS pid \r\n");
        sb.append("FROM identifier WHERE identifier ~ 'qucosa:\\d+$';");
        Set<Identifier> identifiers = service.find(sb.toString());
        identifiers.size();
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
