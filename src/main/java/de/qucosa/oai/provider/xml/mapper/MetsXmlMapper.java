/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
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

package de.qucosa.oai.provider.xml.mapper;

import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.Map;

public class MetsXmlMapper {
    private Document metsDoc = null;

    private XPath xPath = null;

    public MetsXmlMapper(Document metsDoc, Map<String, String> namespaces) {
        this.metsDoc = metsDoc;
        xPath = DocumentXmlUtils.xpath(namespaces);
    }

    public String pid() throws XPathExpressionException {
        String pid = (String) xPath.compile("//mets:mets/@OBJID").evaluate(metsDoc, XPathConstants.STRING);
        return pid;
    }

    public String lastModDate() throws XPathExpressionException {
        String lastModDate = (String) xPath.compile("//mets:mets/mets:metsHdr/@LASTMODDATE").evaluate(metsDoc, XPathConstants.STRING);
        return lastModDate;
    }

    public String agentName() throws XPathExpressionException {
        String agent = (String) xPath.compile("//mets:agent[@ROLE='EDITOR' and @TYPE='ORGANIZATION']/mets:name[1]").evaluate(metsDoc, XPathConstants.STRING);
        return agent;
    }
}
