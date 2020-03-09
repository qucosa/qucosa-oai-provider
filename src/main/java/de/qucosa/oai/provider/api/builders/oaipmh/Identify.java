/*
 * Copyright 2019 Saxon State and University Library Dresden (SLUB)
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

package de.qucosa.oai.provider.api.builders.oaipmh;

import de.qucosa.oai.provider.api.utils.DateTimeConverter;
import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collection;

public class Identify extends OaiPmhDataBuilderAbstract implements OaiPmhDataBuilder {
    private final static String PREFIX_IDENTIFY = "oai.pmh.identify";

    private Environment environment;

    private Collection<Dissemination> disseminations;

    @Override
    public Document oaiXmlData() {
        String request = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toString();
        Node verbNode = oaiPmhTpl.getElementsByTagName(verb).item(0);
        ((Element) verbNode).setAttribute("xmlns", environment.getProperty(PREFIX_IDENTIFY + ".xmlns"));
        ((Element) verbNode).setAttribute("xmlns:xsi", environment.getProperty(PREFIX_IDENTIFY + ".xmlns.xsi"));

        buildIdentifyXml(verbNode, request);
        return oaiPmhTpl;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void setDisseminations(Collection<Dissemination> disseminations) {
        this.disseminations = disseminations;
    }

    private void buildIdentifyXml(Node verbNode, String uri) {
        Document identifyTpl = DocumentXmlUtils.document(
                getClass().getResourceAsStream("/templates/identify.xml"), true);

        Node repositoryName = identifyTpl.getElementsByTagName("repositoryName").item(0);
        repositoryName.setTextContent(environment.getProperty(PREFIX_IDENTIFY + ".repositoryName"));

        Node protocolVersion = identifyTpl.getElementsByTagName("protocolVersion").item(0);
        protocolVersion.setTextContent(environment.getProperty(PREFIX_IDENTIFY + ".protocolVersion"));

        Node baseURL = identifyTpl.getElementsByTagName("baseURL").item(0);
        baseURL.setTextContent(uri);

        Node granularity = identifyTpl.getElementsByTagName("granularity").item(0);
        granularity.setTextContent(environment.getProperty(PREFIX_IDENTIFY + ".granularity"));

        Node adminEmail = identifyTpl.getElementsByTagName("adminEmail").item(0);
        adminEmail.setTextContent(environment.getProperty(PREFIX_IDENTIFY + ".adminEmail"));

        Node deletedRecord = identifyTpl.getElementsByTagName("deletedRecord").item(0);
        deletedRecord.setTextContent(environment.getProperty(PREFIX_IDENTIFY + ".deletedRecord"));

        Node compression = identifyTpl.getElementsByTagName("compression").item(0);
        compression.setTextContent(environment.getProperty(PREFIX_IDENTIFY + ".compression"));

        Node earliestDatestamp = identifyTpl.getElementsByTagName("earliestDatestamp").item(0);
        earliestDatestamp.setTextContent(DateTimeConverter.sqlTimestampToString(disseminations.iterator().next().getLastmoddate()));

        Element oaiIdentifier = (Element) identifyTpl.getElementsByTagName("oai-identifier").item(0);
        oaiIdentifier.setAttribute("xmlns", environment.getProperty(PREFIX_IDENTIFY + ".identifier.xmlns"));
        oaiIdentifier.setAttribute("xsi:schemaLocation", environment.getProperty(PREFIX_IDENTIFY + ".identifier.xmlns.xsi"));

        Node schemeNode = identifyTpl.getElementsByTagName("scheme").item(0);
        schemeNode.setTextContent(environment.getProperty(PREFIX_IDENTIFY + ".identifier.scheme"));

        Node repositoryIdentifier = identifyTpl.getElementsByTagName("repositoryIdentifier").item(0);
        repositoryIdentifier.setTextContent(environment.getProperty(PREFIX_IDENTIFY + ".identifier.repositoryIdentifier"));

        Node delimiter = identifyTpl.getElementsByTagName("delimiter").item(0);
        delimiter.setTextContent(environment.getProperty(PREFIX_IDENTIFY + ".identifier.delimiter"));

        Node sampleIdentifier = identifyTpl.getElementsByTagName("sampleIdentifier").item(0);
        sampleIdentifier.setTextContent(environment.getProperty(PREFIX_IDENTIFY + ".identifier.sampleIdentifier"));

        Node removeResumtionTokenHours = identifyTpl.getElementsByTagName("removeResumtionTokenHours").item(0);
        removeResumtionTokenHours.setTextContent(environment.getProperty(PREFIX_IDENTIFY + ".identifier.remove.resumptionToken.hours"));

        Node importedTpl = oaiPmhTpl.importNode(identifyTpl.getDocumentElement(), true);

        oaiPmhTpl.getDocumentElement().removeChild(verbNode);
        oaiPmhTpl.getDocumentElement().appendChild(importedTpl);
    }
}
