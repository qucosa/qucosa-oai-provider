/*
 * *
 *     ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *     ~
 *     ~ Licensed under the Apache License, Version 2.0 (the "License");
 *     ~ you may not use this file except in compliance with the License.
 *     ~ You may obtain a copy of the License at
 *     ~
 *     ~     http://www.apache.org/licenses/LICENSE-2.0
 *     ~
 *     ~ Unless required by applicable law or agreed to in writing, software
 *     ~ distributed under the License is distributed on an "AS IS" BASIS,
 *     ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     ~ See the License for the specific language governing permissions and
 *     ~ limitations under the License.
 *
 */

package de.qucosa.oai.provider.persistence.dao.postgres;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.ResumptionToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

@Repository
public class ResumptionTokenDao<T extends ResumptionToken> implements Dao<ResumptionToken> {
    private Connection connection;

    @Value("${expiries.hours}")
    private Integer expiriesHours;

    @Value("${expiries.hours.unit}")
    private String expiriesHoursUnit;

    @Autowired
    public ResumptionTokenDao(Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }

        this.connection = connection;
    }

    public ResumptionTokenDao() {
        this.connection = null;
    }

    @Override
    public ResumptionToken saveAndSetIdentifier(ResumptionToken object) throws SaveFailed {
        String sql = "INSERT INTO resumption_tokens (token_id, expiration_date, cursor, format_id)" +
                " VALUES (?, ?, ?, ?)" +
                " ON CONFLICT (token_id)" +
                " DO NOTHING";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, object.getTokenId());
            statement.setTimestamp(2, object.getExpirationDate());
            statement.setLong(3, object.getCursor());
            statement.setLong(4, object.getFormatId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SaveFailed("Cannot save resumption token (" + object.getTokenId() + ").");
            }

        } catch (SQLException e) {
            throw new SaveFailed("Cannot save resumption token (" + object.getTokenId() + ").", e);
        }

        return object;
    }

    @Override
    public Collection<ResumptionToken> saveAndSetIdentifier(Collection<ResumptionToken> objects) throws SaveFailed {
        return null;
    }

    @Override
    public ResumptionToken update(ResumptionToken object) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<ResumptionToken> update(Collection<ResumptionToken> objects) throws UpdateFailed {
        return null;
    }

    @Override
    public Collection<ResumptionToken> findAll() throws NotFound {
        return null;
    }

    @Override
    public ResumptionToken findById(String id) throws NotFound {
        String sql = "SELECT token_id, expiration_date, cursor, format_id FROM resumption_tokens WHERE token_id = ?";
        ResumptionToken resumptionToken = new ResumptionToken();

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                resumptionToken.setTokenId(resultSet.getString("token_id"));
                resumptionToken.setExpirationDate(resultSet.getTimestamp("expiration_date"));
                resumptionToken.setCursor(resultSet.getLong("cursor"));
                resumptionToken.setFormatId(resultSet.getLong("format_id"));
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new NotFound("Resumption token " + id + " not found.", e);
        }

        return resumptionToken;
    }

    @Override
    public Collection<ResumptionToken> findByPropertyAndValue(String property, String value) throws NotFound {
        return null;
    }

    @Override
    public ResumptionToken findByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public Collection<ResumptionToken> findRowsByMultipleValues(String clause, String... values) throws NotFound {
        return null;
    }

    @Override
    public Collection<ResumptionToken> findLastRowsByProperty(String property, int limit) {
        return null;
    }

    @Override
    public Collection<ResumptionToken> findFirstRowsByProperty(String property, int limit) {
        return null;
    }

    @Override
    public void delete() throws DeleteFailed {
        String sql = "DELETE FROM resumption_tokens where expiration_date < NOW() - INTERVAL '" + expiriesHours + " " + expiriesHoursUnit + "'";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DeleteFailed("Cannot delete resumptionToken row.", e);
        }
    }

    @Override
    public void delete(String ident) throws DeleteFailed {

    }

    @Override
    public void delete(ResumptionToken object) throws DeleteFailed {

    }
}
