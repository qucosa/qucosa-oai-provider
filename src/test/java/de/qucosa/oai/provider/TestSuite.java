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

package de.qucosa.oai.provider;

import de.qucosa.oai.provider.controller.FormatControllerIT;
import de.qucosa.oai.provider.controller.OaiPmhControllerGetRecordIT;
import de.qucosa.oai.provider.controller.OaiPmhControllerIdentifyIT;
import de.qucosa.oai.provider.controller.OaiPmhControllerListIdentifiersIT;
import de.qucosa.oai.provider.controller.OaiPmhControllerListMetadataFormatsIT;
import de.qucosa.oai.provider.controller.OaiPmhControllerListRecordsIT;
import de.qucosa.oai.provider.controller.OaiPmhControllerListSetsIT;
import de.qucosa.oai.provider.controller.RecordControllerIT;
import de.qucosa.oai.provider.controller.SetControllerIT;
import de.qucosa.oai.provider.database.InstallTablesIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@SuppressWarnings("JUnit5Platform")
@RunWith(JUnitPlatform.class)
@DisplayName("Embedded postgres and spring controller test suite.")
@SelectClasses({InstallTablesIT.class,
        FormatControllerIT.class,
        SetControllerIT.class,
        RecordControllerIT.class,
        OaiPmhControllerListIdentifiersIT.class,
        OaiPmhControllerListRecordsIT.class,
        OaiPmhControllerGetRecordIT.class,
        OaiPmhControllerListSetsIT.class,
        OaiPmhControllerListMetadataFormatsIT.class,
        OaiPmhControllerIdentifyIT.class})
public class TestSuite {

}
