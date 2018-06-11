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
import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.application.mapper.SetsConfig;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Dissemination;
import de.qucosa.oai.provider.persistence.pojos.Format;
import de.qucosa.oai.provider.persistence.pojos.Record;
import de.qucosa.oai.provider.persistence.pojos.RecordTransport;
import de.qucosa.oai.provider.persistence.postgres.SetsToRecordDao;
import de.qucosa.oai.provider.xml.builders.DisseminationXmlBuilder;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.w3c.dom.Document;
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
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/updatecache")
@RequestScoped
public class UpdateCacheController {

    private ObjectMapper om = new ObjectMapper();

    private PersistenceDaoInterface setsToRecordDao;

    @Inject
    public UpdateCacheController(SetsToRecordDao setsToRecordDao) {
        this.setsToRecordDao = setsToRecordDao;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Context ServletContext servletContext, @Context ResourceContext resourceContext, String input) throws IOException, SQLException, SAXException, XPathExpressionException {

        if (input == null || input.isEmpty()) {
           return Response.status(Response.Status.BAD_REQUEST).entity("Request input data is empty or failed!").build();
        }

        List<RecordTransport> inputData = om.readValue(input.getBytes("UTF-8"),
                om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));
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

        setsToRecordDao.runProcedure();

        return Response.status(Response.Status.OK).build();
    }

    private Response saveDissemination(ResourceContext resourceContext, Format format, Record record, Document disseminationDoc) throws IOException, SAXException, SQLException {
        Dissemination dissemination = new Dissemination();
        DisseminationController disseminationController = resourceContext.getResource(DisseminationController.class);
        dissemination.setFormatId(format.getId());
        dissemination.setRecordId(record.getId());
        dissemination.setXmldata(DocumentXmlUtils.resultXml(disseminationDoc));
        return disseminationController.save(om.writeValueAsString(dissemination));
    }

    /**
     * Returns an format pojo by mdprefix.
     * If format object not exists, else returns after save an new format data row.
     * @param resourceContext
     * @param servletContext
     * @param rt
     * @return Format
     * @throws SQLException
     * @throws IOException 
     */
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

    /**
     *  Returns an record pojo by pid.
     *  If record object not exists, else returns after save an new record data row.
     * @param resourceContext
     * @param servletContext
     * @param rt
     * @return Record
     * @throws SQLException
     * @throws JsonProcessingException
     */
    private Record getRecord(ResourceContext resourceContext, ServletContext servletContext, RecordTransport rt) throws SQLException, IOException, SAXException {
        Record record;
        RecordController recordController = resourceContext.getResource(RecordController.class);
        Response recordResonse = recordController.find(rt.getPid());

        if (recordResonse.getStatus() == 200) {
            record = (Record) recordResonse.getEntity();
        } else {
            record = new Record();
            record.setPid(rt.getPid());
            record.setDatestamp(rt.getModified());
            recordController.save(om.writeValueAsString(record));
            record = (Record) recordController.find(rt.getPid()).getEntity();
        }

        return record;
    }
}
