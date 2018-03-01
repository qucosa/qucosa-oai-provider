package de.qucosa.oai.provider.persistence.postgres;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.xml.sax.SAXException;

import de.qucosa.oai.provider.persistence.PersistenceServiceAbstract;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;

public class SetService extends PersistenceServiceAbstract implements PersistenceServiceInterface {
    
    @SuppressWarnings("unchecked")
    public Set<de.qucosa.oai.provider.persistence.pojos.Set> findAll() {
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = new HashSet<>();
        ResultSet result = null;
        String sql = "SELECT id, setspec, predicate, doc, XPATH('//setSpec', doc) AS setspecnode FROM sets;";

        try {
            Statement stmt = connection().createStatement();
            result = stmt.executeQuery(sql);
            
            while(result.next()) {
                de.qucosa.oai.provider.persistence.pojos.Set set = new de.qucosa.oai.provider.persistence.pojos.Set();
                set.setId(result.getLong("id"));
                set.setSetSpec(result.getString("setspec"));
                set.setPredicate(result.getString("predicate"));
                set.setDoc(result.getString("doc"));
                sets.add(set);
            }
            
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return sets;
    }
    
    @SuppressWarnings("unchecked")
    public <T> void update(Set<T> sets) {
        Set<de.qucosa.oai.provider.persistence.pojos.Set> data = (Set<de.qucosa.oai.provider.persistence.pojos.Set>) sets;
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO sets (id, setspec, predicate, doc) \r\n");
        sb.append("VALUES (nextval('oaiprovider'), ?, ?, ?) \r\n");
        sb.append("ON CONFLICT (setspec) \r\n");
        sb.append("DO UPDATE SET doc = ? \r\n");
        
        try {
            PreparedStatement pst = connection().prepareStatement(sb.toString());
            connection().setAutoCommit(false);
            
            for (de.qucosa.oai.provider.persistence.pojos.Set set : data) {
                SQLXML sqlxml = connection().createSQLXML();
                sqlxml.setString(DocumentXmlUtils.resultXml(set.getDocument()));
                
                pst.setString(1, set.getSetSpec());
                pst.setString(2, set.getPredicate());
                pst.setSQLXML(3, sqlxml);
                pst.setSQLXML(4, sqlxml);
                pst.addBatch();
            }
            
            pst.executeBatch();
            connection().commit();
        } catch (SQLException | IOException | SAXException e) {
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

    @Override
    public <T> Set<T> find(String sqlStmt) {
        return null;
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
