package de.qucosa.oai.provider.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.ErrorDetails;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.DisseminationDao;
import de.qucosa.oai.provider.persitence.dao.postgres.FormatDao;
import de.qucosa.oai.provider.persitence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persitence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persitence.dao.postgres.SetsToRecordDao;
import de.qucosa.oai.provider.persitence.model.Dissemination;
import de.qucosa.oai.provider.persitence.model.Format;
import de.qucosa.oai.provider.persitence.model.Record;
import de.qucosa.oai.provider.persitence.model.Set;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import de.qucosa.oai.provider.services.RecordService;
import de.qucosa.oai.provider.services.SetService;
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
    public SetService setService() throws PropertyVetoException, SQLException {
        SetService setService = new SetService();
        setService.setDao(setDao());
        return setService;
    }

    @Bean
    public Dao recordDao() throws PropertyVetoException, SQLException {
        return (Dao<Record>) new RecordDao(dataSource().getConnection());
    }

    @Bean
    public RecordService recordService() throws PropertyVetoException, SQLException {
        RecordService recordService = new RecordService();
        recordService.setDao(recordDao());
        return recordService;
    }

    @Bean
    public Dao formatDao() throws PropertyVetoException, SQLException {
        return (Dao<Format>) new FormatDao(dataSource().getConnection());
    }

    @Bean
    public FormatService formatService() throws PropertyVetoException, SQLException {
        FormatService formatService = new FormatService();
        formatService.setDao(formatDao());
        return  formatService;
    }

    @Bean
    public Dao disseminationDao() throws PropertyVetoException, SQLException {
        return (Dao<Dissemination>) new DisseminationDao(dataSource().getConnection());
    }

    @Bean
    public DisseminationService disseminationService() throws PropertyVetoException, SQLException {
        DisseminationService disseminationService = new DisseminationService();
        disseminationService.setDao(disseminationDao());
        return disseminationService;
    }

    @Bean
    public Dao setsToRecordDao() throws PropertyVetoException, SQLException {
        return new SetsToRecordDao(dataSource().getConnection());
    }

    @Bean
    public ErrorDetails errorDetails() {
        return new ErrorDetails();
    }
}
