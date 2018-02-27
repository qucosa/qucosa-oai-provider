package de.qucosa.oai.provider.application;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.qucosa.oai.provider.persistence.postgres.FormatService;
import de.qucosa.oai.provider.persistence.postgres.IndentifierService;
import de.qucosa.oai.provider.persistence.postgres.SetService;

public class ApplicationBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(SetService.class).to(SetService.class);
        bind(IndentifierService.class).to(IndentifierService.class);
        bind(FormatService.class).to(FormatService.class);
    }
}
