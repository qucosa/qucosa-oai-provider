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
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.ResumptionToken;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.persistence.model.views.OaiPmhLists;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.SetService;
import de.qucosa.oai.provider.services.SetsToRecordService;

import java.io.IOException;
import java.util.Collection;

public interface OaiPmhListBuilder {
    void list() throws IOException, NotFound;

    void setRecords(Collection<Record> records);

    void setDisseminationService(DisseminationService disseminationService);

    void setSetService(SetService<Set> setService);

    void setVerb(String verb);

    void setFormat(Format format);

    void setSetToRecordService(SetsToRecordService setToRecordService);

    void setResumptionToken(ResumptionToken resumptionToken);

    void setRecordsProPage(int recordsProPage);

    void setOaiPmhLists(Collection<OaiPmhLists> oaiPmhLists);
}
