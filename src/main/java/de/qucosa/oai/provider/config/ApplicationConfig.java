package de.qucosa.oai.provider.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.DisseminationDao;
import de.qucosa.oai.provider.persitence.dao.postgres.FormatDao;
import de.qucosa.oai.provider.persitence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persitence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persitence.model.Format;
import de.qucosa.oai.provider.persitence.model.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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
    public <T> Dao<T> disseminationDao() { return new DisseminationDao<T>(); }

    @Bean
    public <T> Dao<T> setDao() throws SQLException, PropertyVetoException {
        Dao<Set> setDao = new SetDao();
        ((SetDao<Set>) setDao).setConnection(dataSource());
        return (Dao<T>) setDao;
    }

    @Bean
    public SetApi setApi() throws PropertyVetoException, SQLException {
        SetApi setApi = new SetApi();
        setApi.setDao(setDao());
        return setApi;
    }

    @Bean
    public <T> Dao<T> recordDao() { return new RecordDao<>(); }

    @Bean
    public <T> Dao<T> formatDao() throws PropertyVetoException, SQLException {
        Dao<Format> formatDao = new FormatDao();
        ((FormatDao<Format>) formatDao).setConnection(dataSource());
        return (Dao<T>) formatDao;
    }
}
