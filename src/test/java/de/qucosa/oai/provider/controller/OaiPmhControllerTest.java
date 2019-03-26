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

package de.qucosa.oai.provider.controller;

import de.qucosa.oai.provider.OaiPmhTestApplicationConfig;
import de.qucosa.oai.provider.QucosaOaiProviderApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.xmlunit.matchers.CompareMatcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {QucosaOaiProviderApplication.class, OaiPmhTestApplicationConfig.class})
@AutoConfigureMockMvc
public class OaiPmhControllerTest {
    private Logger logger = LoggerFactory.getLogger(OaiPmhControllerTest.class);

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Load xml by ListIdentifers verb.")
    public void hasListIdentifiersNode() throws Exception {
        MvcResult mvcResult = mvc.perform(
                get("/oai/ListIdentifiers?resumptionToken='c898267ed5a9ad3f656800cf146019822c7ffa33426208d9992f9210fac3a7e9/1'")
                .contentType(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk()).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        assertThat(content, CompareMatcher.isIdenticalTo("<ListIdentifiers>"));
    }
}
