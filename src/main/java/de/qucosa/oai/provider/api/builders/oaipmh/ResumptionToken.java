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


import de.qucosa.oai.provider.api.exceptions.XmlDomParserException;
import de.qucosa.oai.provider.api.utils.DateTimeConverter;
import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ResumptionToken {
    private final Document oaiPmhTpl;

    public ResumptionToken(Document oaiPmhTpl) {
        this.oaiPmhTpl = oaiPmhTpl;
    }

    @SuppressWarnings("UnusedReturnValue")
    public ResumptionToken add(String verb, int dataSize, de.qucosa.oai.provider.persistence.model.ResumptionToken resumptionToken, int recordsProPage) throws XmlDomParserException {
        Node nodeByVerb = oaiPmhTpl.getElementsByTagName(verb).item(0);
        Document resumptionTokenDoc = DocumentXmlUtils.document(
                getClass().getResourceAsStream("/templates/resumption-token.xml"), true);
        Node resumptionTokenElem = resumptionTokenDoc.getDocumentElement();

        if ((dataSize - Integer.parseInt(String.valueOf(resumptionToken.getCursor()))) >= recordsProPage) {
            String[] token = resumptionToken.getTokenId().split("/");
            int count = Integer.valueOf(token[1]);
            int page = (count + 1);
            String tokenID = token[0] + "/" + String.valueOf(page);

            resumptionTokenElem.setTextContent(tokenID);
        }

        resumptionTokenElem.getAttributes().getNamedItem("cursor").setNodeValue(String.valueOf(resumptionToken.getCursor()));
        resumptionTokenElem.getAttributes().getNamedItem("expirationDate").setNodeValue(
                DateTimeConverter.sqlTimestampToString(resumptionToken.getExpirationDate())
        );
        resumptionTokenElem.getAttributes().getNamedItem("completeListSize").setNodeValue(
                String.valueOf(dataSize)
        );

        nodeByVerb.appendChild(oaiPmhTpl.importNode(resumptionTokenDoc.getDocumentElement(), true));

        return this;
    }
}
