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
import de.qucosa.oai.provider.persistence.model.views.OaiPmhList;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhListByToken;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Collection;

public class ListIdentifiers extends OaiPmhDataBuilderAbstract implements OaiPmhDataBuilder {

    private Collection<OaiPmhListByToken> oaiPmhListByToken;

    private Collection<OaiPmhList> oaiPmhList;

    @Override
    public Document oaiXmlData() {

        if (oaiPmhList == null && oaiPmhListByToken == null) {
            throw new RuntimeException("Not exists list objects.");
        }

        Node verbNode = oaiPmhTpl.getElementsByTagName(verb).item(0);

        if (oaiPmhListByToken != null) {
            buildListWithResumptionToken(verbNode);
        }

        if (oaiPmhList != null) {
           buildList(verbNode);
        }

        return oaiPmhTpl;
    }

    public void setOaiPmhListByToken(Collection<OaiPmhListByToken> oaiPmhListByToken) {
        this.oaiPmhListByToken = oaiPmhListByToken;
    }

    public void setOaiPmhList(Collection<OaiPmhList> oaiPmhList) {
        this.oaiPmhList = oaiPmhList;
    }

    private void buildListWithResumptionToken(Node verbNode) {

        for (OaiPmhListByToken obj : oaiPmhListByToken) {
            Node importedHeader = oaiPmhTpl.importNode(addHeaderTpl(obj.getUid(),
                    DateTimeConverter.sqlTimestampToString(obj.getLastModDate()), obj.isRecordStatus(), obj.getSets()).getDocumentElement(), true);
            verbNode.appendChild(importedHeader);
        }

        new ResumptionToken(oaiPmhTpl).add(verb, dataSize, resumptionToken, recordsProPage);
    }

    private void buildList(Node verbNode) {

        for (OaiPmhList obj : oaiPmhList) {
            Node importedHeader = oaiPmhTpl.importNode(addHeaderTpl(obj.getUid(),
                    DateTimeConverter.sqlTimestampToString(obj.getLastModDate()), obj.isRecordStatus(), obj.getSets()).getDocumentElement(), true);
            verbNode.appendChild(importedHeader);
        }
    }
}
