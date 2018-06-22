/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.application.config.DissTermsDao;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Dissemination;
import de.qucosa.oai.provider.persistence.pojos.RecordTransport;
import de.qucosa.oai.provider.xml.builders.DisseminationXmlBuilder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.sql.SQLException;

@Path("/dissemination")
@RequestScoped
public class DisseminationController {
    private PersistenceDaoInterface disseminationDao;

    @Inject
    public DisseminationController (PersistenceDaoInterface disseminationDao) {
        this.disseminationDao = disseminationDao;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(String input) {

        if (input == null || input.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The input data object is empty or failed!").build();
        }

        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination;

        try {
            dissemination = om.readValue(input, Dissemination.class);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot build dissemination object.").build();
        }

        try {
            dissemination = disseminationDao.update(dissemination);
        } catch(SQLException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("The dissemination object uncompleted.").build();
        }

        return Response.status(200).entity(dissemination).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response build(@Context ServletContext servletContext, RecordTransport rt) {

        if (rt.getData() == null) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Not found xml for parsing dissemination document.").build();
        }

        Document disseminationDocument;

        try {
            disseminationDocument = new DisseminationXmlBuilder(rt)
                    .setDissTerms((DissTermsDao) servletContext.getAttribute("dissConf"))
                    .buildDissemination();
        } catch (XPathExpressionException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        if (disseminationDocument == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(null).build();
        }

        return Response.status(Response.Status.OK).entity(disseminationDocument).build();
    }

    @GET
    @Path("{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response dissemination(@PathParam("uid") String uid) {
        return Response.status(Response.Status.OK).entity("").build();
    }
}
