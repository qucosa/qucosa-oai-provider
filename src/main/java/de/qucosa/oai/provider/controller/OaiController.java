package de.qucosa.oai.provider.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/oai")
public class OaiController {
    
    @GET
    @Path("/ListIdentfieres")
    public Response getTest() {
        return Response.status(200).entity("sdfsdfsdf").build();
    }
}
