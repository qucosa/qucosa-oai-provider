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
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhList;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhListByToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Collection;

public class ListRecords extends OaiPmhDataBuilderAbstract implements OaiPmhDataBuilder {
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

    public void setOaiPmhListByToken(Collection<OaiPmhListByToken> oaiPmhListByToken) {
        this.oaiPmhListByToken = oaiPmhListByToken;
    }

    public void setOaiPmhList(Collection<OaiPmhList> oaiPmhList) {
        this.oaiPmhList = oaiPmhList;
    }

    private void buildListWithResumptionToken(Node verbNode, Node record, Node metadata) {

        for (OaiPmhListByToken oaiPmhListByToken : oaiPmhListByToken) {

            if (!oaiPmhListByToken.isRecordStatus()) {
                metadataInsert(new ByteArrayInputStream(oaiPmhListByToken.getXmldata().getBytes(StandardCharsets.UTF_8)), metadata);
            }

            buildHeader(oaiPmhListByToken.getUid(), oaiPmhListByToken.getLastModDate(),
                    oaiPmhListByToken.isRecordStatus(), oaiPmhListByToken.getSets(), record, metadata);
            importRecordTpl(verbNode);
        }

        new ResumptionToken(oaiPmhTpl).add(verb, dataSize, resumptionToken, recordsProPage);
    }

    private void buildList(Node verbNode, Node record, Node metadata) {

        for (OaiPmhList oaiPmhList : oaiPmhList) {

            if (!oaiPmhList.isRecordStatus()) {
                metadataInsert(new ByteArrayInputStream(oaiPmhList.getXmldata().getBytes(StandardCharsets.UTF_8)), metadata);
            }

            buildHeader(oaiPmhList.getUid(), oaiPmhList.getLastModDate(), oaiPmhList.isRecordStatus(),
                    oaiPmhList.getSets(), record, metadata);
            importRecordTpl(verbNode);
        }
    }

    private void buildHeader(String uid, Timestamp lastModdate, boolean status, Collection<Set> sets, Node record, Node metadata) {
        Node importHeader = recordTpl.importNode(addHeaderTpl(uid,
                DateTimeConverter.sqlTimestampToString(lastModdate),
                status,
                sets).getDocumentElement(), true);
        record.insertBefore(importHeader, metadata);
    }

    private void metadataInsert(ByteArrayInputStream stream, Node metadata) {
        Document metadataXml = DocumentXmlUtils.document(stream, true);
        Node metadataImport = recordTpl.importNode(metadataXml.getDocumentElement(), true);
        metadata.appendChild(metadataImport);
    }

    private void importRecordTpl(Node verbNode) {
        verbNode.appendChild(oaiPmhTpl.importNode(recordTpl.getDocumentElement(), true));
    }
}
