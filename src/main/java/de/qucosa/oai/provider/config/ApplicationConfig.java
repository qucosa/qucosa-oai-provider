package de.qucosa.oai.provider.config;

import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.DisseminationDao;
import de.qucosa.oai.provider.persitence.dao.postgres.FormatDao;
import de.qucosa.oai.provider.persitence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persitence.dao.postgres.SetDao;
import de.qucosa.oai.provider.persitence.model.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ImportResource({"classpath:applicationContext.xml"})
public class ApplicationConfig {
    private Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @Autowired
    private Environment environment;

    @Bean
    public <T> Dao<T> disseminationDao() { return new DisseminationDao<T>(); }

    @Bean
    public <T> Dao<T> setDao() {
        Dao<Set> setDao = new SetDao();
        ((SetDao<Set>) setDao).setDao(new JdbcTemplate());
        return (Dao<T>) setDao;
    }

    @Bean
    public <T> Dao<T> recordDao() { return new RecordDao<>(); }

    @Bean
    public <T> Dao<T> formatDao() { return new FormatDao<>(); }
}
