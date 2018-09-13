package de.qucosa.oai.provider.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.api.dissemination.DisseminationApi;
import de.qucosa.oai.provider.api.format.FormatApi;
import de.qucosa.oai.provider.api.record.RecordApi;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.dao.postgres.DisseminationDao;
import de.qucosa.oai.provider.persistence.dao.postgres.FormatDao;
import de.qucosa.oai.provider.persistence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persistence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persistence.dao.postgres.SetsToRecordDao;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

@Configuration
public class ApplicationConfig {
    private Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @Autowired
    private Environment environment;

    @Bean
    public ComboPooledDataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(environment.getProperty("psql.driver"));
        dataSource.setJdbcUrl(environment.getProperty("psql.url"));
        dataSource.setUser(environment.getProperty("psql.user"));
        dataSource.setPassword(environment.getProperty("psql.passwd"));
        dataSource.setMinPoolSize(Integer.valueOf(environment.getProperty("min.pool.size")));
        dataSource.setMaxPoolSize(Integer.valueOf(environment.getProperty("max.pool.size")));
        return dataSource;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Dao<Set> setDao() throws PropertyVetoException, SQLException {
        return (Dao<Set>) new SetDao(dataSource().getConnection());
    }

    @Bean
    public SetApi setApi() throws PropertyVetoException, SQLException {
        SetApi setApi = new SetApi();
        setApi.setDao(setDao());
        return setApi;
    }

    @Bean
    public Dao recordDao() throws PropertyVetoException, SQLException {
        return (Dao<Record>) new RecordDao(dataSource().getConnection());
    }

    @Bean
    public RecordApi recordApi() throws PropertyVetoException, SQLException {
        RecordApi recordApi = new RecordApi();
        recordApi.setDao(recordDao());
        return recordApi;
    }

    @Bean
    public Dao formatDao() throws PropertyVetoException, SQLException {
        return (Dao<Format>) new FormatDao(dataSource().getConnection());
    }

    @Bean
    public FormatApi formatApi() throws PropertyVetoException, SQLException {
        FormatApi formatApi = new FormatApi();
        formatApi.setDao(formatDao());
        return  formatApi;
    }

    @Bean
    public Dao disseminationDao() throws PropertyVetoException, SQLException {
        return (Dao<Dissemination>) new DisseminationDao(dataSource().getConnection());
    }

    @Bean
    public DisseminationApi disseminationApi() throws PropertyVetoException, SQLException {
        DisseminationApi api = new DisseminationApi();
        api.setDao(disseminationDao());
        return api;
    }

    @Bean
    public Dao setsToRecordDao() throws PropertyVetoException, SQLException {
        return new SetsToRecordDao(dataSource().getConnection());
    }
}
