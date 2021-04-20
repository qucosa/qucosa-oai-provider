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
import de.qucosa.oai.provider.ErrorDetails;
import de.qucosa.oai.provider.api.exceptions.XmlDomParserException;
import de.qucosa.oai.provider.api.validators.xml.XmlSchemaValidator;
import de.qucosa.oai.provider.config.json.XmlNamespacesConfig;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.OaiRecord;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.persistence.model.SetsToRecord;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import de.qucosa.oai.provider.services.RecordService;
import de.qucosa.oai.provider.services.SetService;
import de.qucosa.oai.provider.services.SetsToRecordService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestMapping("/records")
@RestController
public class RecordController {

    private final Logger logger = LoggerFactory.getLogger(RecordController.class);

    private final RecordService recordService;

    private final FormatService formatService;

    private final SetService setService;

    private final DisseminationService disseminationService;

    private final SetsToRecordService setsToRecordService;

    private final XmlNamespacesConfig xmlNamespacesConfig;

    @Autowired
    public RecordController(RecordService recordService, FormatService formatService, SetService setService,
                            DisseminationService disseminationService, SetsToRecordService setsToRecordService,
                            XmlNamespacesConfig xmlNamespacesConfig) {
        this.recordService = recordService;
        this.formatService = formatService;
        this.setService = setService;
        this.disseminationService = disseminationService;
        this.setsToRecordService = setsToRecordService;
        this.xmlNamespacesConfig = xmlNamespacesConfig;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();

        try {
            OaiRecord oaiRecord = om.readValue(input, OaiRecord.class);

            if (oaiRecord == null) {
                logger.info(new ErrorDetails(this.getClass().getName(), "save", "POST:save",
                        HttpStatus.BAD_REQUEST, "Record transport mapping failed.", null).responseToString());

                return new ResponseEntity("Record transport mapping failed.", HttpStatus.BAD_REQUEST);
            }

            if (oaiRecord.isValidateXmlSchema()) {

                if (oaiRecord.getDissemination().getXmldata() != null && !oaiRecord.getDissemination().getXmldata().isEmpty()) {
                    XmlSchemaValidator schemaValidator = new XmlSchemaValidator(xmlNamespacesConfig);
                    schemaValidator.setFormat(oaiRecord.getFormat().getMdprefix());

                    try {
                        schemaValidator.setXmlDoc(oaiRecord.getDissemination().getXmldata());

                        try {

                            if (!schemaValidator.isValid()) {
                                logger.info(new ErrorDetails(this.getClass().getName(), "save", "POST:save",
                                        HttpStatus.NOT_ACCEPTABLE, "This xml has not valid schema.", null).responseToString());

                                return new ResponseEntity("This xml has not valid schema.", HttpStatus.NOT_ACCEPTABLE);
                            }
                        } catch (XPathExpressionException e) {
                            logger.info(new ErrorDetails(this.getClass().getName(), "save", "POST:save",
                                    HttpStatus.NOT_ACCEPTABLE, e.getMessage(), e).responseToString());

                            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
                        }
                    } catch (XmlDomParserException e) {
                        logger.info(new ErrorDetails(this.getClass().getName(), "save", "POST:save",
                                HttpStatus.NOT_ACCEPTABLE, e.getMessage(), e).responseToString());

                        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
                    }
                }
            }

            Format format = format(oaiRecord);

            if (format == null) {
                logger.info(new ErrorDetails(this.getClass().getName(), "save", "POST:save",
                        HttpStatus.NOT_ACCEPTABLE, "Cannot save format because properties are failed.", null).responseToString());

                return new ResponseEntity("Cannot save format because properties are failed.", HttpStatus.NOT_ACCEPTABLE);
            }

            Record record = record(oaiRecord, format);

            if (record == null) {
                logger.info(new ErrorDetails(this.getClass().getName(), "save", "POST:save",
                        HttpStatus.NOT_ACCEPTABLE, "Cannot find or save record.", null).responseToString());

                return new ResponseEntity("Cannot find or save record.", HttpStatus.NOT_ACCEPTABLE);
            }

            saveSets(oaiRecord, record);

            oaiRecord.getDissemination().setFormatId(format.getFormatId());
            oaiRecord.getDissemination().setRecordId(record.getOaiid());

            try {

                if (disseminationService.saveDissemination(oaiRecord.getDissemination()) == null) {
                    disseminationService.update(oaiRecord.getDissemination());
                }
            } catch (SaveFailed e) {
                logger.info(new ErrorDetails(this.getClass().getName(), "save", "POST:save",
                        HttpStatus.NOT_ACCEPTABLE, "Cannot save dissemination.", null).responseToString());

                return new ResponseEntity("Cannot save dissemination.", HttpStatus.NOT_ACCEPTABLE);
            } catch (UpdateFailed updateFailed) {
                logger.info(new ErrorDetails(this.getClass().getName(), "save", "POST:save",
                        HttpStatus.NOT_ACCEPTABLE, "Cannot update exists dissemination.", null).responseToString());

                return new ResponseEntity("Cannot update exists dissemination.", HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (IOException e) {
            logger.info(new ErrorDetails(this.getClass().getName(), "save", "POST:save",
                    HttpStatus.BAD_REQUEST, null, e).responseToString());

            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "{oaiid}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(@RequestBody String input, @PathVariable String oaiid) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Record updatedRecord;

        try {
            Record record = om.readValue(input, Record.class);

            try {
                updatedRecord = recordService.updateRecord(record, oaiid);
            } catch (UpdateFailed e) {
                logger.info(new ErrorDetails(this.getClass().getName(), "update", "PUT:update/{oaiid}",
                        HttpStatus.NOT_ACCEPTABLE, e.getMessage(), e).responseToString());

                return new ResponseEntity(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (IOException e) {
            logger.info(new ErrorDetails(this.getClass().getName(), "update", "PUT:update/{oaiid}",
                    HttpStatus.BAD_REQUEST, "Bad request input.", e).responseToString());

            return new ResponseEntity("Bad request input.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
    }

    @RequestMapping(value = {"{oaiid}"}, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String oaiid) throws JsonProcessingException {
        Record record = null;

        try {
            Collection<Record> records = recordService.findRecord("oaiid", oaiid);

            if (records != null) {
                record = records.iterator().next();

                if (record != null) {
                    try {
                        recordService.delete(record);
                    } catch (DeleteFailed deleteFailed) {
                        logger.info(new ErrorDetails(this.getClass().getName(), "delete", "DELETE:delete/{oaiid}",
                                HttpStatus.NOT_ACCEPTABLE, deleteFailed.getMessage(), deleteFailed).responseToString());

                        return new ResponseEntity(deleteFailed.getMessage(), HttpStatus.NOT_ACCEPTABLE);
                    }
                }
            }
        } catch (NotFound ignored) { }

        if (record == null) {
            logger.info(new ErrorDetails(this.getClass().getName(), "delete", "DELETE:delete/{oaiid}",
                    HttpStatus.NOT_FOUND, "Cannot found record.", null).responseToString());

            return new ResponseEntity("Cannot found record.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity findAll(@RequestParam(value = "metadataPrefix", required = false) String metadataPrefix,
                                  @RequestParam(value = "from", required = false) String from,
                                  @RequestParam(value = "until", required = false) String until) {
        Collection<Record> records = new ArrayList<>();

        try {

            if (from != null && metadataPrefix == null) {
                return new ResponseEntity<>(records, HttpStatus.OK);
            }

            if (metadataPrefix != null && from != null && until != null) {
                records = recordService.findRowsByMultipleValues("", metadataPrefix, from, until);
            }

            if (metadataPrefix != null && from != null && until == null) {
                records = recordService.findRowsByMultipleValues("lastmoddate BETWEEN ? AND NOW()", metadataPrefix, from);
            }

            if (metadataPrefix != null && from == null && until == null) {
                records = recordService.findRowsByMultipleValues("", metadataPrefix);
            }

            //noinspection ConstantConditions
            if (metadataPrefix == null && from == null && until == null) {
                records = recordService.findAll();
            }
        } catch (NotFound ignored) {}

        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @RequestMapping(value = "{oaiid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity find(@PathVariable(value = "oaiid", required = false) String oaiid) throws JsonProcessingException {
        Record record;

        try {
            Collection<Record> records = recordService.findRecord("oaiid", oaiid);

            if (records == null) {
                logger.info(new ErrorDetails(this.getClass().getName(), "find", "GET:find/{oaiid}",
                        HttpStatus.NOT_FOUND, "Cannot found record.", null).responseToString());

                return new ResponseEntity("Cannot found record.", HttpStatus.NOT_FOUND);
            }

            record = records.iterator().next();
        } catch (NotFound | JsonProcessingException e) {
            logger.info(new ErrorDetails(this.getClass().getName(), "find", "GET:find/{oaiid}",
                    HttpStatus.NOT_FOUND, e.getMessage(), e).responseToString());

            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(record, HttpStatus.OK);
    }

    private Format format(OaiRecord rt) throws JsonProcessingException {
        Collection<Format> formats;

        try {
            formats = formatService.find("mdprefix", rt.getFormat().getMdprefix());
            Format format = null;

            if (!formats.isEmpty()) {
                format = formats.iterator().next();
            }

            if (format == null) {

                try {
                    format = formatService.saveFormat(rt.getFormat());
                } catch (SaveFailed e1) {
                    logger.info(new ErrorDetails(this.getClass().getName(), "format", "POST:format",
                            HttpStatus.NOT_ACCEPTABLE, "Cannot save format.", e1).responseToString());
                }
            }

            return format;
        } catch (NotFound | JsonProcessingException e) {
            logger.info(new ErrorDetails(this.getClass().getName(), "format", "GET:format",
                    HttpStatus.NOT_FOUND, "Cannot find format.", e).responseToString());
        }

        return null;
    }

    private Record record(OaiRecord rt, Format format) throws JsonProcessingException {
        Collection<Record> records;
        Record record = null;

        try {
            records = recordService.findRecord("oaiid", rt.getRecord().getOaiid());

            if (records != null && records.size() > 0) {
                record = records.iterator().next();
            }

            if (record == null) {

                try {
                    Record saveRec = rt.getRecord();
                    Pattern pattern = Pattern.compile("qucosa:\\d+");
                    Matcher matcher = pattern.matcher(saveRec.getOaiid());

                    if (matcher.find()) {
                        saveRec.setPid(matcher.group(0));
                    }

                    if (format.getMdprefix().equals("oai_dc")) {
                        saveRec.setVisible(true);
                    }

                    record = recordService.saveRecord(saveRec);
                } catch (SaveFailed e1) {
                    logger.info(new ErrorDetails(this.getClass().getName(), "record", "POST:record",
                            HttpStatus.NOT_ACCEPTABLE, "Cannot save record.", e1).responseToString());
                }
            }

            return record;
        } catch (NotFound | JsonProcessingException e) {
            logger.info(new ErrorDetails(this.getClass().getName(), "record", "GET:record",
                    HttpStatus.NOT_FOUND, "Cannot find record by uid (" + rt.getRecord().getOaiid() + ").", e).responseToString());
        }

        return null;
    }

    @SuppressWarnings("UnusedReturnValue")
    private ResponseEntity saveSets(OaiRecord rt, Record record) throws JsonProcessingException {

        for (Set set : rt.getSets()) {
            Set readSet = null;

            try {
                Collection<Set> sets = setService.find("setspec", set.getSetSpec());

                if (!sets.isEmpty()) {
                    readSet = sets.iterator().next();
                } else {
                    logger.info("Cannot find set (" + set.getSetSpec() + ").");
                }
            } catch (NotFound ignore) { }

            if (readSet == null) {

                try {
                    set = setService.saveSet(set);
                } catch (SaveFailed e) {
                    logger.info(new ErrorDetails(this.getClass().getName(), "save", "POST:save",
                            HttpStatus.BAD_REQUEST, null, e).responseToString());

                    return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
                }
            } else {
                set = readSet;
            }

            boolean strExsists = false;

            try {
                SetsToRecord findStr = setsToRecordService.findByMultipleValues(
                        "id_set=%s AND id_record=%s",
                        String.valueOf(set.getIdentifier()), String.valueOf(record.getIdentifier()));

                if (findStr != null && findStr.getIdSet() != null && findStr.getIdRecord() != null) {
                    strExsists = true;
                    setsToRecordService.delete(findStr);
                }
            } catch (NotFound | DeleteFailed e) {
                logger.info("Cannot find set to record entry (set:" + set.getIdentifier() + " / record:" + record.getRecordId() + ").", e);
            }

            if (!strExsists) {
                SetsToRecord setsToRecord = new SetsToRecord();
                setsToRecord.setIdRecord(record.getRecordId());
                setsToRecord.setIdSet(Long.valueOf(set.getIdentifier().toString()));

                try {
                    setsToRecordService.saveAndSetIdentifier(setsToRecord);
                } catch (SaveFailed e) {
                    logger.info(new ErrorDetails(this.getClass().getName(), "save", "POST:save",
                            HttpStatus.NOT_ACCEPTABLE, null, e).responseToString());

                    return new ResponseEntity(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
                }
            }
        }

        return new ResponseEntity<>("Save sets is successful", HttpStatus.OK);
    }
}
