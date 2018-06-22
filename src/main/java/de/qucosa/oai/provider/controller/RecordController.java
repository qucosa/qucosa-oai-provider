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
import de.qucosa.oai.provider.application.config.DissTermsDao;
import de.qucosa.oai.provider.application.config.DissTermsMapper;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Dissemination;
import de.qucosa.oai.provider.persistence.pojos.Format;
import de.qucosa.oai.provider.persistence.pojos.Record;
import de.qucosa.oai.provider.persistence.pojos.RecordTransport;
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
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

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
    public Response save(@Context ServletContext servletContext, @Context ResourceContext resourceContext, String input) {

        if (input == null || input.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Request input data is empty or failed!").build();
        }

        List<RecordTransport> inputData;

        try {
            inputData = om.readValue(input.getBytes("UTF-8"),
                    om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));
        } catch (IOException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Cannot build record transport mapping JSON object.").build();
        }

        if (!checkIfOaiDcFormatIsExists(inputData)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The oai_dc format is failed in record input data.").build();
        }

        SetController setController = resourceContext.getResource(SetController.class);

        for (RecordTransport rt : inputData) {

            try {
                setController.save(om.writeValueAsString(rt.getSets()));
            } catch (JsonProcessingException e) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Cannot write set json data.").build();
            }

            Response responseFormat = format(servletContext, resourceContext.getResource(FormatsController.class), rt);

            if (responseFormat.getStatus() != 200) {
                return responseFormat;
            }

            Format format = (Format) responseFormat.getEntity();

            Response recordResponse = record(rt);

            if (recordResponse.getStatus() != 200) {
                return Response.status(recordResponse.getStatus()).entity(recordResponse.getEntity()).build();
            }

            Record record = (Record) recordResponse.getEntity();

            DisseminationController disseminationController = resourceContext.getResource(DisseminationController.class);
            Response dissBuild = disseminationController.build(servletContext, rt);

            if (dissBuild.getStatus() != 200) {
                return dissBuild;
            }

            Document disseminationDocument = (Document) dissBuild.getEntity();

            if (disseminationDocument == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Dissemination document has been not build.").build();
            }

            Response saveDiss = saveDissemination(resourceContext, format, record, disseminationDocument);

            if (saveDiss.getStatus() != 200) {

                if (saveDiss.getStatus() == 406) {
                    return Response.status(Response.Status.NOT_ACCEPTABLE).entity(saveDiss.getEntity()).build();
                }
            }
        }

        return Response.status(Response.Status.OK).entity(true).build();
    }

    @PUT
    @Path("{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("uid") String uid, String input) {

        if (input == null || input.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Input data is failed or empty!").build();
        }

        ObjectMapper om = new ObjectMapper();
        Record record;

        try {
            record = om.readValue(input, Record.class);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Record json mapper is failed!").build();
        }

        if (!record.getUid().equals(uid)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Update UID parameter and record UID are unequal!").build();
        }

        Record result;

        try {
            result = recordDao.update(record);
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @DELETE
    @Path("{uid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("uid") String uid) {

        if (uid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("UID parameter is failed or empty!").build();
        }

        try {
            recordDao.deleteByKeyValue("uid", uid);
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("{uid}")
    public Response find(@PathParam("uid") String uid) {

        if (uid == null || uid.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The uid paramter is failed or empty!").build();
        }

        Record record;

        try {
            record = recordDao.findByValue("uid", uid);
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Record with uid " + uid +" is not found.").build();
        }

        return Response.status(Response.Status.OK).entity(record).build();
    }

    private boolean checkIfOaiDcFormatIsExists(List<RecordTransport> inputdData) {
        boolean isExists = false;

        for (RecordTransport rt : inputdData) {

            if (rt.getMdprefix().equals("oai_dc")) {
                isExists = true;
                break;
            }
        }

        return isExists;
    }

    private Response format(ServletContext servletContext, FormatsController formatsController, RecordTransport rt) {
        Response resFormat = formatsController.format(rt.getMdprefix());

        if (resFormat.getStatus() != 200) {
            Format format = new Format();
            DissTermsDao dissconf = (DissTermsDao) servletContext.getAttribute("dissConf");
            Set<DissTermsMapper.DissFormat> formats = dissconf.getFormats();

            for (DissTermsMapper.DissFormat fm : formats) {

                if (fm.getMdprefix().equals(rt.getMdprefix())) {
                    format.setMdprefix(fm.getMdprefix());
                    format.setSchemaUrl(fm.getSchemaUrl());
                    format.setNamespace(fm.getNamespace());
                    break;
                }
            }

            try {
                resFormat = formatsController.save(om.writeValueAsString(format));
            } catch (JsonProcessingException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Cannot build format object for save process.").build();
            }

            if (resFormat.getStatus() != 200) {
                return Response.status(resFormat.getStatus()).entity(resFormat.readEntity(String.class)).build();
            }
        }

        return resFormat;
    }

    private Response record(RecordTransport rt) {
        Response recordResonse = this.find(rt.getOaiId());

        if (recordResonse.getStatus() == 404) {
            Record record = new Record();
            record.setPid(rt.getPid());
            record.setUid(rt.getOaiId());

            try {
                return this.update("", om.writeValueAsString(record));
            } catch (IOException e) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Cannot write record json data.").build();
            }
        }

        return recordResonse;
    }

    private Response saveDissemination(ResourceContext resourceContext, Format format, Record record, Document disseminationDoc) {
        Dissemination dissemination = new Dissemination();
        DisseminationController disseminationController = resourceContext.getResource(DisseminationController.class);
        dissemination.setFormatId(format.getId());
        dissemination.setRecordId(record.getId());

        try {
            dissemination.setXmldata(DocumentXmlUtils.resultXml(disseminationDoc));
            return disseminationController.save(om.writeValueAsString(dissemination));
        } catch (SAXException | IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot build dissemination object.").build();
        }
    }
}
