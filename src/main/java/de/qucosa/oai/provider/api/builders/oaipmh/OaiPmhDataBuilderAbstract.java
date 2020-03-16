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

import de.qucosa.oai.provider.api.exceptions.XmlDomParserException;
import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.persistence.model.ResumptionToken;
import de.qucosa.oai.provider.persistence.model.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collection;

public abstract class OaiPmhDataBuilderAbstract {

    protected Document oaiPmhTpl;

    protected String verb;

    protected ResumptionToken resumptionToken;

    protected int dataSize;

    protected int recordsProPage;

    protected Document recordTpl;

    public void setOaiPmhTpl(Document oaiPmhTpl) {
        this.oaiPmhTpl = oaiPmhTpl;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public void setResumptionToken(ResumptionToken resumptionToken) {
        this.resumptionToken = resumptionToken;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public void setRecordsProPage(int recordsProPage) {
        this.recordsProPage = recordsProPage;
    }

    protected Document addHeaderTpl(String uid, String lastmoddate, boolean recordStatus, Collection<Set> sets) throws XmlDomParserException {
        Document identifierTpl = DocumentXmlUtils.document(
                getClass().getResourceAsStream("/templates/identifier.xml"), true);
        Node identifier = identifierTpl.getElementsByTagName("identifier").item(0);
        Node datestamp = identifierTpl.getElementsByTagName("datestamp").item(0);
        Node header = identifierTpl.getElementsByTagName("header").item(0);

        if (recordStatus) {
            ((Element) header).setAttribute("status", "deleted");
        }

        identifier.setTextContent(uid);
        datestamp.setTextContent(lastmoddate);

        writeSetsInHeader(identifierTpl, header, sets);

        return identifierTpl;
    }

    protected void writeSetsInHeader(Document identifierTpl, Node header, Collection<Set> sets) {

        if (sets != null && !sets.isEmpty()) {

            for (Set set : sets) {
                Node node = identifierTpl.createElement("setSpec");
                node.setTextContent(set.getSetSpec());
                header.appendChild(identifierTpl.importNode(node, true));
            }
        }
    }

    protected void recordTpl(Class clazz) throws XmlDomParserException {
        recordTpl = DocumentXmlUtils.document(clazz.getResourceAsStream("/templates/record.xml"), true);
    }
}
