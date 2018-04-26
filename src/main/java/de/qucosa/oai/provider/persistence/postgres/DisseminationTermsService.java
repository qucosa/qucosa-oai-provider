/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
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

package de.qucosa.oai.provider.persistence.postgres;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import de.qucosa.oai.provider.persistence.PersistenceServiceAbstract;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.DisseminationTerm;

public class DisseminationTermsService extends PersistenceServiceAbstract implements PersistenceServiceInterface {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Set<T> findAll() {
        String sql = "select * from dissemination_terms;";
        Set<DisseminationTerm> terms = new HashSet<>();
        
        try {
            Statement stmt = connection().createStatement();
            terms = (Set<DisseminationTerm>) stmt.executeQuery(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return (Set<T>) terms;
    }

    @Override
    public <T> Set<T> find(String sqlStmt) {
        return null;
    }

    @Override
    public <T> void update(Set<T> sets) {}
    
    @Override
    public void update(String sql) {}

    @Override
    public <T> T findById(Long id) {
        return null;
    }

    @Override
    public <T> T findByValue(String column, String value) {
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

    @Override
    public void update(String... value) {
        
        try {
            CallableStatement ct = connection().prepareCall("{call generate_dissemination_terms(?, ?, ?)}");
            connection().setAutoCommit(false);
            ct.setString(1, value[1]);
            ct.setString(2, value[0]);
            ct.setString(3, value[2]);
            ct.executeUpdate();
            connection().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findByValues(String... values) {
        DisseminationTerm dt = new DisseminationTerm();
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT p.predicate, f.mdprefix, dt.term FROM dissemination_terms dt \r\n");
        sb.append("LEFT JOIN formats f on f.id = dt.format_id \r\n");
        sb.append("LEFT JOIN dissemination_predicates p on p.id = dt.diss_predicate_id \r\n");
        sb.append("WHERE p.predicate = ? \r\n");
        sb.append("AND f.mdprefix = ?;");
        
        try {
            PreparedStatement pst = connection().prepareStatement(sb.toString());
            pst.setString(1, values[0]);
            pst.setString(2, values[1]);
            dt = (DisseminationTerm) pst.executeQuery();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return (T) dt;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T findByIds(T... values) {
        // TODO Auto-generated method stub
        return null;
    }
}
