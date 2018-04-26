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

import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.persistence.pojos.RecordTransport;
import de.qucosa.oai.provider.persistence.utils.DateTimeConverter;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.List;

public class RecordXmlBuilder {
    private Document dissemination = null;

    private Document recordTemplate = null;

    private RecordTransport record = null;

    private DissTerms dissTerms = null;

    public RecordXmlBuilder(RecordTransport record) {
        this.record = record;
        this.dissemination =  DocumentXmlUtils.document(this.record.getData(), true);;
        this.recordTemplate = DocumentXmlUtils.document(getClass().getResourceAsStream("record.xml"), true);
    }

    public Document buildRecord() throws XPathExpressionException {
        Node importDissemination = recordTemplate.importNode(dissemination.getDocumentElement(), true);
        metadata().appendChild(importDissemination);
        recordIdentifiere().appendChild(recordTemplate.createTextNode(record.getPid()));
        recordDatestamp().appendChild(recordTemplate.createTextNode(DateTimeConverter.sqlTimestampToString(record.getModified())));
        appendSetSpecs();
        return recordTemplate;
    }

    public RecordXmlBuilder setDissTerms(DissTerms dissTerms) {
        this.dissTerms = dissTerms;
        return this;
    }

    private Node recordHeader() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        Node header = (Node) xPath.compile("//record/header").evaluate(recordTemplate, XPathConstants.NODE);
        return header;
    }

    private Node recordIdentifiere() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        Node identifier = (Node) xPath.compile("//record/header/identifier").evaluate(recordTemplate, XPathConstants.NODE);
        return identifier;
    }

    private Node recordDatestamp() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        Node datestamp = (Node) xPath.compile("//record/header/datestamp").evaluate(recordTemplate, XPathConstants.NODE);
        return datestamp;
    }

    private Element metadata() throws XPathExpressionException {
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        Element metadata = (Element) xPath.compile("//record/metadata").evaluate(recordTemplate, XPathConstants.NODE);
        return metadata;
    }

    private void addSetSpec(Node header, String set) {
        Node setSpecElem = recordTemplate.createElement("setSpec");
        setSpecElem.appendChild(recordTemplate.createTextNode(set));
        header.appendChild(setSpecElem);
    }

    private void appendSetSpecs() throws XPathExpressionException {
        Node header = recordHeader();
        List<String> sets = record.getSets();

        for (int i = 0; i < sets.size(); i++) {
            addSetSpec(header, sets.get(i));
        }
    }
}
