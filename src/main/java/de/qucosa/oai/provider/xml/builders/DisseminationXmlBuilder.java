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

package de.qucosa.oai.provider.xml.builders;

import de.qucosa.oai.provider.application.config.DissTermsDao;
import de.qucosa.oai.provider.application.config.SetConfigMapper;
import de.qucosa.oai.provider.persistence.pojos.RecordTransport;
import de.qucosa.oai.provider.persistence.utils.DateTimeConverter;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.Set;

public class DisseminationXmlBuilder {
    private Document recordTemplate;

    private RecordTransport record;

    private DissTermsDao dissTerms = null;

    public DisseminationXmlBuilder(RecordTransport record) {
        this.record = record;
        this.recordTemplate = DocumentXmlUtils.document(getClass().getResourceAsStream("/record.xml"), true);
    }

    public Document buildDissemination() throws XPathExpressionException {
        Node importDissemination = recordTemplate.importNode(record.getData().getDocumentElement(), true);
        metadata().appendChild(importDissemination);
        recordIdentifiere().appendChild(recordTemplate.createTextNode(record.getPid()));
        recordDatestamp().appendChild(recordTemplate.createTextNode(DateTimeConverter.sqlTimestampToString(record.getModified())));
        appendSetSpecs();
        return recordTemplate;
    }

    public DisseminationXmlBuilder setDissTerms(DissTermsDao dissTerms) {
        this.dissTerms = dissTerms;
        return this;
    }

    private Node recordHeader() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        return (Node) xPath.compile("//record/header").evaluate(recordTemplate, XPathConstants.NODE);
    }

    private Node recordIdentifiere() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        return (Node) xPath.compile("//record/header/identifier").evaluate(recordTemplate, XPathConstants.NODE);
    }

    private Node recordDatestamp() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        return (Node) xPath.compile("//record/header/datestamp").evaluate(recordTemplate, XPathConstants.NODE);
    }

    private Element metadata() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        return (Element) xPath.compile("//record/metadata").evaluate(recordTemplate, XPathConstants.NODE);
    }

    private void addSetSpec(Node header, String set) {
        Node setSpecElem = recordTemplate.createElement("setSpec");
        setSpecElem.appendChild(recordTemplate.createTextNode(set));
        header.appendChild(setSpecElem);
    }

    private void appendSetSpecs() throws XPathExpressionException {
        Node header = recordHeader();
        Set<SetConfigMapper.Set> sets = record.getSets();

        for (SetConfigMapper.Set set : sets) {
            addSetSpec(header, set.getSetSpec());
        }
    }
}
