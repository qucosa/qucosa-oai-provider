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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.persistence.pojos.RecordTransport;
import de.qucosa.oai.provider.xml.builders.RecordXmlBuilder;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.w3c.dom.Document;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Path("/record")
@RequestScoped
public class RecordController {

    @Path("/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRecord(String input) {
        ObjectMapper om = new ObjectMapper();
        List<RecordTransport> inputData = null;

        try {
            inputData = om.readValue(input.getBytes("UTF-8"),
                    om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));

            for (RecordTransport record : inputData) {
                Document recordDoc = new RecordXmlBuilder(record)
                        .setDissTerms(new DissTerms("/home/opt/qucosa-fcrepo-camel/config/"))
                        .buildRecord(DocumentXmlUtils.document(record.getData(), true));
                /**
                 * @// TODO: 26.04.18
                 * add save record in database
                 */
            }
        } catch (JsonParseException | JsonMappingException e) {
            return Response.status(500).entity("Json cannot parsed or mapped out.").build();
        } catch (IOException | XPathExpressionException e) {
            return Response.status(500).entity("A xpath expression is failed.").build();
        }

        return Response.status(200).entity(true).build();
    }
}
