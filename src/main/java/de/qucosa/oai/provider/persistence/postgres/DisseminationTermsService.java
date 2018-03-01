package de.qucosa.oai.provider.persistence.postgres;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import de.qucosa.oai.provider.persistence.PersistenceServiceAbstract;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;

public class DisseminationTermsService extends PersistenceServiceAbstract implements PersistenceServiceInterface {

    @Override
    public <T> Set<T> findAll() {
        return null;
    }

    @Override
    public <T> Set<T> find(String sqlStmt) {
        return null;
    }

    @Override
    public <T> void update(Set<T> sets) {
        
    }
    
    @Override
    public void update(String sql) {}

    @Override
    public <T> T findById(Long id) {
        return null;
    }

    @Override
    public <T> T findByValue(String column, String value) {
        return null;
    }

    @Override
    public <T> T findByValues(Set<T> values) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public <T> void deleteByValues(Set<T> values) {
        
    }

    @Override
    public void update(String... value) {
        String sql = "select generate_dissemination_terms('" + value[1] + "', '" + value[0] + "', '" + value[2] + "');";
        try {
            Statement stmt = connection().createStatement();
            connection().setAutoCommit(false);
            stmt.execute(sql);
            connection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
}
