package de.qucosa.oai.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class QucosaOaiProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(QucosaOaiProviderApplication.class, args);
    }
}
