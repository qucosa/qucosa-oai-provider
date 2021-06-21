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

import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import static org.slf4j.event.Level.ERROR;
import static org.slf4j.event.Level.WARN;

public class AppErrorHandler {
    private Logger logger;

    private Exception exception;

    private String message;

    private Level level;

    private HttpStatus httpStatus;

    public AppErrorHandler(Logger logger) {
        this.logger = logger;
    }

    @Deprecated
    public AppErrorHandler logger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public AppErrorHandler exception(Exception exception) {
        this.exception = exception;
        return this;
    }

    public AppErrorHandler message(String message) {
        this.message = message;
        return this;
    }

    public String message() {
        return message;
    }

    public AppErrorHandler level(Level level) {
        this.level = level;
        return this;
    }

    public AppErrorHandler httpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public void log() {
        if (level.equals(ERROR)) {
            logger.error(message);
        } else if (level.equals(WARN)) {
            logger.warn(message);
        }
        if (exception != null) logger.debug("Exception thrown", exception);
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
