package de.qucosa.oai.provider.application;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.postgres.IndentifierService;
import de.qucosa.oai.provider.persistence.postgres.SetService;

public class ApplicationBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(SetService.class).to(PersistenceServiceInterface.class);
        bind(IndentifierService.class).to(PersistenceServiceInterface.class);
    }
}
