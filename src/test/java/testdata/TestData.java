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
package testdata;

public class TestData {

    public static final String SETS = "[{\"setspec\": \"ddc:1200\", \"setname\": \"Test Set 1200\", \"setdescription\" : \"\"}," +
            "{\"setspec\": \"ddc:1201\", \"setname\": \"Test Set 1201\", \"setdescription\" : \"\"}]";

    public static final String FORMATS = "[{\"mdprefix\" : \"oai_dc\", \"schemaurl\" : \"http://www.openarchives.org/OAI/2.0/oai_dc/\", \"namespace\" : \"oai_dc\", \"deleted\" : \"false\"}," +
            "{\"mdprefix\" : \"xmetadiss\", \"schemaurl\" : \"http://www.d-nb.de/standards/xmetadissplus/\", \"namespace\" : \"xmetadiss\", \"deleted\" : \"false\"}," +
            "{\"mdprefix\" : \"epicur\", \"schemaurl\" : \"http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd\", \"namespace\" : \"epicur\", \"deleted\" : \"false\"}]";

    public static final String DISSEMINATIONS = "[{\"recordid\" : \"qucosa:32394\", \"formatid\" : 17, \"lastmoddate\" : 1513255949071, \"xmldata\" : \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-16\\\"?><oai_dc:dc xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\" xmlns:mets=\\\"http://www.loc.gov/METS/\\\" xmlns:mods=\\\"http://www.loc.gov/mods/v3\\\" xmlns:myfunc=\\\"urn:de:qucosa:dc\\\" xmlns:oai_dc=\\\"http://www.openarchives.org/OAI/2.0/oai_dc/\\\" xmlns:slub=\\\"http://slub-dresden.de/\\\" xmlns:xs=\\\"http://www.w3.org/2001/XMLSchema\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"></oai_dc:dc>\"}, " +
            "{\"recordid\" : \"qucosa:32394\", \"formatid\" : 22, \"lastmoddate\" : 1513255949071, \"xmldata\" : \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-16\\\"?>\\n<xMetaDiss:xMetaDiss xmlns:cc=\\\"http://www.d-nb.de/standards/cc/\\\" xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\" xmlns:dcterms=\\\"http://purl.org/dc/terms/\\\" xmlns:ddb=\\\"http://www.d-nb.de/standards/ddb/\\\" xmlns:dini=\\\"http://www.d-nb.de/standards/xmetadissplus/type/\\\" xmlns:mets=\\\"http://www.loc.gov/METS/\\\" xmlns:mods=\\\"http://www.loc.gov/mods/v3\\\" xmlns:myfunc=\\\"urn:de:qucosa:xmetadissplus\\\" xmlns:pc=\\\"http://www.d-nb.de/standards/pc/\\\" xmlns:slub=\\\"http://slub-dresden.de/\\\" xmlns:subject=\\\"http://www.d.nb.de/standards/subject/\\\" xmlns:thesis=\\\"http://www.ndltd.org/standards/metadata/etdms/1.0/\\\" xmlns:urn=\\\"http://www.d-nb.de/standards/urn/\\\" xmlns:xMetaDiss=\\\"http://www.d-nb.de/standards/xmetadissplus/\\\" xmlns:xs=\\\"http://www.w3.org/2001/XMLSchema\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\"></xMetaDiss:xMetaDiss>\"}, " +
            "{\"recordid\" : \"qucosa:32394\", \"formatid\" : 1, \"lastmoddate\" : 1513255949071, \"xmldata\" : \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-16\\\"?><epicur xsi:schemaLocation=\\\"urn:nbn:de:1111-2004033116 http://www.persistent-identifier.de/xepicur/version1.0/xepicur.xsd\\\"><administrative_data><delivery><update_status type=\\\"urn_new\\\"/></delivery></administrative_data><record><identifier scheme=\\\"urn:nbn:de\\\">urn:nbn:de:bsz:14-qucosa-32394</identifier><resource><identifier scheme=\\\"url\\\" type=\\\"frontpage\\\" role=\\\"primary\\\" origin=\\\"original\\\">http://test.tud.qucosa.de/id/qucosa%3A32394</identifier><format scheme=\\\"imt\\\">text/html</format></resource></record></epicur>\"}]";
}
