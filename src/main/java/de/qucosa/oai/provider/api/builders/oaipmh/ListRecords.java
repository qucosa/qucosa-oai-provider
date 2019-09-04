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
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhList;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhListByToken;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class ListRecords extends OaiPmhDataBuilderAbstract implements OaiPmhDataBuilder {
    private Format format;

    private Collection<OaiPmhListByToken> oaiPmhListByToken;

    private Collection<OaiPmhList> oaiPmhList;

    @Override
    public Document oaiXmlData() {

        if (oaiPmhList == null && oaiPmhListByToken == null) {
            throw new RuntimeException("Not exists list objects.");
        }

        recordTpl(getClass());
        Node verbNode = oaiPmhTpl.getElementsByTagName(verb).item(0);

        if (oaiPmhListByToken != null) {
            buildListWithResumptionToken(verbNode, recordTpl.getElementsByTagName("record").item(0),
                    recordTpl.getElementsByTagName("metadata").item(0));
        }

        if (oaiPmhList != null) {
            buildList(verbNode, recordTpl.getElementsByTagName("record").item(0),
                    recordTpl.getElementsByTagName("metadata").item(0));
        }

        return oaiPmhTpl;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Collection<OaiPmhListByToken> getOaiPmhListByToken() {
        return oaiPmhListByToken;
    }

    public void setOaiPmhListByToken(Collection<OaiPmhListByToken> oaiPmhListByToken) {
        this.oaiPmhListByToken = oaiPmhListByToken;
    }

    public Collection<OaiPmhList> getOaiPmhList() {
        return oaiPmhList;
    }

    public void setOaiPmhList(Collection<OaiPmhList> oaiPmhList) {
        this.oaiPmhList = oaiPmhList;
    }

    private void buildListWithResumptionToken(Node verbNode, Node record, Node metadata) {

        for (OaiPmhListByToken obj : oaiPmhListByToken) {

            if (!obj.isRecordStatus()) {
                Document metadataXml = DocumentXmlUtils.document(
                        new ByteArrayInputStream(obj.getXmldata().getBytes(StandardCharsets.UTF_8)), true);
                Node metadataImport = recordTpl.importNode(metadataXml.getDocumentElement(), true);
                metadata.appendChild(metadataImport);
            }

            Node importHeader = recordTpl.importNode(addHeaderTpl(obj.getUid(),
                    DateTimeConverter.sqlTimestampToString(obj.getLastModDate()), obj.isRecordStatus(), obj.getSets()).getDocumentElement(), true);
            record.insertBefore(importHeader, metadata);

            Node importTpl = oaiPmhTpl.importNode(recordTpl.getDocumentElement(), true);
            verbNode.appendChild(importTpl);
        }

        new ResumptionToken(oaiPmhTpl).add(verb, dataSize, resumptionToken, recordsProPage);
    }

    private void buildList(Node verbNode, Node record, Node metadata) {

        for (OaiPmhList obj : oaiPmhList) {

            if (!obj.isRecordStatus()) {
                Document metadataXml = DocumentXmlUtils.document(
                        new ByteArrayInputStream(obj.getXmldata().getBytes(StandardCharsets.UTF_8)), true);
                Node metadataImport = recordTpl.importNode(metadataXml.getDocumentElement(), true);
                metadata.appendChild(metadataImport);
            }

            Node importHeader = recordTpl.importNode(addHeaderTpl(obj.getUid(),
                    DateTimeConverter.sqlTimestampToString(obj.getLastModDate()), obj.isRecordStatus(), obj.getSets()).getDocumentElement(), true);
            record.insertBefore(importHeader, metadata);

            Node importTpl = oaiPmhTpl.importNode(recordTpl.getDocumentElement(), true);
            verbNode.appendChild(importTpl);
        }
    }
}
