package de.qucosa.oai.provider.persistence.postgres;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import de.qucosa.oai.provider.persistence.PersistenceServiceAbstract;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.XmlNamespace;

public class XmlNamespaceService extends PersistenceServiceAbstract implements PersistenceServiceInterface  {

    @SuppressWarnings("unchecked")
    @Override
    public Set<XmlNamespace> findAll() {
        String sql = "SELECT id, prefix, url FROM xml_namespaces;";
        Set<XmlNamespace> namespaces = new HashSet<>();
        ResultSet result = null;
        
        try {
            Statement stmt = connection().createStatement();
            result = stmt.executeQuery(sql);
            
            while(result.next()) {
                XmlNamespace namespace = new XmlNamespace();
                namespace.setId(result.getLong("id"));
                namespace.setPrefix(result.getString("prefix"));
                namespace.setUrl(result.getString("url"));
                namespaces.add(namespace);
            }
            
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return namespaces;
    }

    @Override
    public <T> Set<T> find(String sqlStmt) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void update(Set<T> sets) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("INSERT INTO xml_namespaces (id, prefix, url) VALUES (nextval('oaiprovider'), ?, ?) \r\n");
        buffer.append("ON CONFLICT (prefix) DO UPDATE SET \r\n");
        buffer.append("prefix = ?, url = ?; \r\n");
        
        try {
            PreparedStatement pst = connection().prepareStatement(buffer.toString());
            connection().setAutoCommit(false);
            
            for (XmlNamespace namespace : (Set<XmlNamespace>) sets) {
                pst.setString(1, namespace.getPrefix());
                pst.setString(2, namespace.getUrl());
                pst.setString(3, namespace.getPrefix());
                pst.setString(4, namespace.getUrl());
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
