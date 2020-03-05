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

package de.qucosa.oai.error;

import de.qucosa.oai.provider.api.OaiError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.w3c.dom.Document;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ErrorSpecTest {

    private OaiError error;

    @BeforeAll
    public void init() {
        error = new OaiError("badArgument");
    }

    @Test
    @DisplayName("Test if error code is illegal.")
    public void errorCodeNotExists() {
        Assertions.assertThrows(RuntimeException.class, () -> new OaiError("bdArgument"));
    }

    @Test
    @DisplayName("Test if all informations in oai error object.")
    public void getOaiErrorObj() {
        Assertions.assertEquals("badArgument", error.getErrorCode());
        Assertions.assertNotNull(error.getErrorMsg());
    }

    @Test
    @DisplayName("Test if document after error instance not null.")
    public void isDocumentNotNull() {
        Assertions.assertNotNull(error.getOaiErrorXml());
    }

    @Test
    @DisplayName("Has error xml object all nodes for oai error spec.")
    public void hasErrorAllNodes() {
        Document errorXml = error.getOaiErrorXml();

        String errCode = errorXml.getElementsByTagName("error").item(0)
                .getAttributes().getNamedItem("code").getTextContent();
        String errMsg = errorXml.getElementsByTagName("error").item(0).getTextContent();
        String request = errorXml.getElementsByTagName("request").item(0).getTextContent();
        String respnoseDate = errorXml.getElementsByTagName("responseDate").item(0).getTextContent();

        Assertions.assertEquals("badArgument", errCode);
        Assertions.assertNotEquals("", errMsg);
        Assertions.assertNotEquals("", request);
        Assertions.assertNotEquals("", respnoseDate);
        Assertions.assertTrue(respnoseDate.contains("T"));
        Assertions.assertTrue(respnoseDate.contains("Z"));
    }
}
