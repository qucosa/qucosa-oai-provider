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

import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.persistence.model.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Collection;

public class ListSets extends OaiPmhDataBuilderAbstract implements OaiPmhDataBuilder {
    private Collection<Set> sets;

    @Override
    public Document oaiXmlData() {

        if (sets == null || sets.size() == 0) {
            throw new RuntimeException("Not exists sets objects.");
        }

//        Node verbNode = oaiPmhTpl.getElementsByTagName(verb).item(0);
        buildSets();

        return oaiPmhTpl;
    }

    public void setSets(Collection<Set> sets) {
        this.sets = sets;
    }

    private void buildSets() {

        for (Set set : sets) {
            Document setTpl = DocumentXmlUtils.document(getClass().getResourceAsStream("/templates/set.xml"), true);
            Node setSpec = setTpl.getElementsByTagName("setSpec").item(0);
            setSpec.setTextContent(set.getSetSpec());
            Node setName = setTpl.getElementsByTagName("setName").item(0);
            setName.setTextContent(set.getSetName());

            Node importedNode = oaiPmhTpl.importNode(setTpl.getDocumentElement(), true);
            oaiPmhTpl.getElementsByTagName(verb).item(0).appendChild(importedNode);
        }
    }
}
