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

public abstract class OaiPmhList {
    protected String verb;

    protected Format format;

    protected Document oaiPmhTemplate;

    public OaiPmhList(Document oaiPmhTemplate) {
        this.oaiPmhTemplate = oaiPmhTemplate;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    protected Node listNode() {
        Document document = DocumentXmlUtils.document(null, true);
        Node node = document.createElement(verb);
        return node;
    }
}
