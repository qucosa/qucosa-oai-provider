package de.qucosa.oai.provider.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.api.dissemination.DisseminationApi;
import de.qucosa.oai.provider.api.format.FormatApi;
import de.qucosa.oai.provider.api.record.RecordApi;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.DisseminationDao;
import de.qucosa.oai.provider.persitence.dao.postgres.FormatDao;
import de.qucosa.oai.provider.persitence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persitence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persitence.model.Dissemination;
import de.qucosa.oai.provider.persitence.model.Format;
import de.qucosa.oai.provider.persitence.model.Record;
import de.qucosa.oai.provider.persitence.model.Set;
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
    public Dao disseminationDao() throws PropertyVetoException, SQLException {
        Dao dao = new DisseminationDao<Dissemination>();
        dao.setConnection(dataSource());
        return dao;
    }

    @Bean
    public DisseminationApi disseminationApi() throws PropertyVetoException, SQLException {
        DisseminationApi api = new DisseminationApi();
        api.setDao(disseminationDao());
        return api;
    }

    @Bean
    public Dao setDao() throws SQLException, PropertyVetoException {
        Dao setDao = new SetDao<Set>();
        setDao.setConnection(dataSource());
        return setDao;
    }

    @Bean
    public SetApi setApi() throws PropertyVetoException, SQLException {
        SetApi setApi = new SetApi();
        setApi.setDao(setDao());
        return setApi;
    }

    @Bean
    public Dao recordDao() throws PropertyVetoException, SQLException {
        Dao recordDao = new RecordDao<Record>();
        recordDao.setConnection(dataSource());
        return recordDao;
    }

    @Bean
    public RecordApi recordApi() throws PropertyVetoException, SQLException {
        RecordApi recordApi = new RecordApi();
        recordApi.setDao(recordDao());
        return recordApi;
    }

    @Bean
    public Dao formatDao() throws PropertyVetoException, SQLException {
        Dao formatDao = new FormatDao<Format>();
        formatDao.setConnection(dataSource());
        return formatDao;
    }

    @Bean
    public FormatApi formatApi() throws PropertyVetoException, SQLException {
        FormatApi formatApi = new FormatApi();
        formatApi.setDao(formatDao());
        return  formatApi;
    }
}
