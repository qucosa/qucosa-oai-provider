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

package de.qucosa.oai.provider.api.validators.xml;

import java.util.HashMap;
import java.util.Map;

public class ValidateXsdSchemas {
    public final static Map<String, Boolean> validateSchemas = new HashMap<String, Boolean>() {
        {
            put("http://www.loc.gov/standards/mets/mets.xsd", true);
            put("http://www.openarchives.org/OAI/2.0/oai_dc.xsd", false);
            put("http://www.loc.gov/standards/mods/v3/mods.xsd", true);
            put("http://dublincore.org/schemas/xmls/qdc/2008/02/11/dc.xsd", false);
            put("http://files.dnb.de/standards/xmetadissplus/xmetadissplus.xsd", false);
            put("http://files.dnb.de/standards/xmetadiss/pc.xsd", false);
            put("http://files.dnb.de/standards/xmetadiss/ddb.xsd", false);
            put("http://dublincore.org/schemas/xmls/qdc/2008/02/11/dcterms.xsd", false);
            put("http://files.dnb.de/standards/xmetadiss/subject.xsd", false);
            put("http://files.dnb.de/standards/xmetadiss/cc.xsd", false);
            put("http://files.dnb.de/standards/xmetadiss/thesis.xsd", false);
            put("https://www.w3.org/2009/XMLSchema/XMLSchema.xsd", false);
            put("http://files.dnb.de/standards/xmetadissplus/xmetadissplustype.xsd", false);
            put("http://files.dnb.de/standards/xmetadiss/urn.xsd", false);
        }
    };
}
