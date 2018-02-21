package de.qucosa.oai.provider.persistence.connections.tests;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import de.qucosa.oai.provider.persistence.Connect;

public class ConnectionTests_IT {
    @Test
    public void postgresConnect_Test() throws SQLException {
        Connection connection = new Connect("postgresql", "oaiprovider").connection();
        connection.close();
    }
}
