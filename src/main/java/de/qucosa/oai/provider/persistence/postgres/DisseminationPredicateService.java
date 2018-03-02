package de.qucosa.oai.provider.persistence.postgres;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import de.qucosa.oai.provider.persistence.PersistenceServiceAbstract;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.DisseminationPredicate;

public class DisseminationPredicateService extends PersistenceServiceAbstract implements PersistenceServiceInterface {

    @Override
    public <T> Set<T> findAll() {
        return null;
    }

    @Override
    public <T> Set<T> find(String sqlStmt) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void update(Set<T> sets) {
        StringBuffer sb = new StringBuffer();
        sb.append("insert into dissemination_predicates (id, predicate) values (nextval('oaiprovider'), ?) \r\n");
        sb.append("ON CONFLICT (predicate) DO UPDATE SET \r\n");
        sb.append("predicate = ?; \r\n");
        
        try {
            PreparedStatement pst = connection().prepareStatement(sb.toString());
            connection().setAutoCommit(false);
            
            for (DisseminationPredicate pred : (Set<DisseminationPredicate>) sets) {
                pst.setString(1, pred.getPredicate());
                pst.setString(2, pred.getPredicate());
                pst.addBatch();
            }
            
            pst.executeBatch();
            connection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T findById(Long id) {
        return null;
    }

    @Override
    public <T> T findByValues(Set<T> values) {
        return null;
    }

    @Override
    public void deleteById(Long id) {}

    @Override
    public <T> void deleteByValues(Set<T> values) {}

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findByValue(String column, String value) {
        DisseminationPredicate predicate = new DisseminationPredicate();
        StringBuffer sb = new StringBuffer();
        sb.append("select id, predicate from dissemination_predicates where " + column + " = ?;");
        
        try {
            PreparedStatement pst = connection().prepareStatement(sb.toString());
            connection().setAutoCommit(false);
            pst.setString(1, value);
            ResultSet result = pst.executeQuery();
            
            while(result.next()) {
                predicate.setId(result.getLong("id"));
                predicate.setPredicate(result.getString("predicate"));
            }
            
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return (T) predicate;
    }

    @Override
    public void update(String sql) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(String... value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public <T> T findByValues(String... values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T findByIds(T... values) {
        // TODO Auto-generated method stub
        return null;
    }
}
