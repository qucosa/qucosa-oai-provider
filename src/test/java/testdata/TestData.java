/**
 ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 */
package testdata;

public class TestData {

    public static String SETS = "[{\"setspec\": \"ddc:1200\", \"setname\": \"Test Set 1200\", \"setdescription\" : \"\"}," +
            "{\"setspec\": \"ddc:1201\", \"setname\": \"Test Set 1201\", \"setdescription\" : \"\"}]";

    public static String FORMATS = "[{\"formatId\" : 1, \"mdprefix\" : \"oai_dc\", \"schemaurl\" : \"http://www.openarchives.org/OAI/2.0/oai_dc/\", \"namespace\" : \"oai_dc\", \"deleted\" : \"false\"}," +
            "{\"formatId\" : 2, \"mdprefix\" : \"xmetadiss\", \"schemaurl\" : \"http://www.d-nb.de/standards/xmetadissplus/\", \"namespace\" : \"xmetadiss\", \"deleted\" : \"false\"}," +
            "{\"formatId\" : 3, \"mdprefix\" : \"epicur\", \"schemaurl\" : \"http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd\", \"namespace\" : \"epicur\", \"deleted\" : \"false\"}]";

    public static String RECORDS = "[{\"pid\" : \"qucosa:55887\", \"uid\" : \"oai:example:org:qucosa:55887\"}, " +
            "{\"pid\" : \"qucosa:57777\", \"uid\" : \"oai:example:org:qucosa:57777\"}," +
            "{\"pid\" : \"qucosa:55666\", \"uid\" : \"oai:example:org:qucosa:55666\"}]";

    public static String RECORDS_INPUT = "[\n" +
            "  {\n" +
            "    \"record\" : {\n" +
            "      \"pid\" : \"qucosa:55887\",\n" +
            "      \"uid\" : \"oai:example:org:qucosa:55887\"\n" +
            "    },\n" +
            "    \"format\" : {\n" +
            "      \"mdprefix\" : \"oai_dc\",\n" +
            "      \"schemaurl\" : \"http://www.openarchives.org/OAI/2.0/oai_dc/\",\n" +
            "      \"namespace\" : \"oai_dc\"\n" +
            "    },\n" +
            "    \"dissemination\" : {\n" +
            "      \"lastmoddate\" : 1513255949071,\n" +
            "      \"xmldata\" : \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-16\\\"?><oai_dc:dc xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\" xmlns:mets=\\\"http://www.loc.gov/METS/\\\" xmlns:mods=\\\"http://www.loc.gov/mods/v3\\\" xmlns:myfunc=\\\"urn:de:qucosa:dc\\\" xmlns:oai_dc=\\\"http://www.openarchives.org/OAI/2.0/oai_dc/\\\" xmlns:slub=\\\"http://slub-dresden.de/\\\" xmlns:xs=\\\"http://www.w3.org/2001/XMLSchema\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"></oai_dc:dc>\"\n" +
            "    },\n" +
            "    \"sets\" : [\n" +
            "      {\n" +
            "        \"setspec\": \"ddc:980\",\n" +
            "        \"setname\": \"General history of South America\",\n" +
            "        \"setdescription\" : \"\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"setspec\": \"ddc:990\",\n" +
            "        \"setname\": \"General history of other areas\",\n" +
            "        \"setdescription\" : \"\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"record\" : {\n" +
            "      \"pid\" : \"qucosa:55887\",\n" +
            "      \"uid\" : \"oai:example:org:qucosa:55887\"\n" +
            "    },\n" +
            "    \"format\" : {\n" +
            "      \"mdprefix\" : \"xmetadiss\",\n" +
            "      \"schemaurl\" : \"http://www.d-nb.de/standards/xmetadissplus/\",\n" +
            "      \"namespace\" : \"xmetadiss\"\n" +
            "    },\n" +
            "    \"dissemination\" : {\n" +
            "      \"lastmoddate\" : 1513255949071,\n" +
            "      \"xmldata\" : \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-16\\\"?>\\n<xMetaDiss:xMetaDiss xmlns:cc=\\\"http://www.d-nb.de/standards/cc/\\\" xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\" xmlns:dcterms=\\\"http://purl.org/dc/terms/\\\" xmlns:ddb=\\\"http://www.d-nb.de/standards/ddb/\\\" xmlns:dini=\\\"http://www.d-nb.de/standards/xmetadissplus/type/\\\" xmlns:mets=\\\"http://www.loc.gov/METS/\\\" xmlns:mods=\\\"http://www.loc.gov/mods/v3\\\" xmlns:myfunc=\\\"urn:de:qucosa:xmetadissplus\\\" xmlns:pc=\\\"http://www.d-nb.de/standards/pc/\\\" xmlns:slub=\\\"http://slub-dresden.de/\\\" xmlns:subject=\\\"http://www.d.nb.de/standards/subject/\\\" xmlns:thesis=\\\"http://www.ndltd.org/standards/metadata/etdms/1.0/\\\" xmlns:urn=\\\"http://www.d-nb.de/standards/urn/\\\" xmlns:xMetaDiss=\\\"http://www.d-nb.de/standards/xmetadissplus/\\\" xmlns:xs=\\\"http://www.w3.org/2001/XMLSchema\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"></xMetaDiss:xMetaDiss>\"\n" +
            "    },\n" +
            "    \"sets\" : [\n" +
            "      {\n" +
            "        \"setspec\": \"ddc:980\",\n" +
            "        \"setname\": \"General history of South America\",\n" +
            "        \"setdescription\" : \"\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"setspec\": \"ddc:990\",\n" +
            "        \"setname\": \"General history of other areas\",\n" +
            "        \"setdescription\" : \"\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "]";

    public static String DISSEMINATIONS = "[{\"recordid\" : \"oai:example:org:qucosa:55887\", \"formatid\" : 1, \"lastmoddate\" : 1513255949071, \"xmldata\" : \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-16\\\"?><oai_dc:dc xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\" xmlns:mets=\\\"http://www.loc.gov/METS/\\\" xmlns:mods=\\\"http://www.loc.gov/mods/v3\\\" xmlns:myfunc=\\\"urn:de:qucosa:dc\\\" xmlns:oai_dc=\\\"http://www.openarchives.org/OAI/2.0/oai_dc/\\\" xmlns:slub=\\\"http://slub-dresden.de/\\\" xmlns:xs=\\\"http://www.w3.org/2001/XMLSchema\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"></oai_dc:dc>\"}, " +
            "{\"recordid\" : \"oai:example:org:qucosa:55887\", \"formatid\" : 2, \"lastmoddate\" : 1513255949071, \"xmldata\" : \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-16\\\"?>\\n<xMetaDiss:xMetaDiss xmlns:cc=\\\"http://www.d-nb.de/standards/cc/\\\" xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\" xmlns:dcterms=\\\"http://purl.org/dc/terms/\\\" xmlns:ddb=\\\"http://www.d-nb.de/standards/ddb/\\\" xmlns:dini=\\\"http://www.d-nb.de/standards/xmetadissplus/type/\\\" xmlns:mets=\\\"http://www.loc.gov/METS/\\\" xmlns:mods=\\\"http://www.loc.gov/mods/v3\\\" xmlns:myfunc=\\\"urn:de:qucosa:xmetadissplus\\\" xmlns:pc=\\\"http://www.d-nb.de/standards/pc/\\\" xmlns:slub=\\\"http://slub-dresden.de/\\\" xmlns:subject=\\\"http://www.d.nb.de/standards/subject/\\\" xmlns:thesis=\\\"http://www.ndltd.org/standards/metadata/etdms/1.0/\\\" xmlns:urn=\\\"http://www.d-nb.de/standards/urn/\\\" xmlns:xMetaDiss=\\\"http://www.d-nb.de/standards/xmetadissplus/\\\" xmlns:xs=\\\"http://www.w3.org/2001/XMLSchema\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"></xMetaDiss:xMetaDiss>\"}]";
}
