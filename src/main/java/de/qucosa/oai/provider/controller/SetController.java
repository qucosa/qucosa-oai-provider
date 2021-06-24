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
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.services.SetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
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

@RequestMapping("/sets")
@RestController
public class SetController {
    private final Logger logger = LoggerFactory.getLogger(SetController.class);

    private final SetService setService;

    @Autowired
    public SetController(SetService setService) {
        this.setService = setService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity findAll() throws JsonProcessingException {
        Collection<Set> sets = setService.findAll();
        return new ResponseEntity<>(sets, HttpStatus.OK);
    }

    @RequestMapping(value = "{setspec}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity find(@PathVariable String setspec) {
        Set set = new Set();
        Collection<Set> sets = setService.find("setspec", setspec);

        if (!sets.isEmpty()) {
            set = sets.iterator().next();
        }

        return new ResponseEntity<>(set, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) {
        Object output;
        ObjectMapper om = new ObjectMapper();

        try {
            output = setService.saveSet(om.readValue(input, Set.class));
        } catch (IOException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger)
                    .level(Level.ERROR)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Cannot parse JSON input.");
            aeh.log();
            return new ResponseEntity<>(aeh.message(), aeh.httpStatus());
        }

        if (output == null) {
            return new ResponseEntity("Cannot save set objects.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @RequestMapping(value = "{setspec}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(@RequestBody Set input, @PathVariable String setspec) throws JsonProcessingException {
        Set set = setService.updateSet(input, setspec);

        if (set == null) {
            return new ResponseEntity("Cannot update set objects.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(set, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@RequestBody Set input) throws JsonProcessingException {
        setService.delete(input);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
