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
import java.util.Objects;

public class GetRecord extends OaiPmhDataBuilderAbstract implements OaiPmhDataBuilder {
    // qucosa:30859
    private Format format;

    private Collection<OaiPmhList> oaiPmhList;

    private String identifier;

    @Override
    public Document oaiXmlData() {

        if (oaiPmhList == null) {
            throw new RuntimeException("Not exists list objects.");
        }

        recordTpl(getClass());
        OaiPmhList record = null;

        if (!oaiPmhList.isEmpty()) {

            for (OaiPmhList entry : oaiPmhList) {

                if (!entry.getUid().equals(identifier)) {
                    continue;
                }

                record = entry;
            }
        }

        Node verbNode = oaiPmhTpl.getElementsByTagName(verb).item(0);
        buildRecord(verbNode, Objects.requireNonNull(record));

        return oaiPmhTpl;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public void setOaiPmhList(Collection<OaiPmhList> oaiPmhList) {
        this.oaiPmhList = oaiPmhList;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    private void buildRecord(Node verbNode, OaiPmhList record) {
        Node recordNode = recordTpl.getElementsByTagName("record").item(0);
        Node metadata = recordTpl.getElementsByTagName("metadata").item(0);

        if (!record.isRecordStatus()) {
            Document metadataXml = DocumentXmlUtils.document(
                    new ByteArrayInputStream(record.getXmldata().getBytes(StandardCharsets.UTF_8)), true);
            Node metadataImport = recordTpl.importNode(metadataXml.getDocumentElement(), true);
            metadata.appendChild(metadataImport);
        }

        Node importHeader = recordTpl.importNode(addHeaderTpl(record.getUid(),
                DateTimeConverter.sqlTimestampToString(record.getLastModDate()), record.isRecordStatus(), record.getSets()).getDocumentElement(), true);
        recordNode.insertBefore(importHeader, metadata);

        Node importTpl = oaiPmhTpl.importNode(recordTpl.getDocumentElement(), true);
        verbNode.appendChild(importTpl);
    }
}
