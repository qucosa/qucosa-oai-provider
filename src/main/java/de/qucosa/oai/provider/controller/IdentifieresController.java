package de.qucosa.oai.provider.controller;

import java.sql.Connection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;

public class IdentifieresController {
    private Connection connection = new Connect("postgresql", "oaiprovider").connection();
    
    @Inject
    private PersistenceServiceInterface service;
    
    @PostConstruct
    public void init() {
        service.setConnection(connection);
    }
}
