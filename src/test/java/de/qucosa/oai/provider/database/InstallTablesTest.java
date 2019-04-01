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
import de.qucosa.oai.provider.config.OaiPmhTestApplicationConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {QucosaOaiProviderApplication.class, OaiPmhTestApplicationConfig.class})
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InstallTablesTest {
    private Logger logger = LoggerFactory.getLogger(InstallTablesTest.class);

    @Autowired
    private OaiPmhTestApplicationConfig config;

    @Test
    @DisplayName(("Check if all tables after embedded postgresql exists."))
    @Order(1)
    public void checkIfInstallAllTablesFromSqlScript() throws SQLException {
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema='public' ORDER BY table_schema,table_name;";
        Statement statement = config.connection.prepareStatement(sql);
        ResultSet resultSet = ((PreparedStatement) statement).executeQuery();
        List<String> tables = new ArrayList<>();

        while (resultSet.next()) {
            tables.add(resultSet.getString("table_name"));
        }

        resultSet.close();

        assertThat(tables).isNotEmpty();
        assertThat(tables.size()).isEqualTo(9);

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
                case "oai_pmh_lists":
                    assertThat(tableName).isEqualTo("oai_pmh_lists");
                    break;
            }
        }
    }

    @Test
    @DisplayName("Has table sets inserted data rows.")
    @Order(3)
    public void hasSetsTableDataRows() throws SQLException {
        ResultSet resultSet = dataRows("sets");
        assertThat(resultSet.next()).isTrue();
        resultSet.close();
    }

    @Test
    @DisplayName("Has table records inserted data rows.")
    @Order(4)
    public void hasRecordsTableDataRows() throws SQLException {
        ResultSet resultSet = dataRows("records");
        assertThat(resultSet.next()).isTrue();
        resultSet.close();
    }

    @Test
    @DisplayName("Has table formats inserted data rows.")
    @Order(2)
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

    @Test
    @DisplayName("Hast table resumption_tokens insterted data rows.")
    @Order(7)
    public void hasResumptionTokensTableDataRows() throws SQLException {
        ResultSet resultSet = dataRows("resumption_tokens");
        assertThat(resultSet.next()).isTrue();
        resultSet.close();
    }

    @Test
    @DisplayName("Hast n:m table rst_to_identifiers inserted data rows.")
    @Order(8)
    public void hasTableRstToIdentifiersDataRows() throws SQLException {
        ResultSet resultSet = dataRows("rst_to_identifiers");
        assertThat(resultSet.next()).isTrue();
        resultSet.close();
    }

    @Test
    @DisplayName("Returns view oai_pmh_lists selected data rows.")
    @Order(9)
    public void hasOamiPmhListsDatarows() throws SQLException {
        ResultSet resultSet = dataRows("oai_pmh_lists");
        assertThat(resultSet.next()).isTrue();
        resultSet.close();
    }

    private ResultSet dataRows(String tableName) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        Statement statement = config.connection.createStatement();
        return statement.executeQuery(sql);
    }
}
