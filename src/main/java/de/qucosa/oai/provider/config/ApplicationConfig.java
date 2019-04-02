/**
    ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
    ~
    ~ Licensed under the Apache License, Version 2.0 (the "License");
    ~ you may not use this file except in compliance with the License.
    ~ You may obtain a copy of the License at
    ~
    ~     http://www.apache.org/licenses/LICENSE-2.0
    ~
    ~ Unless required by applicable law or agreed to in writing, software
    ~ distributed under the License is distributed on an "AS IS" BASIS,
    ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    ~ See the License for the specific language governing permissions and
    ~ limitations under the License.
 */
package de.qucosa.oai.provider.config;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.dao.postgres.DisseminationDao;
import de.qucosa.oai.provider.persistence.dao.postgres.FormatDao;
import de.qucosa.oai.provider.persistence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persistence.dao.postgres.ResumptionTokenDao;
import de.qucosa.oai.provider.persistence.dao.postgres.RstToIdentifiersDao;
import de.qucosa.oai.provider.persistence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persistence.dao.postgres.SetsToRecordDao;
import de.qucosa.oai.provider.persistence.dao.postgres.views.OaiPmhListsDao;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.ResumptionToken;
import de.qucosa.oai.provider.persistence.model.RstToIdentifiers;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.persistence.model.SetsToRecord;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhLists;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import de.qucosa.oai.provider.services.RecordService;
import de.qucosa.oai.provider.services.ResumptionTokenService;
import de.qucosa.oai.provider.services.RstToIdentifiersService;
import de.qucosa.oai.provider.services.SetService;
import de.qucosa.oai.provider.services.SetsToRecordService;
import de.qucosa.oai.provider.services.views.OaiPmhListsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class ApplicationConfig {
    private Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @Autowired
    private Environment environment;

    @Bean
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(environment.getProperty("psql.url"));
        dataSource.setDriverClassName(environment.getProperty("psql.driver"));
        dataSource.setUsername(environment.getProperty("psql.user"));
        dataSource.setPassword(environment.getProperty("psql.passwd"));
        return dataSource;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Dao<Set> setDao() throws SQLException {
        return new SetDao<>(dataSource().getConnection());
    }

    @Bean
    public SetService setService() throws SQLException {
        SetService setService = new SetService();
        setService.setDao(setDao());
        return setService;
    }

    @Bean
    public Dao<Record> recordDao() throws SQLException {
        return new RecordDao<>(dataSource().getConnection());
    }

    @Bean
    public RecordService recordService() throws SQLException {
        RecordService recordService = new RecordService();
        recordService.setDao(recordDao());
        return recordService;
    }

    @Bean
    public Dao<Format> formatDao() throws SQLException {
        return new FormatDao<>(dataSource().getConnection());
    }

    @Bean
    public FormatService formatService() throws SQLException {
        FormatService formatService = new FormatService();
        formatService.setDao(formatDao());
        return  formatService;
    }

    @Bean
    public Dao<Dissemination> disseminationDao() throws SQLException {
        return new DisseminationDao<>(dataSource().getConnection());
    }

    @Bean
    public DisseminationService disseminationService() throws SQLException {
        DisseminationService disseminationService = new DisseminationService();
        disseminationService.setDao(disseminationDao());
        return disseminationService;
    }

    @Bean
    public Dao<SetsToRecord> setsToRecordDao() throws SQLException {
        return new SetsToRecordDao<>(dataSource().getConnection());
    }

    @Bean
    public SetsToRecordService setsToRecordService() throws SQLException {
        SetsToRecordService setsToRecordService = new SetsToRecordService();
        setsToRecordService.setDao(setsToRecordDao());
        return setsToRecordService;
    }

    @Bean
    public Dao<ResumptionToken> resumptionTokenDao() throws SQLException {
        return new ResumptionTokenDao<>(dataSource().getConnection());
    }

    @Bean
    public ResumptionTokenService resumptionTokenService() throws SQLException {
        ResumptionTokenService resumptionTokenService = new ResumptionTokenService();
        resumptionTokenService.setDao(resumptionTokenDao());
        return resumptionTokenService;
    }

    @Bean
    public Dao<RstToIdentifiers> rstToIdentifiersDao() throws SQLException {
        return new RstToIdentifiersDao<>(dataSource().getConnection());
    }

    @Bean
    public RstToIdentifiersService rstToIdentifiersService() throws SQLException {
        RstToIdentifiersService rstToIdentifiersService = new RstToIdentifiersService();
        rstToIdentifiersService.setDao(rstToIdentifiersDao());
        return rstToIdentifiersService;
    }

    @Bean
    public Dao<OaiPmhLists> oaiPmhListsDao() throws SQLException {
        return new OaiPmhListsDao<>(dataSource().getConnection());
    }

    @Bean
    public OaiPmhListsService oaiPmhListsService() throws SQLException {
        OaiPmhListsService oaiPmhListsService = new OaiPmhListsService();
        oaiPmhListsService.setDao(oaiPmhListsDao());
        return oaiPmhListsService;
    }
}
