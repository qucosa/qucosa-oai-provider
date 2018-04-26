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
import de.qucosa.oai.provider.json.mapper.SetsConfig;
import de.qucosa.oai.provider.xml.mapper.MetsXmlMapper;
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

    private DissTerms dissTerms = null;

    private SetsConfig sets = null;

    private String format = null;

    public RecordXmlBuilder(Document dissemination, Document recordTemplate) {
        this.dissemination = dissemination;
        this.recordTemplate = recordTemplate;
    }

    public Document buildRecord(MetsXmlMapper metsXml) throws XPathExpressionException {
        Node importDissemination = recordTemplate.importNode(dissemination.getDocumentElement(), true);
        metadata().appendChild(importDissemination);
        recordIdentifiere().appendChild(recordTemplate.createTextNode(metsXml.pid()));
        recordDatestamp().appendChild(recordTemplate.createTextNode(metsXml.lastModDate()));
        appendSetSpecs();
        return recordTemplate;
    }

    public RecordXmlBuilder setMetsDocument(Document metsDoc) {
        return this;
    }

    public RecordXmlBuilder setDissTerms(DissTerms dissTerms) {
        this.dissTerms = dissTerms;
        return this;
    }

    public RecordXmlBuilder setSets(SetsConfig sets) {
        this.sets = sets;
        return this;
    }

    public RecordXmlBuilder setFormat(String format) {
        this.format = format;
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

    private boolean matchTerm(String key, String value) throws XPathExpressionException {
        DissTerms.Term term = dissTerms.getTerm(key, format);
        XPath xPath = DocumentXmlUtils.xpath(dissTerms.getMapXmlNamespaces());
        Node node = null;

        if (term != null) {

            if (!term.getTerm().isEmpty()) {
                node = (Node) xPath.compile(term.getTerm().replace("$val", value)).evaluate(dissemination, XPathConstants.NODE);
            }
        }

        return (node != null) ? true : false;
    }

    private void addSetSpec(Node header, SetsConfig.Set set) {
        Node setSpecElem = recordTemplate.createElement("setSpec");
        setSpecElem.appendChild(recordTemplate.createTextNode(set.getSetSpec()));
        header.appendChild(setSpecElem);
    }

    private void appendSetSpecs() throws XPathExpressionException {
        Node header = recordHeader();
        List<SetsConfig.Set> setObjects = sets.getSetObjects();

        for (SetsConfig.Set setObj : setObjects) {
            String predicateKey = null;
            String predicateValue = null;

            if (setObj.getPredicate() != null && !setObj.getPredicate().isEmpty()) {

                if (setObj.getPredicate().contains("=")) {
                    String[] predicate = setObj.getPredicate().split("=");
                    predicateKey = predicate[0];
                    predicateValue = predicate[1];

                    if (!predicateValue.contains("/")) {

                        if (matchTerm(predicateKey, predicateValue)) {
                            addSetSpec(header, setObj);
                        }
                    } else {
                        String[] predicateValues = predicateValue.split("/");

                        if (predicateValues.length > 0) {

                        }
                    }
                } else {
                    predicateKey = setObj.getPredicate();
                }
            }
        }
    }
}
