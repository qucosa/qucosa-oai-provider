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

import de.qucosa.oai.provider.ErrorDetails;
import de.qucosa.oai.provider.api.builders.oaipmh.OaiPmhFactory;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import de.qucosa.oai.provider.services.RecordService;
import de.qucosa.oai.provider.services.SetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import java.util.Collection;

@RequestMapping("/oai")
@RestController
public class OaiPmhController {
    private Logger logger = LoggerFactory.getLogger(OaiPmhController.class);

    @Value("${records.pro.page}")
    private int recordsProPage;

    @Value("${expiries.hours}")
    private int expiriesHours;

    private int cursor = 0;

    private RecordService recordService;

    private FormatService formatService;

    private SetService setService;

    private DisseminationService disseminationService;

    @Autowired
    public OaiPmhController(RecordService recordService, FormatService formatService, SetService setService,
                            DisseminationService disseminationService) {
        this.recordService = recordService;
        this.formatService = formatService;
        this.setService = setService;
        this.disseminationService = disseminationService;
    }

    @GetMapping(value = {"{verb}", "{verb}/{metadataPrefix}", "{verb}/{metadataPrefix}/{from}",
            "{verb}/{metadataPrefix}/{from}/{until}"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity findAll(@PathVariable String verb,
                                  @PathVariable(value = "metadataPrefix", required = false) String metadataPrefix,
                                  @PathVariable(value = "from", required = false) String from,
                                  @PathVariable(value = "until", required = false) String until) {
        Format format = findFormat(metadataPrefix);
        Collection<Record> records;

        if (format == null) {
            return new ErrorDetails(this.getClass().getName(), "find", "GET:find",
                    HttpStatus.NOT_FOUND, "Cannot found format " + metadataPrefix + ".", null)
                    .response();
        }

        try {
            records = recordService.findAll();
        } catch (NotFound notFound) {
            return new ErrorDetails(this.getClass().getName(), "findAll", "GET:findAll",
                    HttpStatus.NOT_FOUND, "Cannot found records.", notFound)
                    .response();
        }

        OaiPmhFactory oaiPmhFactory = new OaiPmhFactory(getClass().getResourceAsStream("/templates/oai_pmh.xml"));
        Document oaiPmhList = oaiPmhFactory.createList(verb, format, records, disseminationService);

        return new ResponseEntity(oaiPmhList, HttpStatus.OK);
    }

    private Format findFormat(String metadataPrefix) {
        Collection<Format> formats;
        Format format = null;

        try {
            formats = formatService.find("mdprefix", metadataPrefix);

            if (!formats.isEmpty()) {
                format = formats.iterator().next();
            }
        } catch (NotFound notFound) {
            logger.error("Format " + metadataPrefix + " not found!");
        }

        return format;
    }
}
