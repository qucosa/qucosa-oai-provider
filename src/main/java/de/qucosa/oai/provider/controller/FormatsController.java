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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.ErrorDetails;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.services.FormatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RequestMapping("/formats")
@RestController
public class FormatsController {
    private final FormatService formatService;

    @Autowired
    public FormatsController(FormatService formatService) {
        this.formatService = formatService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity findAll() {
        Collection<Format> formats;

        try {
            formats = formatService.findAll();
        } catch (NotFound e) {
            return new ErrorDetails(this.getClass().getName(), "findAll", "GET:formats",
                    HttpStatus.NOT_FOUND, "", e).response();
        }

        return new ResponseEntity<>(formats, HttpStatus.OK);
    }

    @GetMapping(value = "/format", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity find(@RequestParam(value = "mdprefix", required = false) String mdprefix,
                               @RequestParam(value = "formatId", required = false) Long formatId) {

        if (mdprefix == null && formatId == null) {
            return new ErrorDetails(this.getClass().getName(), "find",
                    "GET:formats/format",
                    HttpStatus.BAD_REQUEST, "You must set mdprefix or formatId request paramter.",
                    null).response();
        }

        if (mdprefix != null && formatId != null) {
            return new ErrorDetails(this.getClass().getName(), "find",
                    "GET:formats/format?formatId=" + formatId + "&mdprefix=" + mdprefix,
                    HttpStatus.BAD_REQUEST, "Setting from mdprefix and formatid is not allowed.",
                    null).response();
        }

        Collection<Format> formats;
        Format format = new Format();

        try {

            if (mdprefix != null) {
                formats = formatService.find("mdprefix", mdprefix);

                if (formats.isEmpty()) {
                    return new ResponseEntity<>(format, HttpStatus.OK);
//                    return new ErrorDetails(this.getClass().getName(), "find", "GET:formats/" + mdprefix,
//                            HttpStatus.NOT_FOUND, "Cannot found format.", null).response();
                }

                format = formats.iterator().next();
            }

            if (formatId != null) {
                format = formatService.findById(String.valueOf(formatId));
            }

        } catch (NotFound e) {
            return new ErrorDetails(this.getClass().getName(), "find", "GET:formats/" + mdprefix,
                    HttpStatus.NOT_FOUND, "", e).response();
        }

        return new ResponseEntity<>(format, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) {
        Object output;
        ObjectMapper om = new ObjectMapper();

        try {
            output = formatService.saveFormat(om.readValue(input, Format.class));
        } catch (IOException e) {

            try {
                output = formatService.saveFormats(om.readValue(input, om.getTypeFactory().constructCollectionType(List.class, Format.class)));
            } catch (IOException e1) {
                return new ErrorDetails(this.getClass().getName(), "save", "POST:formats",
                        HttpStatus.BAD_REQUEST, "", e).response();
            } catch (SaveFailed saveFailed) {
                return new ErrorDetails(this.getClass().getName(), "save", "POST:formats",
                        HttpStatus.NOT_ACCEPTABLE, saveFailed.getMessage(), saveFailed).response();
            }
        } catch (SaveFailed e) {
            return new ErrorDetails(this.getClass().getName(), "save", "POST:formats",
                    HttpStatus.NOT_ACCEPTABLE, e.getMessage(), e).response();
        }

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @RequestMapping(value = "{mdprefix}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(@RequestBody Format input, @PathVariable String mdprefix) {
        Format format;

        try {
            format = formatService.updateFormat(input, mdprefix);
        } catch (UpdateFailed e) {
            return new ErrorDetails(this.getClass().getName(), "update", "PUT:formats/" + mdprefix,
                    HttpStatus.NOT_ACCEPTABLE, e.getMessage(), e).response();
        }

        return new ResponseEntity<>(format, HttpStatus.OK);
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@RequestBody Format input) {

        try {
            formatService.delete(input);
        } catch (DeleteFailed deleteFailed) {
            return new ErrorDetails(this.getClass().getName(), "delete", "DELETE:formats",
                    HttpStatus.NOT_ACCEPTABLE, deleteFailed.getMessage(), deleteFailed).response();
        }

        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
