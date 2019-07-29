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

import de.qucosa.oai.provider.QucosaOaiProviderApplication;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties= {"spring.main.allow-bean-definition-overriding=true"},
        classes = {QucosaOaiProviderApplication.class, InstallTablesTest.TestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = {InstallTablesTest.Initializer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
public class InstallTablesTest {
    private Logger logger = LoggerFactory.getLogger(InstallTablesTest.class);

    private static Connection connection;

    @Container
    private static PostgreSQLContainer sqlContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:9.5")
            .withDatabaseName("oaiprovider")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("db/init-tables.sql")
            .withStartupTimeoutSeconds(600);

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            sqlContainer.start();

            TestPropertyValues.of(
                    "spring.datasource.url=" + sqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + sqlContainer.getUsername(),
                    "spring.datasource.password=" + sqlContainer.getPassword()
            ).applyTo(configurableApplicationContext);
        }
    }

    @TestConfiguration
    public static class TestConfig {

        @Bean
        public Connection connection() throws SQLException {
            connection = sqlContainer.createConnection("");
            return connection;
        }
    }

    @Test
    @DisplayName(("Check if all tables after embedded postgresql exists."))
    @Order(1)
    public void checkIfInstallAllTablesFromSqlScript() throws SQLException {
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema='public' ORDER BY table_schema,table_name;";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();
        List<String> tables = new ArrayList<>();

        while (resultSet.next()) {
            tables.add(resultSet.getString("table_name"));
        }

        resultSet.close();

        assertThat(tables).isNotEmpty();
        assertThat(tables.size()).isEqualTo(10);

        for (String tableName : tables) {

            switch (tableName) {
                case "disseminations":
                    assertThat(tableName).isEqualTo("disseminations");
                    break;
                case "formats":
                    assertThat(tableName).isEqualTo("formats");
                    break;
                case "records":
                    assertThat(tableName).isEqualTo("records");
                    break;
                case "sets":
                    assertThat(tableName).isEqualTo("sets");
                    break;
                case "resumption_tokens":
                    assertThat(tableName).isEqualTo("resumption_tokens");
                    break;
                case "rst_to_identifiers":
                    assertThat(tableName).isEqualTo("rst_to_identifiers");
                    break;
                case "sets_to_records":
                    assertThat(tableName).isEqualTo("sets_to_records");
                    break;
                case "oai_pmh_list":
                    assertThat(tableName).isEqualTo("oai_pmh_list");
                    break;
                case "oai_pmh_list_by_token":
                    assertThat(tableName).isEqualTo("oai_pmh_list_by_token");
                    break;
            }
        }
    }

    @Test
    @DisplayName("Has table sets inserted data rows.")
    @Order(2)
    public void hasSetsTableDataRows() throws SQLException {
        ResultSet resultSet = dataRows("sets");
        assertThat(resultSet.next()).isTrue();
        resultSet.close();
    }

    @Test
    @DisplayName("Has table records inserted data rows.")
    @Order(3)
    public void hasRecordsTableDataRows() throws SQLException {
        ResultSet resultSet = dataRows("records");
        assertThat(resultSet.next()).isTrue();
        resultSet.close();
    }

    @Test
    @DisplayName("Has table formats inserted data rows.")
    @Order(4)
    public void hasFormatsTableDataRows() throws SQLException {
        ResultSet resultSet = dataRows("formats");
        assertThat(resultSet.next()).isTrue();
        resultSet.close();
    }

    @Test
    @DisplayName("Has n:m table sets_to_records inserted data rows.")
    @Order(5)
    public void hasSetsToRecordsTableDataRows() throws SQLException {
        ResultSet resultSet = dataRows("sets_to_records");
        assertThat(resultSet.next()).isTrue();
        resultSet.close();
    }

    @Test
    @DisplayName("Has table disseminations inserted data rows.")
    @Order(6)
    public void hasDisseminationsTableDataRows() throws SQLException {
        ResultSet resultSet = dataRows("disseminations");
        assertThat(resultSet.next()).isTrue();
        resultSet.close();
    }

    private ResultSet dataRows(String tableName) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    @AfterAll
    public void schutdwonTest() {
        sqlContainer.stop();
    }
}
