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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.application.mapper.SetsConfig;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.Format;
import de.qucosa.oai.provider.persistence.pojos.RecordTransport;
import de.qucosa.oai.provider.persistence.postgres.FormatService;
import de.qucosa.oai.provider.xml.builders.SetXmlBuilder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/updatecache")
@RequestScoped
public class UpdateCacheController {

    private PersistenceServiceInterface formatService;

    private ObjectMapper om = new ObjectMapper();

    @Inject
    public UpdateCacheController(FormatService formatService) {
        this.formatService = formatService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Context ServletContext servletContext, @Context ResourceContext resourceContext, String input) throws IOException, SQLException, SAXException {

        if (input.isEmpty() || input == null) {
            Response.status(Response.Status.BAD_REQUEST).entity("Request input data is empty or failed!").build();
        }

        List<RecordTransport> inputData = om.readValue(input.getBytes("UTF-8"),
                om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));
        SetController setController = resourceContext.getResource(SetController.class);
        FormatsController formatsController = resourceContext.getResource(FormatsController.class);

        for (RecordTransport rt : inputData) {
            Response resSet = setController.save(om.writeValueAsString(sets(rt.getSets(), (SetsConfig) servletContext.getAttribute("sets"))));
            Response resFormat = formatsController.format(servletContext, rt.getPrefix());
            Format format = (Format) resFormat.getEntity();

            if (format == null) {
                addFormat(resourceContext, rt);
            }
        }

        return null;
    }

    private Set<de.qucosa.oai.provider.persistence.pojos.Set> sets(List<String> sets, SetsConfig setsConfig) {
        Set<de.qucosa.oai.provider.persistence.pojos.Set> output = new HashSet<>();

        for (String setspec : sets) {
            SetsConfig.Set set = setsConfig.getSetObject(setspec);
            de.qucosa.oai.provider.persistence.pojos.Set updateSet = new de.qucosa.oai.provider.persistence.pojos.Set();
            updateSet.setSetSpec(set.getSetSpec());
            updateSet.setPredicate(set.getPredicate());
            updateSet.setDocument(SetXmlBuilder.build(set));
            output.add(updateSet);
        }

        return output;
    }

    private int cntFormat(RecordTransport rt) throws SQLException {
        return formatService.count("id", "mdprefix", rt.getPrefix());
    }

    private Format getFormat(RecordTransport rt) {
        return formatService.findByValue("mdprefix", rt.getPrefix());
    }

    private Response addFormat(ResourceContext resourceContext, RecordTransport rt) throws SQLException, JsonProcessingException {
        FormatsController fc = resourceContext.getResource(FormatsController.class);
        Format format = new Format();
        format.setMdprefix(rt.getPrefix());
        Response rb = fc.save(om.writeValueAsString(format));
        return rb;
    }
}
