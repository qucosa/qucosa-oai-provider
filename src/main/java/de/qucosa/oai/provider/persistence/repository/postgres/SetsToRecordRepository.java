/*
 * Copyright 2019 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.qucosa.oai.provider.persistence.repository.postgres;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.model.Identifiable;
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
public class SetsToRecordRepository<T extends SetsToRecord> implements Dao<SetsToRecord>, Identifiable {

    private final Connection connection;

    @Autowired
    public SetsToRecordRepository(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public SetsToRecordRepository() {
        this.connection = null;
    }

    @Override
    public SetsToRecord saveAndSetIdentifier(SetsToRecord object) {
        String sql = "INSERT INTO sets_to_records (id_set, id_record) VALUES (?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, object.getIdSet());
            ps.setLong(2, object.getIdRecord());
            ps.executeUpdate();
        } catch (SQLException e) {
            //throw new SaveFailed(e.getMessage());
        }

        return object;
    }

    @Override
    public Collection<SetsToRecord> saveAndSetIdentifier(Collection objects) {
        return new ArrayList<>();
    }

    @Override
    public SetsToRecord update(SetsToRecord object) {
        return new SetsToRecord();
    }

    @Override
    public Collection<SetsToRecord> update() {
        return new ArrayList<>();
    }

    @Override
    public Collection<SetsToRecord> findAll() {
        return new ArrayList<>();
    }

    @Override
    public SetsToRecord findById(String id) {
        return new SetsToRecord();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection findByPropertyAndValue(String property, String value) {
        String sql = "select rc.id, rc.pid, rc.uid, st.id, st.setspec, st.setname, st.setdescription " +
                "from sets_to_records " +
                "left join records rc on rc.id = id_record " +
                "left join sets st on st.id = id_set " +
                "where " + property + "  = ?";
        List<Set> setList = new ArrayList<>();

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setLong(1, Long.parseLong(value));
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
            //throw  new NotFound(e.getMessage());
        }

        return setList;
    }

    @Override
    public SetsToRecord findByMultipleValues(String clause, String... values) {
        clause = clause.replace("%s", "?");
        String sql = "SELECT * FROM sets_to_records WHERE " + clause;
        SetsToRecord setsToRecord = new SetsToRecord();

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setLong(1, Long.parseLong(values[0]));
            ps.setLong(2, Long.parseLong(values[1]));
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                setsToRecord.setIdSet(resultSet.getLong("id_set"));
                setsToRecord.setIdRecord(resultSet.getLong("id_record"));
            }

            resultSet.close();
        } catch (SQLException e) {
            //throw new NotFound(e.getMessage());
        }
        return setsToRecord;
    }

    @Override
    public Collection<SetsToRecord> findRowsByMultipleValues(String clause, String... values) {
        return new ArrayList<>();
    }

    @Override
    public Collection<SetsToRecord> findFirstRowsByProperty(String property, int limit) {
        return new ArrayList<>();
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(String ident) {

    }

    @Override
    public void delete(SetsToRecord object) {
        String sql = "DELETE FROM sets_to_records WHERE id_set = ? AND id_record = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setLong(1, object.getIdSet());
            pst.setLong(2, object.getIdRecord());
            pst.executeUpdate();
        } catch (SQLException e) {
            //throw new DeleteFailed(e.getMessage());
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
