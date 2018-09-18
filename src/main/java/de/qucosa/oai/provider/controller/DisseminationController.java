/**
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
import de.qucosa.oai.provider.persistence.exceptions.UndoDeleteFailed;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;

@RequestMapping("/dissemination")
@RestController
public class DisseminationController {

    private Logger logger = LoggerFactory.getLogger(FormatsController.class);

    private DisseminationService disseminationService;

    private FormatService formatService;

    @Autowired
    public DisseminationController(FormatService formatService, DisseminationService disseminationService) {
        this.formatService = formatService;
        this.disseminationService = disseminationService;
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity find(@PathVariable String uid) {
        Collection<Dissemination> disseminations;

        try {
            disseminations = disseminationService.findAllByUid("recordid", uid);
        } catch (NotFound e) {
            return new ErrorDetails(this.getClass().getName(), "find", "GET:dissemination/" + uid,
                    HttpStatus.NOT_FOUND, "", e).response();
        }

        return new ResponseEntity(disseminations, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) {
        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination;

        try {
            dissemination = om.readValue(input, Dissemination.class);
            dissemination = disseminationService.saveDissemination(dissemination);
        } catch (IOException e) {
            return new ErrorDetails(this.getClass().getName(), "save", "POST:dissemination",
                    HttpStatus.BAD_REQUEST, "", e).response();
        } catch (SaveFailed e) {
            return new ErrorDetails(this.getClass().getName(), "save", "POST:dissemination",
                    HttpStatus.NOT_ACCEPTABLE, "", e).response();
        }

        return new ResponseEntity(dissemination, HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(@RequestBody Dissemination input, @PathVariable String uid) {
        // @todo clarify if is update the dissemination object meaningful.
        return new ResponseEntity(new Dissemination(), HttpStatus.OK);
    }

    @RequestMapping(value = {"{uid}/{mdprefix}", "{uid}/{mdprefix}/{undo}"}, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String uid, @PathVariable String mdprefix, @PathVariable(value = "undo", required = false) String undo) {
        Dissemination dissemination;

        try {
            Format format;

            try {
                Collection<Format> formats = formatService.find("mdprefix", mdprefix);

                if (!formats.isEmpty()) {
                    format = formats.iterator().next();
                } else {
                    return new ErrorDetails(this.getClass().getName(), "delete", "DELETE:dissemination/" + uid + "/" + mdprefix + "/" + delete,
                            HttpStatus.NOT_FOUND, "Cannot find format.", null).response();
                }
            } catch (NotFound fnf) {
                return new ErrorDetails(this.getClass().getName(), "delete", "DELETE:dissemination/" + uid + "/" + mdprefix + "/" + delete,
                        HttpStatus.NOT_FOUND, null, fnf).response();
            }

            try {
                dissemination = disseminationService.findByMultipleValues("formatid = %s AND recordid = %s", String.valueOf(format.getFormatId()), uid);

                if (undo == null || undo.isEmpty()) {
                    dissemination.setDeleted(true);
                } else if (undo.equals("undo")) {
                    dissemination.setDeleted(false);
                } else {
                    return errorDetails.create(this.getClass().getName(), "delete", "DELETE:dissemination/" + uid + "/" + mdprefix + "/" + undo,
                            HttpStatus.BAD_REQUEST, "The undo param is set, but wrong.", null).response();
                }

                disseminationService.deleteDissemination(dissemination, undo);
            } catch (NotFound dnf) {
                return new ErrorDetails(this.getClass().getName(), "delete", "DELETE:dissemination/" + uid + "/" + mdprefix + "/" + delete,
                        HttpStatus.NOT_FOUND, null, dnf).response();
            } catch (UndoDeleteFailed undoDeleteFailed) {
                return errorDetails.create(this.getClass().getName(), "delete", "DELETE:dissemination/" + uid + "/" + mdprefix + "/" + undo,
                        HttpStatus.NOT_ACCEPTABLE, null, undoDeleteFailed).response();
            }
        } catch (DeleteFailed e) {
            return new ErrorDetails(this.getClass().getName(), "delete", "DELETE:dissemination/" + uid + "/" + mdprefix + "/" + delete,
                    HttpStatus.NOT_ACCEPTABLE, null, e).response();
        }

        return new ResponseEntity(true, HttpStatus.OK);
    }
}
