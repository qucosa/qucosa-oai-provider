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
import de.qucosa.oai.provider.persistence.model.Format;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Collection;

public class ListMetadataFormats extends OaiPmhDataBuilderAbstract implements OaiPmhDataBuilder {
    private Collection<Format> formats;

    @Override
    public Document oaiXmlData() {

        if (formats == null || formats.size() == 0) {
            throw new RuntimeException("Not exists formats objects.");
        }

        buildFormatsList();

        return oaiPmhTpl;
    }

    public void setFormats(Collection<Format> formats) {
        this.formats = formats;
    }

    private void buildFormatsList() {

        for (Format format : formats) {
            Document formatTpl = DocumentXmlUtils.document(getClass().getResourceAsStream("/templates/format.xml"), true);
            Node metadataPrefix = formatTpl.getElementsByTagName("metadataPrefix").item(0);
            metadataPrefix.setTextContent(format.getMdprefix());
            Node schema = formatTpl.getElementsByTagName("schema").item(0);
            schema.setTextContent(format.getSchemaUrl());
            Node metadataNamespace = formatTpl.getElementsByTagName("metadataNamespace").item(0);
            metadataNamespace.setTextContent(format.getNamespace());

            Node importedNode = oaiPmhTpl.importNode(formatTpl.getDocumentElement(), true);
            oaiPmhTpl.getElementsByTagName(verb).item(0).appendChild(importedNode);
        }
    }
}
