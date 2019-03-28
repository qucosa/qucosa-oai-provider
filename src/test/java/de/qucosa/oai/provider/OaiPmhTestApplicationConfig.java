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

package de.qucosa.oai.provider;

import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_5;

@TestConfiguration
public class OaiPmhTestApplicationConfig {
    private Logger logger = LoggerFactory.getLogger(OaiPmhTestApplicationConfig.class);

    @Autowired
    private Environment environment;

    private String connUrl;

    public Connection connection;

    @Bean(destroyMethod = "stop")
    public EmbeddedPostgres postgres() throws IOException, SQLException {
        EmbeddedPostgres postgres = new EmbeddedPostgres(V9_5);
        connUrl = postgres.start(environment.getProperty("psql.host"),
                Integer.valueOf(environment.getProperty("psql.port").toString()),
                environment.getProperty("psql.database"),
                environment.getProperty("psql.user"),
                environment.getProperty("psql.passwd"));
        postgres.getProcess().get().importFromFile(
                new File(getClass().getResource("/db/migration/psql-oia-provider-test-data.backup").getPath()));
        connection = DriverManager.getConnection(connUrl);
        return postgres;
    }

    @DependsOn("postgres")
    @Bean
    public DataSource dataSource() {
        return new SimpleDriverDataSource(new Driver(), connUrl);
    }
}
