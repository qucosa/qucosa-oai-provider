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

package de.qucosa.oai.provider.database;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.model.Set;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.SQLException;
import java.util.Collection;

import static org.assertj.core.api.Java6Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SqlSetsTest extends EmbeddedPostgresForUnittests {

    @Test
    @Order(1)
    public void Find_all_sets() throws SQLException, NotFound {
        Dao<Set> dao = new SetDao<Set>(connection);
        Collection<Set> sets = dao.findAll();
        assertThat(sets.size() ).isGreaterThan(0);

        System.out.println("find all sets test");
    }

    @Test
    @Order(2)
    public void Find_set_by_setspec() throws NotFound {
        Dao<Set> dao = new SetDao<Set>(connection);
        Collection<Set> sets = dao.findByPropertyAndValue("setspec","ddc:610");
        Set set = sets.iterator().next();

        assertThat(set.getSetSpec()).isEqualTo("ddc:610");

        System.out.println("find set by setspec,.");
    }
}
