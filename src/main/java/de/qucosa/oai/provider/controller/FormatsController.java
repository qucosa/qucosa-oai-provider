package de.qucosa.oai.provider.controller;

import java.sql.Connection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.process.internal.RequestScoped;

import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.postgres.FormatService;

@Path("/formats")
@RequestScoped
public class FormatsController {
    private Connection connection = new Connect("postgresql", "oaiprovider").connection();
    
    @Inject
    private FormatService formatService;
    
    @PostConstruct
    public void init() {
        formatService.setConnection(connection);
    }
    
    @Path("/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateFormats(String input) {
        
    }
    
    @Path("/listFormats")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listFormats(@Context ServletContext servletContext) {
        return Response.status(200).entity(true).build();
    }
}
