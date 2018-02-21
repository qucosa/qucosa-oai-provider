package de.qucosa.oai.provider.persistence.service.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.postgres.SetService;

public class Sets_IT {
    Connection connection = null;
    
    PersistenceServiceInterface service = new SetService();
    
    @Before
    public void connect() {
        connection = new Connect("postgresql", "oaiprovider").connection();
        service.setConnection(connection);
    }
    
    @Test
    public void findAll_Test() {
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = service.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(sets);
            out.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        byte[] byteSets = baos.toByteArray();
        
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
