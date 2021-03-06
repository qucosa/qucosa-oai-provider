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
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhListByToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

@Repository
public class OaiPmhListByTokenDao<T extends OaiPmhListByToken> implements Dao<OaiPmhListByToken> {

    private Connection connection;

    private final static String TABLE_NAME = "oai_pmh_list_by_token";

    private ObjectMapper om;

    @Autowired
    public OaiPmhListByTokenDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
        om = new ObjectMapper();
    }

    public OaiPmhListByTokenDao() { }


    @Override
    public OaiPmhListByToken saveAndSetIdentifier(OaiPmhListByToken object) {
        return new OaiPmhListByToken();
    }

    @Override
    public Collection<OaiPmhListByToken> saveAndSetIdentifier(Collection<OaiPmhListByToken> objects) {
        return new ArrayList<>();
    }

    @Override
    public OaiPmhListByToken update(OaiPmhListByToken object) {
        return new OaiPmhListByToken();
    }

    @Override
    public Collection<OaiPmhListByToken> update() {
        return new ArrayList<>();
    }

    @Override
    public Collection<OaiPmhListByToken> findAll() {
        return new ArrayList<>();
    }

    @Override
    public OaiPmhListByToken findById(String id) {
        return null;
    }

    @Override
    public Collection<OaiPmhListByToken> findByPropertyAndValue(String property, String value) {
        return new ArrayList<>();
    }

    @Override
    public OaiPmhListByToken findByMultipleValues(String clause, String... values) {
        return new OaiPmhListByToken();
    }

    @Override
    public Collection<OaiPmhListByToken> findRowsByMultipleValues(String clause, String... values) throws NotFound {
        if (values[0] == null || values[0].isEmpty() || values[1] == null || values[1].isEmpty()) {
            throw new NotFound("Cannot find oai omh list entries because resumptionToken or format_id failed.");
        }

        clause = clause.replace("%s", "?");
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + clause + " AND visible = true";
        Collection<OaiPmhListByToken> pmhLists = new ArrayList<>();

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, values[0]);
            pst.setLong(2, Long.parseLong(values[1]));
            ResultSet resultSet = pst.executeQuery();

            while (resultSet.next()) {
                OaiPmhListByToken oaiPmhLists = new OaiPmhListByToken();
                oaiPmhLists.setRstId(resultSet.getString("rst_id"));
                oaiPmhLists.setOaiid(resultSet.getString("oaiid"));
                oaiPmhLists.setRecordId(resultSet.getLong("record_id"));
                oaiPmhLists.setLastModDate(resultSet.getTimestamp("lastmoddate"));
                oaiPmhLists.setExpirationDate(resultSet.getTimestamp("expiration_date"));
                oaiPmhLists.setXmldata(resultSet.getString("xmldata"));
                oaiPmhLists.setRecordStatus(resultSet.getBoolean("record_status"));
                oaiPmhLists.setDisseminationStatus(resultSet.getBoolean("dissemination_status"));

                if (resultSet.getString("set") != null) {
                    oaiPmhLists.setSets(
                            om.readValue(
                                    resultSet.getString("set"),
                                    om.getTypeFactory().constructCollectionType(Collection.class, Set.class)));
                }

                pmhLists.add(oaiPmhLists);
            }

            if (pmhLists.isEmpty()) {
                throw new NotFound("Cannot found data from view.");
            }
        } catch (SQLException | JsonParseException | JsonMappingException e) {
            throw new NotFound("SQL-ERROR: Cannot found data from view.", e);
        } catch (IOException ignored) {

        }

        return pmhLists;
    }

    @Override
    public Collection<OaiPmhListByToken> findLastRowsByProperty() {
        return new ArrayList<>();
    }

    @Override
    public Collection<OaiPmhListByToken> findFirstRowsByProperty(String property, int limit) {
        return new ArrayList<>();
    }

    @Override
    public void delete() {

    }

    @Override
    public void delete(String ident) {

    }

    @Override
    public void delete(OaiPmhListByToken object) {

    }
}
