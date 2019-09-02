/*
 ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 */
package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.ErrorDetails;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.services.DisseminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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


    @Autowired
    public DisseminationController(DisseminationService disseminationService) {
        this.disseminationService = disseminationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity find(@RequestParam(value = "uid", required = false) String uid,
                               @RequestParam(value = "formatId", required = false) Long formatId) {
        Collection<Dissemination> disseminations = new ArrayList<>();

        if (uid == null && formatId == null) {
            return new ErrorDetails(this.getClass().getName(), "find", "GET:disseminations",
                    HttpStatus.BAD_REQUEST, "Parameter uid or formatId failed.", null).response();
        }

        try {

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
        } catch (NotFound e) {
            return new ErrorDetails(this.getClass().getName(), "find", "GET:disseminations" + uid,
                    HttpStatus.NOT_FOUND, e.getMessage(), e).response();
        }

        return new ResponseEntity<>(disseminations, HttpStatus.OK);
    }

    @GetMapping(value = "/earliest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity findEarliest() {
        Collection<Dissemination> disseminations;

        try {
            disseminations = disseminationService.findFirstRowsByProperty("lastmoddate", 1);
        } catch (NotFound notFound) {
            return new ErrorDetails(this.getClass().getName(), "find", "GET:findEarliest/earliest",
                    HttpStatus.NOT_FOUND, notFound.getMessage(), notFound).response();
        }

        return new ResponseEntity<>(disseminations, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) {
        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination;

        try {
            dissemination = om.readValue(input, Dissemination.class);
            dissemination = disseminationService.saveDissemination(dissemination);
        } catch (IOException e) {
            return new ErrorDetails(this.getClass().getName(), "save", "POST:disseminations",
                    HttpStatus.BAD_REQUEST, "", e).response();
        } catch (SaveFailed e) {
            return new ErrorDetails(this.getClass().getName(), "save", "POST:disseminations",
                    HttpStatus.NOT_ACCEPTABLE, e.getMessage(), e).response();
        }

        return new ResponseEntity<>(dissemination, HttpStatus.OK);
    }

    @PutMapping(value = "{uid}/{mdprefix}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(@RequestBody Dissemination input,
                                 @PathVariable String uid,
                                 @PathVariable String mdprefix) {
        Dissemination dissemination;

        try {
            dissemination = disseminationService.update(input);
        } catch (UpdateFailed updateFailed) {
            return new ErrorDetails(this.getClass().getName(), "update", "PUT:disseminations",
                    HttpStatus.NOT_ACCEPTABLE, updateFailed.getMessage(), updateFailed).response();
        }

        return new ResponseEntity<>(dissemination, HttpStatus.OK);
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@RequestBody Dissemination input) {

        try {
            disseminationService.delete(input);
        } catch (DeleteFailed deleteFailed) {
            return new ErrorDetails(this.getClass().getName(), "delete", "DELETE:disseminations",
                    HttpStatus.NOT_ACCEPTABLE, deleteFailed.getMessage(), deleteFailed).response();
        }

        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
