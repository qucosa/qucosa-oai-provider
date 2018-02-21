package de.qucosa.oai.provider.persistence.postgres;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import de.qucosa.oai.provider.persistence.PersistenceServiceAbstract;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.Record;

public class RecordService extends PersistenceServiceAbstract implements PersistenceServiceInterface {

    @SuppressWarnings("unchecked")
    @Override
    public Set<Record> findAll() {
        Set<Record> records = new HashSet<>();
        ResultSet result = null;
        String sql = "SELECT id, identifier_id, format, moddate, xmldata FROM records;";
        
        try {
            Statement stmt = connection().createStatement();
            result = stmt.executeQuery(sql);
            
            while(result.next()) {
                Record record = new Record();
                record.setId(result.getLong("id"));
                record.setFormat(result.getLong("format"));
                record.setIdentifierId(result.getLong("identifier_id"));
                record.setModdate(result.getDate("moddate"));
                record.setXmldata(result.getString("xmldata"));
                records.add(record);
            }
            
            result.close();
            connection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return records;
    }

    @Override
    public <T> Set<T> find(String sqlStmt) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void update(Set<T> sets) {
        Set<Record> records = (Set<Record>) sets;
        StringBuffer sb = new StringBuffer();
        sb.append("INSERT INTO records (id, identifier_id, format, moddate, xmldata \r\n)");
        sb.append("VALUES (nextval('oaiprovider'), ?, ?, ?, ?) \r\n");
        sb.append("ON CONFLICT (id) DO UPDATE \r\n");
        sb.append("SET identifier_id = ?, format = ?, moddate = ?, xmldata = ?;");
        
        try {
            PreparedStatement pst = connection().prepareStatement(sb.toString());
            connection().setAutoCommit(false);
            
            for (Record record : records) {
                StringWriter sw = new StringWriter();
                sw.write(record.getXmldata());
                SQLXML sqlxml = connection().createSQLXML();
                sqlxml.setString(sw.toString());
                
                pst.setLong(1, record.getIdentifierId());
                pst.setLong(2, record.getFormat());
                pst.setDate(3, record.getModdate());
                pst.setSQLXML(4, sqlxml);
                
                pst.setLong(5, record.getIdentifierId());
                pst.setLong(6, record.getFormat());
                pst.setDate(7, record.getModdate());
                pst.setSQLXML(8, sqlxml);
                
                pst.addBatch();
            }
            
            pst.executeBatch();
            connection().commit();
            connection().close();
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
}
