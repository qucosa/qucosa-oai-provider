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

import de.qucosa.oai.provider.OaiPmhTestApplicationConfig;
import de.qucosa.oai.provider.QucosaOaiProviderApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
@SpringBootTest(classes = {QucosaOaiProviderApplication.class, OaiPmhTestApplicationConfig.class})
public class InstallTablesTest {
    private Logger logger = LoggerFactory.getLogger(InstallTablesTest.class);

    @Autowired
    private OaiPmhTestApplicationConfig config;

    @Test
    @DisplayName(("Check if all tables after embedded postgresql exists."))
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
        assertThat(tables.size()).isEqualTo(8);
    }
}
