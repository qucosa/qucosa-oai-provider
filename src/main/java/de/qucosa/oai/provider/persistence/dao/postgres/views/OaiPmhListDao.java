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

package de.qucosa.oai.provider.persistence.dao.postgres.views;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.utils.DateTimeConverter;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

@Repository
public class OaiPmhListDao<T extends OaiPmhList> implements Dao<OaiPmhList> {

    private Connection connection;

    private ObjectMapper om;

    @Autowired
    public OaiPmhListDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
        om = new ObjectMapper();
    }

    public OaiPmhListDao() { }

    @Override
    public OaiPmhList saveAndSetIdentifier(OaiPmhList object) {
        return new OaiPmhList();
    }

    @Override
    public Collection<OaiPmhList> saveAndSetIdentifier(Collection<OaiPmhList> objects) {
        return new ArrayList<>();
    }

    @Override
    public OaiPmhList update(OaiPmhList object) {
        return new OaiPmhList();
    }

    @Override
    public Collection<OaiPmhList> update() {
        return new ArrayList<>();
    }

    @Override
    public Collection<OaiPmhList> findAll() {
        return new ArrayList<>();
    }

    @Override
    public OaiPmhList findById(String id) {
        return new OaiPmhList();
    }

    @Override
    public Collection<OaiPmhList> findByPropertyAndValue(String property, String value) throws NotFound {

        if (property == null || property.isEmpty()) {
            throw new NotFound("Property param failed.");
        }

        if (value == null || value.isEmpty()) {
            throw new NotFound("Value param failed.");
        }

        Collection<OaiPmhList> oaiPmhList = new ArrayList<>();
        String sql = "SELECT * FROM oai_pmh_list WHERE " + property + " = ? AND visible = true";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setLong(1, Long.parseLong(value));
            ResultSet resultSet = pst.executeQuery();

            while (resultSet.next()) {
                oaiPmhList.add(listData(resultSet));
            }

            resultSet.close();

            if (oaiPmhList.isEmpty()) {
                throw new NotFound("Not found data in view oai_pmh_list.");
            }
        } catch (SQLException | JsonParseException | JsonMappingException e) {
            throw new NotFound("DATA-TYPE-ERROR: Not found data in view oai_pmh_list.", e);
        } catch (IOException ignored) {
        }

        return oaiPmhList;
    }

    @Override
    public OaiPmhList findByMultipleValues(String clause, String... values) {
        return new OaiPmhList();
    }

    @Override
    public Collection<OaiPmhList> findRowsByMultipleValues(String clause, String... values) throws NotFound {
        Collection<OaiPmhList> oaiPmhList = new ArrayList<>();
        String sql = "SELECT * FROM oai_pmh_list WHERE format_id = ? AND visible = true";

        if (clause.isEmpty()) {
            sql += " AND lastmoddate BETWEEN ? AND (?::date + '24 hours'::interval)";
        } else {
            sql += " AND " + clause;
        }

        sql += " ORDER BY lastmoddate ASC";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setLong(1, Long.parseLong(values[0]));

            if (values.length == 3) {
                pst.setTimestamp(2, DateTimeConverter.timestampWithTimezone(values[1]));
                pst.setTimestamp(3, DateTimeConverter.timestampWithTimezone(values[2]));
            } else if (values.length == 2) {
                pst.setTimestamp(2, DateTimeConverter.timestampWithTimezone(values[1]));
            }

            ResultSet resultSet = pst.executeQuery();

            while (resultSet.next()) {
                oaiPmhList.add(listData(resultSet));
            }

            resultSet.close();

            if (oaiPmhList.isEmpty()) {
                throw new NotFound("Not found data in view oai_pmh_list.");
            }
        } catch (SQLException | IOException e) {
            throw new NotFound("SQL-ERROR: Not found data in view oai_pmh_list.", e);
        } catch (DatatypeConfigurationException e) {
            throw new NotFound("DATA-TYPE-ERROR: Not found data in view oai_pmh_list.", e);
        }


        return oaiPmhList;
    }

    @Override
    public Collection<OaiPmhList> findLastRowsByProperty() {
        return new ArrayList<>();
    }

    @Override
    public Collection<OaiPmhList> findFirstRowsByProperty(String property, int limit) {
        return new ArrayList<>();
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(String ident) {

    }

    @Override
    public void delete(OaiPmhList object) {

    }

    private OaiPmhList listData(ResultSet resultSet) throws SQLException, IOException {
        OaiPmhList res = new OaiPmhList();
        res.setRecordId(resultSet.getLong("record_id"));
        res.setPid(resultSet.getString("pid"));
        res.setOaiid(resultSet.getString("oaiid"));
        res.setFormat(resultSet.getLong("format_id"));
        res.setMdprefix(resultSet.getString("mdprefix"));
        res.setLastModDate(resultSet.getTimestamp("lastmoddate"));
        res.setXmldata(resultSet.getString("xmldata"));
        res.setRecordStatus(resultSet.getBoolean("record_status"));
        res.setDissStatus(resultSet.getBoolean("diss_status"));

        if (resultSet.getString("set") != null) {
            res.setSets(om.readValue(resultSet.getString("set"),
                    om.getTypeFactory().constructCollectionType(Collection.class, Set.class)));
        }

        return res;
    }
}
