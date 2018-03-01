package de.qucosa.oai.provider.persistence.postgres;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import de.qucosa.oai.provider.persistence.PersistenceServiceAbstract;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.Identifier;

public class IndentifierService extends PersistenceServiceAbstract implements PersistenceServiceInterface {
    @SuppressWarnings("unchecked")
    @Override
    public Set<Identifier> findAll() {
        Set<Identifier> identifiers = new HashSet<>();
        ResultSet result = null;

        try {
            String sql = "SELECT * FROM identifier;";
            Statement stmt = connection().createStatement();
            result = stmt.executeQuery(sql);

            while (result.next()) {
                Identifier identifier = new Identifier();
                identifier.setId(result.getLong("id"));
                identifier.setDatestamp(result.getTimestamp("datestamp"));
                identifier.setIdentifier(result.getString("identifier"));
                identifiers.add(identifier);
            }

            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return identifiers;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> void update(Set<T> sets) {
        Set<Identifier> identifiers = (Set<Identifier>) sets;
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO identifier (id, identifier, datestamp) \r\n");
        sb.append("VALUES (nextval('oaiprovider'), ?, ?) \r\n");
        sb.append("ON CONFLICT (identifier) \r\n");
        sb.append("DO UPDATE SET identifier = ? \r\n");
        
        try {
            PreparedStatement pst = connection().prepareStatement(sb.toString());
            connection().setAutoCommit(false);
            
            for (Identifier identifier : identifiers) {
                pst.setString(1, identifier.getIdentifier());
                pst.setTimestamp(2, identifier.getDatestamp());
                pst.setString(3, identifier.getIdentifier());
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
    public void deleteById(Long id) {
        
    }

    @Override
    public <T> void deleteByValues(Set<T> values) {
        
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Set<T> find(String sqlStmt) {
        Set<Identifier> identifiers = new HashSet<>();
        ResultSet result = null;

        try {
            Statement stmt = connection().createStatement();
            result = stmt.executeQuery(sqlStmt);

            while (result.next()) {
                Identifier identifier = new Identifier();
                identifier.setId(result.getLong("id"));
                identifier.setDatestamp(result.getTimestamp("datestamp"));
                identifier.setIdentifier(result.getString("identifier"));
                identifier.setPid(result.getString("pid"));
                identifiers.add(identifier);
            }

            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return (Set<T>) identifiers;
    }

    @Override
    public <T> T findByValue(String column, String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void update(String sql) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(String... value) {
        // TODO Auto-generated method stub
        
    }
}
