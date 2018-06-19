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
            Format format = format(getFormat(servletContext, resourceContext.getResource(FormatsController.class), rt));

            if (format == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Format is not found.").build();
            }

            Record record = record(servletContext, resourceContext, rt);

            if (record == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Record is not found.").build();
            }

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
    public Response update(@PathParam("uid") String uid, String input) throws IOException, SQLException, SAXException {

        if (input == null || input.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Input data is failed or empty!").build();
        }

        ObjectMapper om = new ObjectMapper();
        Record record = om.readValue(input, Record.class);

        if (record == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Record json mapper is failed!").build();
        }

        if (!record.getUid().equals(uid)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Update UID parameter and record UID ar unequal!").build();
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

        if (record == null || record.getId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The mapping object is failed!").build();
        }

        return Response.status(Response.Status.OK).entity(record).build();
    }

    public Format format(Format format) {
        return format;
    }

    public Record record(ServletContext servletContext, ResourceContext resourceContext, RecordTransport rt) throws SQLException, SAXException, XPathExpressionException, IOException {
        return getRecord(servletContext, resourceContext, rt);
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

    private Format getFormat(ServletContext servletContext, FormatsController formatsController, RecordTransport rt) throws SQLException, IOException, SAXException {
        Response resFormat = formatsController.format(rt.getMdprefix());
        Format format;

        if (resFormat.getStatus() != 200) {
            format = new Format();
            DissTerms dissconf = (DissTerms) servletContext.getAttribute("dissConf");
            Set<DissTerms.DissFormat> formats = dissconf.formats();

            for (DissTerms.DissFormat fm : formats) {

                if (fm.getMdprefix().equals(rt.getMdprefix())) {
                    format.setMdprefix(fm.getMdprefix());
                    format.setSchemaUrl(fm.getSchemaUrl());
                    format.setNamespace(fm.getNamespace());
                    break;
                }
            }

            formatsController.save(om.writeValueAsString(format));
            Response response = formatsController.format(rt.getMdprefix());

            if (response.getEntity() instanceof Format) {
                format = (Format) formatsController.format(rt.getMdprefix()).getEntity();
            } else {
                format = null;
            }

        } else {
            format = (Format) resFormat.getEntity();
        }

        return format;
    }

    private Record getRecord(ServletContext servletContext, ResourceContext resourceContext, RecordTransport rt) throws SQLException, IOException, SAXException, XPathExpressionException {
        Record record;
        Response recordResonse = this.find(rt.getOaiId());

        if (recordResonse.getStatus() != 200) {
            record = new Record();
            record.setPid(rt.getPid());
            record.setUid(rt.getOaiId());
//            this.save(servletContext, resourceContext, om.writeValueAsString(record));
            this.update("", om.writeValueAsString(record));
            Response response = this.find(rt.getPid());

            if (response.getEntity() instanceof Record) {
                record = (Record) this.find(rt.getPid()).getEntity();
            } else {
                record = null;
            }
        } else {
            record = (Record) recordResonse.getEntity();
        }

        return record;
    }

    private Response saveDissemination(ResourceContext resourceContext, Format format, Record record, Document disseminationDoc) throws IOException, SAXException, SQLException {
        Dissemination dissemination = new Dissemination();
        DisseminationController disseminationController = resourceContext.getResource(DisseminationController.class);
        dissemination.setFormatId(format.getId());
        dissemination.setRecordId(record.getId());
        dissemination.setXmldata(DocumentXmlUtils.resultXml(disseminationDoc));
        Response response = disseminationController.save(om.writeValueAsString(dissemination));
        return response;
    }
}
