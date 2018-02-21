package de.qucosa.oai.provider.persistence;

import java.sql.Connection;
import java.util.Set;

public interface PersistenceServiceInterface {
    public void setConnection(Connection connection);
    
    public <T> Set<T> findAll();
    
    public <T> Set<T> find(String sqlStmt);
    
    public <T> void update(Set<T> sets);
    
    public <T> T findById(Long id);
    
    public <T> T findByValues(Set<T> values);
    
    public void deleteById(Long id);
    
    public <T> void deleteByValues(Set<T> values);
}
