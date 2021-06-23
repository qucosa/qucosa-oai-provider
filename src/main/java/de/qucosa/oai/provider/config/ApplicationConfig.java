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
package de.qucosa.oai.provider.config;

import de.qucosa.oai.provider.config.json.XmlNamespacesConfig;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.dao.postgres.DisseminationRepository;
import de.qucosa.oai.provider.persistence.dao.postgres.FormatRepository;
import de.qucosa.oai.provider.persistence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persistence.dao.postgres.ResumptionTokenDao;
import de.qucosa.oai.provider.persistence.dao.postgres.RstToIdentifiersDao;
import de.qucosa.oai.provider.persistence.dao.postgres.SetRepository;
import de.qucosa.oai.provider.persistence.dao.postgres.SetsToRecordDao;
import de.qucosa.oai.provider.persistence.dao.postgres.views.OaiPmhListByTokenDao;
import de.qucosa.oai.provider.persistence.dao.postgres.views.OaiPmhListDao;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.ResumptionToken;
import de.qucosa.oai.provider.persistence.model.RstToIdentifiers;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.persistence.model.SetsToRecord;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhList;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhListByToken;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import de.qucosa.oai.provider.services.RecordService;
import de.qucosa.oai.provider.services.ResumptionTokenService;
import de.qucosa.oai.provider.services.RstToIdentifiersService;
import de.qucosa.oai.provider.services.SetService;
import de.qucosa.oai.provider.services.SetsToRecordService;
import de.qucosa.oai.provider.services.views.OaiPmhListByTokenService;
import de.qucosa.oai.provider.services.views.OaiPmhListService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@Configuration
public class ApplicationConfig {
    private final Environment environment;

    public ApplicationConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(environment.getProperty("psql.url"));
        dataSource.setDriverClassName(Objects.requireNonNull(environment.getProperty("psql.driver")));
        dataSource.setUsername(environment.getProperty("psql.user"));
        dataSource.setPassword(environment.getProperty("psql.passwd"));
        return dataSource;
    }

    @Bean
    public Connection connection() throws SQLException {
        return dataSource().getConnection();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Dao<Set> setRepository() throws SQLException {
        return new SetRepository<>(connection());
    }

    @Bean
    public SetService setService() throws SQLException {
        SetService setService = new SetService();
        setService.setDao(setRepository());
        return setService;
    }

    @Bean
    public Dao<Record> recordDao() throws SQLException {
        return new RecordDao<>(connection());
    }

    @Bean
    public RecordService recordService() throws SQLException {
        RecordService recordService = new RecordService();
        recordService.setDao(recordDao());
        return recordService;
    }

    @Bean
    public Dao<Format> formatRepository() throws SQLException {
        return new FormatRepository<>(connection());
    }

    @Bean
    public FormatService formatService() throws SQLException {
        FormatService formatService = new FormatService();
        formatService.setDao(formatRepository());
        return  formatService;
    }

    @Bean
    public Dao<Dissemination> disseminationRepository() throws SQLException {
        return new DisseminationRepository<>(connection());
    }

    @Bean
    public DisseminationService disseminationService() throws SQLException {
        DisseminationService disseminationService = new DisseminationService();
        disseminationService.setDao(disseminationRepository());
        return disseminationService;
    }

    @Bean
    public Dao<SetsToRecord> setsToRecordDao() throws SQLException {
        return new SetsToRecordDao<>(connection());
    }

    @Bean
    public SetsToRecordService setsToRecordService() throws SQLException {
        SetsToRecordService setsToRecordService = new SetsToRecordService();
        setsToRecordService.setDao(setsToRecordDao());
        return setsToRecordService;
    }

    @Bean
    public Dao<ResumptionToken> resumptionTokenDao() throws SQLException {
        return new ResumptionTokenDao<>(connection());
    }

    @Bean
    public ResumptionTokenService resumptionTokenService() throws SQLException {
        ResumptionTokenService resumptionTokenService = new ResumptionTokenService();
        resumptionTokenService.setDao(resumptionTokenDao());
        return resumptionTokenService;
    }

    @Bean
    public Dao<RstToIdentifiers> rstToIdentifiersDao() throws SQLException {
        return new RstToIdentifiersDao<>(connection());
    }

    @Bean
    public RstToIdentifiersService rstToIdentifiersService() throws SQLException {
        RstToIdentifiersService rstToIdentifiersService = new RstToIdentifiersService();
        rstToIdentifiersService.setDao(rstToIdentifiersDao());
        return rstToIdentifiersService;
    }

    @Bean
    public Dao<OaiPmhListByToken> oaiPmhListByTokenDao() throws SQLException {
        return new OaiPmhListByTokenDao<>(connection());
    }

    @Bean
    public OaiPmhListByTokenService oaiPmhListsService() throws SQLException {
        OaiPmhListByTokenService oaiPmhListsService = new OaiPmhListByTokenService();
        oaiPmhListsService.setDao(oaiPmhListByTokenDao());
        return oaiPmhListsService;
    }

    @Bean
    public Dao<OaiPmhList> oaiPmhListDao() throws SQLException {
        return new OaiPmhListDao<>(connection());
    }

    @Bean
    public OaiPmhListService oaiPmhListService() throws SQLException {
        OaiPmhListService oaiPmhListService = new OaiPmhListService();
        oaiPmhListService.setDao(oaiPmhListDao());
        return oaiPmhListService;
    }

    @Bean
    public XmlNamespacesConfig xmlNamespacesConfig() throws IOException {
        return new XmlNamespacesConfig(getClass().getResourceAsStream("/config/namespaces.json"));
    }
}
