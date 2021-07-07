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
import de.qucosa.oai.provider.api.exceptions.XmlDomParserException;
import de.qucosa.oai.provider.api.validators.xml.XmlSchemaValidator;
import de.qucosa.oai.provider.config.json.XmlNamespacesConfig;
import de.qucosa.oai.provider.persistence.model.Dissemination;
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
import org.slf4j.event.Level;
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

    private static final Logger logger = LoggerFactory.getLogger(RecordController.class);

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
                AppErrorHandler aeh = new AppErrorHandler(logger)
                        .level(Level.ERROR)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message("Oai record mapping failed.");
                aeh.log();
                return new ResponseEntity(aeh.message(), aeh.httpStatus());
            }

            if (oaiRecord.isValidateXmlSchema()) {

                if (oaiRecord.getDissemination().getXmldata() != null && !oaiRecord.getDissemination().getXmldata().isEmpty()) {
                    XmlSchemaValidator schemaValidator = new XmlSchemaValidator(xmlNamespacesConfig);
                    schemaValidator.setFormat(oaiRecord.getFormat().getMdprefix());

                    try {
                        schemaValidator.setXmlDoc(oaiRecord.getDissemination().getXmldata());

                        try {

                            if (!schemaValidator.isValid()) {
                                AppErrorHandler aeh = new AppErrorHandler(logger)
                                        .level(Level.ERROR)
                                        .httpStatus(HttpStatus.BAD_REQUEST)
                                        .message("This xml has not valid schema.");
                                aeh.log();
                                return new ResponseEntity(aeh.message(), aeh.httpStatus());
                            }
                        } catch (XPathExpressionException e) {
                            AppErrorHandler aeh = new AppErrorHandler(logger)
                                    .level(Level.ERROR)
                                    .exception(e)
                                    .httpStatus(HttpStatus.BAD_REQUEST)
                                    .message(e.getMessage());
                            aeh.log();
                            return new ResponseEntity(aeh.message(), aeh.httpStatus());
                        }
                    } catch (XmlDomParserException e) {
                        AppErrorHandler aeh = new AppErrorHandler(logger)
                                .level(Level.ERROR)
                                .exception(e)
                                .httpStatus(HttpStatus.BAD_REQUEST)
                                .message(e.getMessage());
                        aeh.log();
                        return new ResponseEntity(aeh.message(), aeh.httpStatus());
                    }
                }
            }

            Format format = format(oaiRecord);

            if (format == null) {
                AppErrorHandler aeh = new AppErrorHandler(logger)
                        .level(Level.ERROR)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message("Cannot find or save format.");
                aeh.log();
                return new ResponseEntity(aeh.message(), aeh.httpStatus());
            }

            Record record = saveAndReturnRecord(oaiRecord, format);

            if (record == null) {
                // FIXME Hier könnte ergründet werden, was tatsächlich falsch ist am Request
                AppErrorHandler aeh = new AppErrorHandler(logger)
                        .level(Level.ERROR)
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message("Cannot find or save record.");
                aeh.log();
                return new ResponseEntity(aeh.message(), aeh.httpStatus());
            }

            saveSets(oaiRecord, record);

            oaiRecord.getDissemination().setFormatId(format.getFormatId());
            oaiRecord.getDissemination().setRecordId(record.getOaiid());

            if (disseminationService.saveDissemination(oaiRecord.getDissemination()) == null) {
                Dissemination dissemination = disseminationService.update(oaiRecord.getDissemination());

                if (dissemination == null) {
                    AppErrorHandler aeh = new AppErrorHandler(logger)
                            .level(Level.ERROR)
                            .httpStatus(HttpStatus.BAD_REQUEST)
                            .message("Cannot save or update dissemination.");
                    aeh.log();
                    //return new ResponseEntity(aeh.message(), aeh.httpStatus());
                }
            }
        } catch (IOException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger)
                    .level(Level.ERROR)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Cannot parse JSON input.");
            aeh.log();
            return new ResponseEntity(aeh.message(), aeh.httpStatus());
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(@RequestBody String input) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Record updatedRecord;

        try {
            Record record = om.readValue(input, Record.class);
            updatedRecord = recordService.updateRecord(record);
        } catch (IOException e) {
            AppErrorHandler aeh = new AppErrorHandler(logger)
                    .level(Level.ERROR)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Cannot parse JSON input.");
            aeh.log();
            return new ResponseEntity(aeh.message(), aeh.httpStatus());
        }

        return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
    }

    @RequestMapping(value = {"{oaiid}"}, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String oaiid) throws JsonProcessingException {
        recordService.delete(oaiid);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity findAll(@RequestParam(value = "metadataPrefix", required = false) String metadataPrefix,
                                  @RequestParam(value = "from", required = false) String from,
                                  @RequestParam(value = "until", required = false) String until) {
        Collection<Record> records = new ArrayList<>();

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

        if (metadataPrefix == null && from == null && until == null) {
            records = recordService.findAll();
        }

        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @RequestMapping(value = "{oaiid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity find(@PathVariable(value = "oaiid", required = false) String oaiid) throws JsonProcessingException {
        Record record = null;
        Collection<Record> records = recordService.findRecord("oaiid", oaiid);

        if (records.size() > 0) {
            record = records.iterator().next();
        }

        return new ResponseEntity<>(record, HttpStatus.OK);
    }

    private Format format(OaiRecord rt) throws JsonProcessingException {
        Collection<Format> formats;

        formats = formatService.find("mdprefix", rt.getFormat().getMdprefix());
        Format format = null;

        if (!formats.isEmpty()) {
            format = formats.iterator().next();
        }

        if (format == null) {
            format = formatService.saveFormat(rt.getFormat());
        }

        return format;
    }

    /**
     * Save record, return if successful, null otherwise
     *
     * @param rt
     * @param format
     * @return Saved object, with updated identifier, otherwise null
     */
    private Record saveAndReturnRecord(OaiRecord rt, Format format) {
        Collection<Record> records =
                recordService.findRecord("oaiid", rt.getRecord().getOaiid());
        Record record = null;

        if (records != null && records.size() > 0) {
            record = records.iterator().next();
        }

        if (record == null) {

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
        }

        return record;
    }

    @SuppressWarnings("UnusedReturnValue")
    private ResponseEntity saveSets(OaiRecord rt, Record record) throws JsonProcessingException {

        for (Set set : rt.getSets()) {
            Set readSet = null;
            Collection<Set> sets = setService.find("setspec", set.getSetSpec());

            if (!sets.isEmpty()) {
                readSet = sets.iterator().next();
            }

            set = (readSet == null) ? setService.saveSet(set) : readSet;
            boolean strExsists = false;

            SetsToRecord findStr = setsToRecordService.findByMultipleValues(
                    "id_set=%s AND id_record=%s",
                    String.valueOf(set.getIdentifier()), String.valueOf(record.getIdentifier()));

            if (findStr != null && findStr.getIdSet() != null && findStr.getIdRecord() != null) {
                strExsists = true;
                setsToRecordService.delete(findStr);
            }

            if (!strExsists) {
                SetsToRecord setsToRecord = new SetsToRecord();
                setsToRecord.setIdRecord(record.getRecordId());
                setsToRecord.setIdSet(Long.valueOf(set.getIdentifier().toString()));

                setsToRecordService.saveAndSetIdentifier(setsToRecord);
            }
        }

        return new ResponseEntity<>("Save sets is successful", HttpStatus.OK);
    }
}
