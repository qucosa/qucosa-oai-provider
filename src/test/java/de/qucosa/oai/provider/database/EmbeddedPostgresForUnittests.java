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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_5;

public abstract class EmbeddedPostgresForUnittests {
    protected EmbeddedPostgres postgres;

    protected Connection connection;

    @BeforeAll
    public void setUp() throws IOException, SQLException {
        postgres = new EmbeddedPostgres(V9_5);
        String connUrl = postgres.start("localhost", 5434,  "oaiprovider",
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
}
