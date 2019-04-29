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

package de.qucosa.oai.provider;

import de.qucosa.oai.provider.controller.FormatControllerTest;
import de.qucosa.oai.provider.controller.OaiPmhControllerGetRecordTest;
import de.qucosa.oai.provider.controller.OaiPmhControllerIdentifyTest;
import de.qucosa.oai.provider.controller.OaiPmhControllerListIdentifiersTest;
import de.qucosa.oai.provider.controller.OaiPmhControllerListMetadataFormatsTest;
import de.qucosa.oai.provider.controller.OaiPmhControllerListRecordsTest;
import de.qucosa.oai.provider.controller.OaiPmhControllerListSetsTest;
import de.qucosa.oai.provider.controller.RecordControllerTest;
import de.qucosa.oai.provider.controller.SetControllerTest;
import de.qucosa.oai.provider.database.InstallTablesTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@SuppressWarnings("JUnit5Platform")
@RunWith(JUnitPlatform.class)
@DisplayName("Embedded postgres and spring controller test suite.")
@SelectClasses({InstallTablesTest.class, FormatControllerTest.class, SetControllerTest.class,
        RecordControllerTest.class,
        OaiPmhControllerListIdentifiersTest.class,
        OaiPmhControllerListRecordsTest.class,
        OaiPmhControllerGetRecordTest.class,
        OaiPmhControllerListSetsTest.class,
        OaiPmhControllerListMetadataFormatsTest.class,
        OaiPmhControllerIdentifyTest.class})
public class TestSuite {

}
