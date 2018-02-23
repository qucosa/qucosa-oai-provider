package de.qucosa.oai.provider.application;

import org.glassfish.jersey.server.ResourceConfig;

public class ApplicationConfig extends ResourceConfig {
    public ApplicationConfig() {
        register(new ApplicationBinder());
        packages(true, "de.qucosa.oai.provider.controller");
    }
}
