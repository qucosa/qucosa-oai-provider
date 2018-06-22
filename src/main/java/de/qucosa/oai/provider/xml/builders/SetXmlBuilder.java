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

package de.qucosa.oai.provider.xml.builders;

import de.qucosa.oai.provider.application.config.SetConfigMapper;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;
import org.w3c.dom.Document;

public class SetXmlBuilder {

    public static Document build(SetConfigMapper.Set set) {
        Document setXml = DocumentXmlUtils.document(SetXmlBuilder.class.getResourceAsStream("/set.xml"), true);
        setXml.getElementsByTagName("setSpec").item(0).appendChild(setXml.createTextNode(set.getSetSpec()));
        setXml.getElementsByTagName("setName").item(0).appendChild(setXml.createTextNode(set.getSetName()));
        return setXml;
    }
}
