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
package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.AppErrorHandler;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.services.DisseminationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@RequestMapping("/disseminations")
@RestController
public class DisseminationController {
    private final DisseminationService disseminationService;

    private final Logger logger = LoggerFactory.getLogger(DisseminationController.class);

    @Autowired
    public DisseminationController(DisseminationService disseminationService) {
        this.disseminationService = disseminationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity find(@RequestParam(value = "uid", required = false) String uid,
                               @RequestParam(value = "formatId", required = false) Long formatId) throws JsonProcessingException {
        Collection<Dissemination> disseminations = new ArrayList<>();

        if (uid == null && formatId == null) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR).httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Parameter uid or formatId failed.");
            aeh.log();
            return new ResponseEntity(aeh.message(), aeh.httpStatus());
        }

        if (uid != null && formatId == null) {
            disseminations = disseminationService.findByPropertyAndValue("id_record", uid);
        }

        if (formatId != null && uid == null) {
            disseminations = disseminationService.findByPropertyAndValue("id_format", String.valueOf(formatId));
        }

        if (uid != null && formatId != null) {
            Dissemination dissemination = disseminationService.findByMultipleValues(
                    "id_record = %s AND id_format = %s",
                    uid,
                    String.valueOf(formatId));
            disseminations.add(dissemination);
        }

        return new ResponseEntity<>(disseminations, HttpStatus.OK);
    }

    @GetMapping(value = "/earliest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity findEarliest() throws JsonProcessingException {
        Collection<Dissemination> disseminations = disseminationService.findFirstRowsByProperty(
                "lastmoddate", 1);

        if (disseminations == null) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.WARN).httpStatus(HttpStatus.NOT_FOUND)
                    .message("Cannot found disseminations.");
            aeh.log();
            return new ResponseEntity(aeh.message(), aeh.httpStatus());
        }

        return new ResponseEntity<>(disseminations, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination = null;

        try {
            dissemination = om.readValue(input, Dissemination.class);

            if (dissemination.getRecordId() == null || dissemination.getRecordId().isEmpty()) {
                AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR).httpStatus(HttpStatus.BAD_REQUEST)
                        .message("Dissemination object is invalid, record_id failed.");
                return new ResponseEntity(aeh.message(), aeh.httpStatus());
            }
        } catch (IOException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR).httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Cannot parse JSON input.").exception(e);
            aeh.log();
            return new ResponseEntity(aeh.message(), aeh.httpStatus());
        }

        dissemination = disseminationService.saveDissemination(dissemination);

        return new ResponseEntity<>(dissemination, HttpStatus.OK);
    }

    @PutMapping(value = "{uid}/{mdprefix}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(@RequestBody Dissemination input) throws JsonProcessingException {
        Dissemination dissemination = disseminationService.update(input);

        if (dissemination == null) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.WARN).httpStatus(HttpStatus.NOT_FOUND)
                    .message("Cannot update disseminations.");
            aeh.log();
            return new ResponseEntity(aeh.message(), aeh.httpStatus());
        }

        return new ResponseEntity<>(dissemination, HttpStatus.OK);
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@RequestBody Dissemination input) throws JsonProcessingException {
        disseminationService.delete(input);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
