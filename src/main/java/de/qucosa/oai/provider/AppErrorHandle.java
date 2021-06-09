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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AppErrorHandle {
    private Logger logger;

    private Exception exception;

    private String message;

    private Level level;

    private HttpStatus httpStatus;

    public AppErrorHandle logger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public AppErrorHandle exception(Exception exception) {
        this.exception = exception;
        return this;
    }

    public AppErrorHandle message(String message) {
        this.message = message;
        return this;
    }

    public AppErrorHandle level(Level level) {
        this.level = level;
        return this;
    }

    public AppErrorHandle httpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public ResponseEntity httpResponse() {
        return new ResponseEntity(message, httpStatus);
    }

    public void logError() {
        ErrorDetails errorDetails = new ErrorDetails(message, level, httpStatus);

        try {
            logger.error(errorDetails.errorDetails());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @JsonAutoDetect
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorDetails {
        @JsonProperty("exception")
        private Exception exception;

        @JsonProperty("message")
        private String message;

        @JsonProperty("level")
        private Level level;

        @JsonProperty("httpStatus")
        private HttpStatus httpStatus;

        public ErrorDetails(String message, Level level, HttpStatus httpStatus) {
            this.message = message;
            this.level = level;
            this.httpStatus = httpStatus;
        }

        public ErrorDetails(Exception exception, String message, Level level, HttpStatus httpStatus) {
            this.exception = exception;
            this.message = message;
            this.level = level;
            this.httpStatus = httpStatus;
        }

        public String errorDetails() throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        }
    }
}
