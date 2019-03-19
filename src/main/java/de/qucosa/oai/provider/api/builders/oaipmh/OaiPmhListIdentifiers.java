/*
 * *
 *     ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *     ~
 *     ~ Licensed under the Apache License, Version 2.0 (the "License");
 *     ~ you may not use this file except in compliance with the License.
 *     ~ You may obtain a copy of the License at
 *     ~
 *     ~     http://www.apache.org/licenses/LICENSE-2.0
 *     ~
 *     ~ Unless required by applicable law or agreed to in writing, software
 *     ~ distributed under the License is distributed on an "AS IS" BASIS,
 *     ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     ~ See the License for the specific language governing permissions and
 *     ~ limitations under the License.
 *
 */

package de.qucosa.oai.provider.api.builders.oaipmh;

import de.qucosa.oai.provider.api.utils.DateTimeConverter;
import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collection;

public class OaiPmhListIdentifiers extends OaiPmhList implements OaiPmhListBuilder {

    public OaiPmhListIdentifiers(Document oaiPmhTemplate) {
        super(oaiPmhTemplate);
    }

    @Override
    public void list() throws NotFound {
        Node listNode = listNode();
        Element element = oaiPmhTemplate.getDocumentElement();
        Node imported = oaiPmhTemplate.importNode(listNode, true);
        element.appendChild(imported);

        buildIdentirierList();
    }

    private void buildIdentirierList() throws NotFound {
        Node listIdentifiers = oaiPmhTemplate.getElementsByTagName(verb).item(0);

        for (Record record : records) {
            record = records.iterator().next();
            Collection<Set> sets = setsToRecordService.findByPropertyAndValue("rc.id", String.valueOf(record.getIdentifier()));

            Dissemination dissemination = disseminationService.findByMultipleValues(
                    "id_format = %s AND id_record = %s", String.valueOf(format.getIdentifier()), record.getUid());

            Document identifierTpl = DocumentXmlUtils.document(
                    getClass().getResourceAsStream("/templates/identifier.xml"), true);
            Node identifier = identifierTpl.getElementsByTagName("identifier").item(0);
            identifier.setTextContent(record.getUid());

            Node datestamp = identifierTpl.getElementsByTagName("datestamp").item(0);
            datestamp.setTextContent(dissemination.getLastmoddate().toString());

            writeSetsInHeader(identifierTpl, sets);

            listIdentifiers.appendChild(oaiPmhTemplate.importNode(identifierTpl.getDocumentElement(), true));
            addResumtionToken();
        }
    }

    private void writeSetsInHeader(Document identifierTpl, Collection<Set> sets) {
        Node header = identifierTpl.getElementsByTagName("header").item(0);

        for (Set set : sets) {
            Node node = identifierTpl.createElement("setspec");
            node.setTextContent(set.getSetSpec());
            header.appendChild(identifierTpl.importNode(node, true));
        }
    }

    private void addResumtionToken() {
        Node nodeByVerb = oaiPmhTemplate.getElementsByTagName(verb).item(0);
        Document resumptionTokenDoc = DocumentXmlUtils.document(
                getClass().getResourceAsStream("/templates/resumption-token.xml"), true);
        Node resumptionTokenElem = resumptionTokenDoc.getDocumentElement();
        resumptionTokenElem.setTextContent(resumptionToken.getTokenId());
        resumptionTokenElem.getAttributes().getNamedItem("cursor").setNodeValue(String.valueOf(resumptionToken.getCursor()));
        resumptionTokenElem.getAttributes().getNamedItem("expirationDate").setNodeValue(
                DateTimeConverter.sqlTimestampToString(resumptionToken.getExpirationDate())
        );
        resumptionTokenElem.getAttributes().getNamedItem("completeListSize").setNodeValue(
                String.valueOf(records.size())
        );

        nodeByVerb.appendChild(oaiPmhTemplate.importNode(resumptionTokenDoc.getDocumentElement(), true));
    }
}
