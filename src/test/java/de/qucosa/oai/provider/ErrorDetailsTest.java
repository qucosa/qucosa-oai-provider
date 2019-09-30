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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ErrorDetailsTest {
    @Autowired
    private JacksonTester<ErrorDetails> jacksonTester;

    @Test
    public void testSerialize() throws Exception {
        ErrorDetails errorDetails = new ErrorDetails(
                "myclass",
                "methodname",
                ":/path",
                HttpStatus.NOT_FOUND,
                "Everything wrong",
                new Exception("root cause"));

        JsonContent<ErrorDetails> json = jacksonTester.write(errorDetails);

        assertThat(json)
                .extractingJsonPathStringValue("@.httpStatus")
                .contains(HttpStatus.NOT_FOUND.name());
    }
}
