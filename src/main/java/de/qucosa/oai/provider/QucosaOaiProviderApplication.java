package de.qucosa.oai.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

@SpringBootApplication
@EnableAutoConfiguration
public class QucosaOaiProviderApplication implements CommandLineRunner {
    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(QucosaOaiProviderApplication.class, args);
    }

    @Override
    public void run(String... args) { }
}
