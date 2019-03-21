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
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class OaiPmhListIdentifiers extends OaiPmhList implements OaiPmhListBuilder {

    public OaiPmhListIdentifiers(Document oaiPmhTemplate) {
        super(oaiPmhTemplate);
    }

    @Override
    public void list() throws NotFound {
        listNode();
        buildIdentifierList();
    }

    private void buildIdentifierList() throws NotFound {

        if (oaiPmhLists == null || oaiPmhLists.isEmpty()) {
            createListHeader(oaiPmhTemplate.getElementsByTagName(verb).item(0),
                    records);
        } else {
            createListHeader(oaiPmhTemplate.getElementsByTagName(verb).item(0),
                    oaiPmhLists);
        }

        if (resumptionToken != null) {
            addResumtionToken(records.size());
        }
    }
}
