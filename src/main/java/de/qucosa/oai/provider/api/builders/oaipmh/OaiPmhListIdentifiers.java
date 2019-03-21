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

import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Collection;

public class OaiPmhListIdentifiers extends OaiPmhList implements OaiPmhListBuilder {

    public OaiPmhListIdentifiers(Document oaiPmhTemplate) {
        super(oaiPmhTemplate);
    }

    @Override
    public void list() throws NotFound {
        listNode();

        if (oaiPmhLists == null || oaiPmhLists.isEmpty()) {
            addList(records);
        } else {
            addList(oaiPmhLists);
        }
    }

    private void addList(Collection collection) throws NotFound {
        Node verbNode = oaiPmhTemplate.getElementsByTagName(verb).item(0);

        for (Object object : collection) {
            Document headerTpl = addHeaderTpl(object);
            Node imported = oaiPmhTemplate.importNode(headerTpl.getDocumentElement(), true);
            verbNode.appendChild(imported);
        }

        if (resumptionToken != null) {
            addResumtionToken(records.size());
        }
    }
}
