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

import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhListByToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

public class OaiPmhListRecords extends OaiPmhList implements OaiPmhListBuilder {

    public OaiPmhListRecords(Document oaiPmhTemplate) {
        super(oaiPmhTemplate);
    }

    @Override
    public void list() throws NotFound, UnsupportedEncodingException {
        listNode();

        if (oaiPmhLists == null || oaiPmhLists.isEmpty()) {
            addList(records);
        } else {
            addList(oaiPmhLists);
        }
    }

    private void addList(Collection collection) throws NotFound, UnsupportedEncodingException {

        for (Object object : collection) {
            Document entry = DocumentXmlUtils.document(
                    getClass().getResourceAsStream("/templates/record.xml"), true);
            Node metadata = entry.getElementsByTagName("metadata").item(0);
            InputStream is = null;

            if (object instanceof OaiPmhListByToken) {

                if (!((OaiPmhListByToken) object).isRecordStatus()) {
                    is = new ByteArrayInputStream(dissemination(
                            "id_format = %s AND id_record = %s", String.valueOf(format.getIdentifier()),
                            ((OaiPmhListByToken) object).getUid()).getXmldata().getBytes("UTF-8"));
                }
            } else if (object instanceof Record) {

                if (!((Record) object).isDeleted()) {
                    is = new ByteArrayInputStream(dissemination(
                            "id_format = %s AND id_record = %s", String.valueOf(format.getIdentifier()),
                            ((Record) object).getUid()).getXmldata().getBytes("UTF-8"));
                }
            }

            if (is != null) {
                Document verbXml = DocumentXmlUtils.document(is, true);
                Node importedMetadata = entry.importNode(verbXml.getDocumentElement(), true);
                metadata.appendChild(importedMetadata);
            }

            Node recordNode = entry.getElementsByTagName("record").item(0);
            Node importedHeader = entry.importNode(addHeaderTpl(object).getDocumentElement(), true);
            recordNode.insertBefore(importedHeader, metadata);

            Element verbElem = (Element) oaiPmhTemplate.getElementsByTagName(verb).item(0);
            Node imported = oaiPmhTemplate.importNode(entry.getDocumentElement(), true);
            verbElem.appendChild(imported);
        }

        if (resumptionToken != null) {
            addResumtionToken(records.size());
        }
    }
}
