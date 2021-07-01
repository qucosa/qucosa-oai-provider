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
import de.qucosa.oai.provider.persistence.repository.postgres.DisseminationRepository;
import de.qucosa.oai.provider.persistence.repository.postgres.FormatRepository;
import de.qucosa.oai.provider.persistence.repository.postgres.RecordRepository;
import de.qucosa.oai.provider.persistence.repository.postgres.ResumptionTokenRepository;
import de.qucosa.oai.provider.persistence.repository.postgres.RstToIdentifiersRepository;
import de.qucosa.oai.provider.persistence.repository.postgres.SetRepository;
import de.qucosa.oai.provider.persistence.repository.postgres.SetsToRecordRepository;
import de.qucosa.oai.provider.persistence.repository.postgres.views.OaiPmhListByTokenRepository;
import de.qucosa.oai.provider.persistence.repository.postgres.views.OaiPmhListRepository;
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
    public Dao<Record> recordRepository() throws SQLException {
        return new RecordRepository<>(connection());
    }

    @Bean
    public RecordService recordService() throws SQLException {
        RecordService recordService = new RecordService();
        recordService.setDao(recordRepository());
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
    public Dao<SetsToRecord> setsToRecordRepository() throws SQLException {
        return new SetsToRecordRepository<>(connection());
    }

    @Bean
    public SetsToRecordService setsToRecordService() throws SQLException {
        SetsToRecordService setsToRecordService = new SetsToRecordService();
        setsToRecordService.setDao(setsToRecordRepository());
        return setsToRecordService;
    }

    @Bean
    public Dao<ResumptionToken> resumptionTokenRepository() throws SQLException {
        return new ResumptionTokenRepository<>(connection());
    }

    @Bean
    public ResumptionTokenService resumptionTokenService() throws SQLException {
        ResumptionTokenService resumptionTokenService = new ResumptionTokenService();
        resumptionTokenService.setDao(resumptionTokenRepository());
        return resumptionTokenService;
    }

    @Bean
    public Dao<RstToIdentifiers> rstToIdentifiersRepository() throws SQLException {
        return new RstToIdentifiersRepository<>(connection());
    }

    @Bean
    public RstToIdentifiersService rstToIdentifiersService() throws SQLException {
        RstToIdentifiersService rstToIdentifiersService = new RstToIdentifiersService();
        rstToIdentifiersService.setDao(rstToIdentifiersRepository());
        return rstToIdentifiersService;
    }

    @Bean
    public Dao<OaiPmhListByToken> oaiPmhListByTokenRepository() throws SQLException {
        return new OaiPmhListByTokenRepository<>(connection());
    }

    @Bean
    public OaiPmhListByTokenService oaiPmhListsService() throws SQLException {
        OaiPmhListByTokenService oaiPmhListsService = new OaiPmhListByTokenService();
        oaiPmhListsService.setDao(oaiPmhListByTokenRepository());
        return oaiPmhListsService;
    }

    @Bean
    public Dao<OaiPmhList> oaiPmhListRepository() throws SQLException {
        return new OaiPmhListRepository<>(connection());
    }

    @Bean
    public OaiPmhListService oaiPmhListService() throws SQLException {
        OaiPmhListService oaiPmhListService = new OaiPmhListService();
        oaiPmhListService.setDao(oaiPmhListRepository());
        return oaiPmhListService;
    }

    @Bean
    public XmlNamespacesConfig xmlNamespacesConfig() throws IOException {
        return new XmlNamespacesConfig(getClass().getResourceAsStream("/config/namespaces.json"));
    }
}
