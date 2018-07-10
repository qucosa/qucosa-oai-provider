package de.qucosa.oai.provider.config;

import de.qucosa.oai.provider.config.dao.DissTermsJson;
import de.qucosa.oai.provider.config.dao.SetJson;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.dao.postgres.DisseminationDao;
import de.qucosa.oai.provider.persitence.dao.postgres.FormatDao;
import de.qucosa.oai.provider.persitence.dao.postgres.RecordDao;
import de.qucosa.oai.provider.persitence.dao.postgres.SetDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.io.FileNotFoundException;

@Import({DissTermsJson.class, SetJson.class})
@Configuration
public class ApplicationConfig {
    private Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @Autowired
    private Environment environment;

    @Bean
    public DissTermsJson dissTermsJson() {
        try {
            return new DissTermsJson(environment.getProperty("config.path") + "/dissemination-config.json");
        } catch (FileNotFoundException e) {
            logger.error(environment.getProperty("config.path") + "/dissemination-config.json" + " not found.");
            throw new RuntimeException("Cannot start application.", e);
        }
    }

    @Bean
    public SetJson setJson() {
        try {
            return new SetJson(environment.getProperty("config.path") + "/list-set-conf.json");
        } catch (FileNotFoundException e) {
            logger.error(environment.getProperty("config.path") + "/list-set-conf.json" + " not found.");
            throw new RuntimeException("Cannot start application.", e);
        }
    }

    @Bean
    public <T> Dao<T> disseminationDao() { return new DisseminationDao<T>(); }

    @Bean
    public <T> Dao<T> setDao() { return new SetDao<T>(); }

    @Bean
    public <T> Dao<T> recordDao() { return new RecordDao<>(); }

    @Bean
    public <T> Dao<T> formatDao() { return new FormatDao<>(); }
}
