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
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.services.FormatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
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

@RequestMapping("/formats")
@RestController
public class FormatsController {
    private final FormatService formatService;

    private final Logger logger = LoggerFactory.getLogger(FormatsController.class);

    @Autowired
    public FormatsController(FormatService formatService) {
        this.formatService = formatService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity findAll() {
        return new ResponseEntity<>(formatService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/format", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @ResponseBody
    public ResponseEntity find(@RequestParam(value = "mdprefix", required = false) String mdprefix,
                                         @RequestParam(value = "formatId", required = false) Long formatId) throws JsonProcessingException {

        if (mdprefix == null && formatId == null) {
            AppErrorHandler aeh = new AppErrorHandler(logger)
                    .level(Level.ERROR)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Missing mdprefix or formatId request parameter.");
            aeh.log();
            return new ResponseEntity(aeh.message(), aeh.httpStatus());
        }

        if (mdprefix != null && formatId != null) {
            AppErrorHandler aeh = new AppErrorHandler(logger)
                    .level(Level.ERROR)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Setting of mdprefix and formatid is not allowed.");
            aeh.log();
            return new ResponseEntity(aeh.message(), aeh.httpStatus());
        }

        Collection<Format> formats;
        Format format = new Format();

        if (mdprefix != null) {
            formats = formatService.find("mdprefix", mdprefix);

            if (!formats.isEmpty()) {
                format = formats.iterator().next();
            }
        }

        if (formatId != null) {
            format = formatService.findById(String.valueOf(formatId));
        }

        if (format.getFormatId() == null || format.getMdprefix().isEmpty()) {
            AppErrorHandler aeh = new AppErrorHandler(logger)
                    .level(Level.WARN)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("Cannot found format.");
            aeh.log();
            return new ResponseEntity<>(aeh.message(), aeh.httpStatus());
        }

        return new ResponseEntity<>(format, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) throws JsonProcessingException {
        Object output = new Format();
        ObjectMapper om = new ObjectMapper();

        try {
            output = formatService.saveFormat(om.readValue(input, Format.class));
        } catch (IOException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR).exception(e)
                    .message("The format input object is bad.")
                    .httpStatus(HttpStatus.BAD_REQUEST);
            aeh.log();
            return new ResponseEntity(aeh.message(), aeh.httpStatus());
        }

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @RequestMapping(value = "{mdprefix}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(@RequestBody Format input, @PathVariable String mdprefix) throws JsonProcessingException {
        Format format = formatService.updateFormat(input);

        if (format == null) {
            AppErrorHandler aeh = new AppErrorHandler(logger).level(Level.ERROR)
                    .message("Cannot update format " + input.getMdprefix() + ".")
                    .httpStatus(HttpStatus.BAD_REQUEST);
            aeh.log();
            return new ResponseEntity(aeh.message(), aeh.httpStatus());
        }

        return new ResponseEntity<>(format, HttpStatus.OK);
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@RequestBody Format input) throws JsonProcessingException {
        formatService.delete(input);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
