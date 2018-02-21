package de.qucosa.oai.provider.persistence;

import java.sql.Connection;

import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;

public abstract class PersistenceServiceAbstract implements PersistenceServiceInterface {
    private Connection connection;
    
    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    protected Connection connection() {
        return connection;
    }
}
