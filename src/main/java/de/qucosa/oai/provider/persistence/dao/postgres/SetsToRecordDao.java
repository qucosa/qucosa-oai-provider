/*
 ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 */
package de.qucosa.oai.provider.persistence.dao.postgres;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.HasIdentifier;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.persistence.model.SetsToRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class SetsToRecordDao<T extends SetsToRecord> implements Dao<SetsToRecord>, HasIdentifier {

    private Connection connection;

    @Autowired
    public SetsToRecordDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public SetsToRecordDao() {
        this.connection = null;
    }

    @Override
    public SetsToRecord saveAndSetIdentifier(SetsToRecord object) throws SaveFailed {
        String sql = "INSERT INTO sets_to_records (id_set, id_record) VALUES (?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, object.getIdSet());
            ps.setLong(2, object.getIdRecord());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SaveFailed(e.getMessage());
        }

        return object;
    }

    @Override
    public Collection<SetsToRecord> saveAndSetIdentifier(Collection objects) throws SaveFailed {
        return null;
    }

    @Override
    public SetsToRecord update(SetsToRecord object) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<SetsToRecord> update(Collection objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<SetsToRecord> findAll() throws NotFound {
        return null;
    }

    @Override
    public T findById(String id) throws NotFound {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection findByPropertyAndValue(String property, String value) throws NotFound {
        String sql = "select rc.id, rc.pid, rc.uid, st.id, st.setspec, st.setname, st.setdescription " +
                "from sets_to_records " +
                "left join records rc on rc.id = id_record " +
                "left join sets st on st.id = id_set " +
                "where " + property + "  = ?";
        List<Set> setList = new ArrayList<>();

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setLong(1, Long.valueOf(value));
            ResultSet resultSet = pst.executeQuery();

            while (resultSet.next()) {
                Set set = new Set();
                set.setSetSpec(resultSet.getString("setspec"));
                set.setSetName(resultSet.getString("setname"));
                set.setSetDescription(resultSet.getString("setdescription"));
                setList.add(set);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw  new NotFound(e.getMessage());
        }

        return setList;
    }

    @Override
    public SetsToRecord findByMultipleValues(String clause, String... values) throws NotFound {
        clause = clause.replace("%s", "?");
        String sql = "SELECT * FROM sets_to_records WHERE " + clause;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, Long.valueOf(values[0]));
            ps.setLong(2, Long.valueOf(values[1]));
            ResultSet resultSet = ps.executeQuery();
            SetsToRecord setsToRecord = new SetsToRecord();

            while (resultSet.next()) {
                setsToRecord.setIdSet(resultSet.getLong("id_set"));
                setsToRecord.setIdRecord(resultSet.getLong("id_record"));
            }

            resultSet.close();
            return setsToRecord;
        } catch (SQLException e) {
            throw new NotFound(e.getMessage());
        }
    }

    @Override
    public Collection<SetsToRecord> findRowsByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public SetsToRecord findLastRowsByProperty(String property, int limit) {
        return null;
    }

    @Override
    public SetsToRecord findFirstRowsByProperty(String property, int limit) {
        return null;
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(String ident) throws DeleteFailed {

    }

    @Override
    public void delete(SetsToRecord object) throws DeleteFailed {
        String sql = "DELETE FROM sets_to_records WHERE id_set = ? AND id_record = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setLong(1, object.getIdSet());
            pst.setLong(2, object.getIdRecord());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new DeleteFailed(e.getMessage());
        }
    }

    @Override
    public void setIdentifier(Object identifier) {

    }

    @Override
    public Object getIdentifier() {
        return null;
    }
}
