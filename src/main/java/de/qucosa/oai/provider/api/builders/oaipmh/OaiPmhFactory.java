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

import de.qucosa.oai.provider.api.utils.DocumentXmlUtils;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.ResumptionToken;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhLists;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.SetService;
import de.qucosa.oai.provider.services.SetsToRecordService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class OaiPmhFactory {
    private Document oaiPmhTemplate;

    private OaiPmhListBuilder listBuilder;

    public OaiPmhFactory(String path) throws FileNotFoundException {
        this(new File(path));
    }

    public OaiPmhFactory(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public OaiPmhFactory(InputStream inputStream) {
        oaiPmhTemplate = DocumentXmlUtils.document(inputStream, true);
    }

    public Document createList(String verb, Format format, Collection<Record> records,
                               DisseminationService disseminationService,
                               SetService<Set> setService,
                               SetsToRecordService setsToRecordService) throws IOException, NotFound {
        buildList(verb, format, records, disseminationService, setService, setsToRecordService, null, 0, null);
        setOaiPmhAttributes(verb, format);

        listBuilder.list();
        return oaiPmhTemplate;
    }

    public Document createList(String verb, Format format, Collection<Record> records,
                               DisseminationService disseminationService, SetService<Set> setService,
                               SetsToRecordService setsToRecordService, ResumptionToken resumptionToken,
                               int recordsProPage, Collection<OaiPmhLists> oaiPmhLists) throws IOException, NotFound {
        buildList(verb, format, records, disseminationService, setService, setsToRecordService, resumptionToken,
                recordsProPage, oaiPmhLists);
        setOaiPmhAttributes(verb, format);

        listBuilder.list();
        return oaiPmhTemplate;
    }

    protected void buildList(String verb, Format format, Collection<Record> records,
                             DisseminationService disseminationService, SetService<Set> setService,
                             SetsToRecordService setsToRecordService, ResumptionToken resumptionToken,
                             int recordsProPage, Collection<OaiPmhLists> oaiPmhLists) {
        switch (verb) {
            case "ListIdentifiers":
                listBuilder = new OaiPmhListIdentifiers(oaiPmhTemplate);
                break;
            case "ListRecords":
                listBuilder = new OaiPmhListRecords(oaiPmhTemplate);
                break;
        }

        listBuilder.setVerb(verb);
        listBuilder.setFormat(format);
        listBuilder.setRecords(records);
        listBuilder.setDisseminationService(disseminationService);
        listBuilder.setSetService(setService);
        listBuilder.setSetToRecordService(setsToRecordService);
        listBuilder.setResumptionToken(resumptionToken);
        listBuilder.setRecordsProPage(recordsProPage);
        listBuilder.setOaiPmhLists(oaiPmhLists);
    }

    private void setOaiPmhAttributes(String verb, Format format) {
        Node element = oaiPmhTemplate.getElementsByTagName("request").item(0);
        element.getAttributes().getNamedItem("metadataPrefix").setNodeValue(format.getMdprefix());
        element.getAttributes().getNamedItem("verb").setNodeValue(verb);
    }
}
