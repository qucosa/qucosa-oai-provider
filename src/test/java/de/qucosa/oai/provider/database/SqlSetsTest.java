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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_5;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqlSetsTest {

    private EmbeddedPostgres postgres;

    private Connection connection;

    private String connUrl;

    @BeforeAll
    public void setUp() throws IOException, SQLException {
        postgres = new EmbeddedPostgres(V9_5);
        connUrl = postgres.start("localhost", 5432,  "oaiprovider",
                "postgres", "postgres");
        connection = DriverManager.getConnection(connUrl);
        postgres.getProcess().get().importFromFile(
                new File(getClass().getResource("/database/psql-oia-provider-test-data.sql").getPath()));

        System.out.println("init global for all tests.");
    }

    @AfterAll
    public void destroy() throws SQLException {
        connection.close();
        postgres.stop();
        System.out.println("destroy all inits.");
    }

    @Test
    public void Find_all_sets() throws SQLException, NotFound {
        Dao<Set> dao = new SetDao<Set>(connection);
        Collection<Set> sets = dao.findAll();
        assertThat(sets.size() ).isGreaterThan(0);

        System.out.println("find all sets test");
    }

    @Test
    public void Find_set_by_setspec() throws NotFound {
        Dao<Set> dao = new SetDao<Set>(connection);
        Collection<Set> sets = dao.findByPropertyAndValue("setspec","ddc:610");
        Set set = sets.iterator().next();

        assertThat(set.getSetSpec()).isEqualTo("ddc:610");

        System.out.println("find set by setspec,.");
    }
}
