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
import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Dissemination;
import de.qucosa.oai.provider.persistence.pojos.Format;
import de.qucosa.oai.provider.persistence.pojos.Record;
import de.qucosa.oai.provider.persistence.pojos.RecordTransport;
import de.qucosa.oai.provider.xml.builders.DisseminationXmlBuilder;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Path("/record")
@RequestScoped
public class RecordController {
    private ObjectMapper om = new ObjectMapper();

    private PersistenceDaoInterface recordDao;

    @Inject
    public RecordController(PersistenceDaoInterface recordDao) {
        this.recordDao = recordDao;
    }

    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(@Context ServletContext servletContext, @Context ResourceContext resourceContext, String input) throws IOException, SQLException, SAXException, XPathExpressionException {

        if (input == null || input.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Request input data is empty or failed!").build();
        }

        List<RecordTransport> inputData = om.readValue(input.getBytes("UTF-8"),
                om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));



        if (!checkIfOaiDcFormatIsExists(inputData)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The oai_dc format is failed in record input data.").build();
        }

        SetController setController = resourceContext.getResource(SetController.class);


        for (RecordTransport rt : inputData) {
            setController.save(om.writeValueAsString(rt.getSets()));
            Format format = getFormat(resourceContext, servletContext, rt);

            if (format == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Get and / or save format prefix is failed!").build();
            }

            Record record = getRecord(resourceContext, servletContext, rt);

            if (record == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Get and / or save record prefix is failed!").build();
            }

            Document disseminationDocument = new DisseminationXmlBuilder(rt)
                    .setDissTerms((DissTerms) servletContext.getAttribute("dissConf"))
                    .buildDissemination();

            if (disseminationDocument == null) {
                // @todo log datails message of this failed dissemination document build
                return Response.status(Response.Status.BAD_REQUEST).entity("Dissemination xml document build is failed!").build();
            }

            if (saveDissemination(resourceContext, format, record, disseminationDocument).getStatus() != 200) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Dissemination save is failed!").build();
            }
        }

//        Record record = buildSqlObject(input);
//
//        if (record == null) {
//            return Response.status(Response.Status.BAD_REQUEST).entity("Data json mapper object is failed!").build();
//        }
//
//        int[] result = recordDao.update(record);

        return Response.status(Response.Status.OK).entity(true).build();
    }

    private boolean checkIfOaiDcFormatIsExists(List<RecordTransport> inputdData) {
        boolean isExists = false;

        for (RecordTransport rt : inputdData) {

            if (rt.getPrefix().equals("oai_dc")) {
                isExists = true;
                break;
            }
        }

        return isExists;
    }

    @PUT
    @Path("{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("uid") String uid, String input) throws IOException, SQLException, SAXException {

        if (uid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("UID parameter is failed or empty!").build();
        }

        if (input.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Input data is failed or empty!").build();
        }

        ObjectMapper om = new ObjectMapper();
        Record record = om.readValue(input, Record.class);

        if (record == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Record json mapper is failed!").build();
        }

        if (!record.getPid().equals(uid)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Update UID parameter and record PID ar unequal!").build();
        }

        Record result = recordDao.update(record);

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @DELETE
    @Path("{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("uid") String uid) throws SQLException {

        if (uid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("UID parameter is failed or empty!").build();
        }

        recordDao.deleteByKeyValue("uid", uid);

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("{uid}")
    public Response find(@PathParam("uid") String uid) throws SQLException {

        if (uid == null || uid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The uid paramter is failed or empty!").build();
        }

        Record record = recordDao.findByValue("uid", uid);

        if (record.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The mapping object is failed!").build();
        }

        return Response.status(Response.Status.OK).entity(record).build();
    }

//    private Record buildSqlObject(String json) throws IOException {
//        ObjectMapper om = new ObjectMapper();
//        return om.readValue(json, Record.class);
//    }

    private Format getFormat(ResourceContext resourceContext, ServletContext servletContext, RecordTransport rt) throws SQLException, IOException, SAXException {
        FormatsController formatsController = resourceContext.getResource(FormatsController.class);
        Response resFormat = formatsController.format(servletContext, rt.getPrefix());
        Format format;

        if (resFormat.getStatus() != 200) {
            format = new Format();
            format.setMdprefix(rt.getPrefix());
            formatsController.save(om.writeValueAsString(format));
            format = (Format) formatsController.format(servletContext, rt.getPrefix()).getEntity();
        } else {
            format = (Format) resFormat.getEntity();
        }

        return format;
    }

    private Record getRecord(ResourceContext resourceContext, ServletContext servletContext, RecordTransport rt) throws SQLException, IOException, SAXException, XPathExpressionException {
        Record record;
        RecordController recordController = resourceContext.getResource(RecordController.class);
        Response recordResonse = recordController.find(rt.getPid());

        if (recordResonse.getStatus() == 200) {
            record = (Record) recordResonse.getEntity();
        } else {
            record = new Record();
            record.setPid(rt.getPid());
            recordController.save(servletContext, resourceContext, om.writeValueAsString(record));
            record = (Record) recordController.find(rt.getPid()).getEntity();
        }

        return record;
    }

    private Response saveDissemination(ResourceContext resourceContext, Format format, Record record, Document disseminationDoc) throws IOException, SAXException, SQLException {
        Dissemination dissemination = new Dissemination();
        DisseminationController disseminationController = resourceContext.getResource(DisseminationController.class);
        dissemination.setFormatId(format.getId());
        dissemination.setRecordId(record.getId());
        dissemination.setXmldata(DocumentXmlUtils.resultXml(disseminationDoc));
        return disseminationController.save(om.writeValueAsString(dissemination));
    }
}
