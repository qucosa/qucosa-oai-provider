package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.ErrorDetails;
import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.persistence.model.Record;
import de.qucosa.oai.provider.persistence.model.RecordTransport;
import de.qucosa.oai.provider.persistence.model.Set;
import de.qucosa.oai.provider.persistence.model.SetsToRecord;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import de.qucosa.oai.provider.services.RecordService;
import de.qucosa.oai.provider.services.SetService;
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
import java.util.List;

@RequestMapping("/records")
@RestController
public class RecordController {

    private Logger logger = LoggerFactory.getLogger(RecordController.class);

    @Autowired
    private RecordService recordService;

    @Autowired
    private FormatService formatService;

    @Autowired
    private SetService setService;

    @Autowired
    private DisseminationService disseminationService;

    @Autowired
    private Dao setsToRecordDao;

    @Autowired
    private ErrorDetails errorDetails;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) {
        ObjectMapper om = new ObjectMapper();

        try {
            List<RecordTransport> inputData = om.readValue(input, om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));

            if (inputData == null || inputData.size() == 0) {
                return errorDetails.create(
                        this.getClass().getName(), "save", "POST:save",
                        HttpStatus.BAD_REQUEST, "Record transport mapping failed.", null).response();
            }

            if (!recordService.checkIfOaiDcDisseminationExists(inputData)) {
                return errorDetails.create(
                        this.getClass().getName(), "save", "POST:save",
                        HttpStatus.BAD_REQUEST, "OAI_DC dissemination failed.", null).response();
            }

            for (RecordTransport rt : inputData) {
                Format format = null;

                try {
                    format = (Format) formatService.find("mdprefix", rt.getFormat().getMdprefix()).iterator().next();

                    if (format.getFormatId() == null) {

                        try {
                            format = formatService.saveFormat(rt.getFormat());
                        } catch (SaveFailed e1) {
                            logger.error("Cannot save format.", e1);
                        }
                    }
                } catch (NotFound e) {
                    logger.warn("Cannot find format.", e);
                }

                if (format == null) {
                    return errorDetails.create(
                            this.getClass().getName(), "save", "POST:save",
                            HttpStatus.NOT_ACCEPTABLE, "Cannot save format because properties are failed.", null).response();
                }

                Record record = null;

                try {
                    record = (Record) recordService.findRecord("uid", rt.getRecord().getUid()).iterator().next();

                    if (record.getRecordId() == null) {

                        try {
                            record = recordService.saveRecord(rt.getRecord());
                        } catch (SaveFailed e1) {
                            logger.error("Cannot save record..", e1);
                        }
                    }
                } catch (NotFound e) {
                    logger.info("Cannot find record by uid (" + rt.getRecord().getUid() + ").", e);
                }

                if (record == null) {
                    return errorDetails.create(
                            this.getClass().getName(), "save", "POST:save",
                            HttpStatus.NOT_ACCEPTABLE, "Cannot find or save record.", null).response();
                }

                for (Set set : rt.getSets()) {
                    Set readSet = null;

                    try {
                        Collection<Set> sets = setService.find("setspec", set.getSetSpec());

                        if (sets != null) {
                            readSet = sets.iterator().next();
                        } else {
                            logger.info("Cannot find set (" + set.getSetSpec() + ").");
                        }
                    } catch (NotFound ignore) { }

                    if (readSet == null) {

                        try {
                            set = setService.saveSet(set);
                        } catch (SaveFailed e) {
                            return errorDetails.create(
                                    this.getClass().getName(), "save", "POST:save",
                                    HttpStatus.BAD_REQUEST, null, e).response();
                        }
                    } else {
                        set = readSet;
                    }

                    boolean strExsists = false;

                    try {
                        SetsToRecord findStr = (SetsToRecord) setsToRecordDao.findByMultipleValues(
                                "id_set=%s AND id_record=%s",
                                String.valueOf(set.getIdentifier()), String.valueOf(record.getIdentifier()));

                        if (findStr != null && findStr.getIdSet() != null && findStr.getIdRecord() != null) {
                            strExsists = true;
                        }
                    } catch (NotFound e) {
                        logger.info("Cannot find set to record entry (set:" + set.getIdentifier() + " / record:" + record.getRecordId() + ").", e);
                    }

                    if (!strExsists) {
                        SetsToRecord setsToRecord = new SetsToRecord();
                        setsToRecord.setIdRecord(record.getRecordId());
                        setsToRecord.setIdSet(Long.valueOf(set.getIdentifier().toString()));

                        try {
                            setsToRecordDao.saveAndSetIdentifier(setsToRecord);
                        } catch (SaveFailed e) {
                            return errorDetails.create(
                                    this.getClass().getName(), "save", "POST:save",
                                    HttpStatus.NOT_ACCEPTABLE, null, e).response();
                        }
                    }
                }

                rt.getDissemination().setFormatId(format.getFormatId());
                rt.getDissemination().setRecordId(record.getUid());

                try {

                    if (disseminationService.saveDissemination(rt.getDissemination()) == null) {
                        return errorDetails.create(
                                this.getClass().getName(), "save", "POST:save",
                                HttpStatus.NOT_ACCEPTABLE, "Cannot save dissemination because exists.", null).response();
                    }
                } catch (SaveFailed e) {
                    return errorDetails.create(
                            this.getClass().getName(), "save", "POST:save",
                            HttpStatus.NOT_ACCEPTABLE, "Cannot save dissemination.", null).response();
                }
            }
        } catch (IOException e) {
            return errorDetails.create(
                    this.getClass().getName(), "save", "POST:save",
                    HttpStatus.BAD_REQUEST, null, e).response();
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Record> update(@RequestBody String input, @PathVariable String uid) {
        ObjectMapper om = new ObjectMapper();
        Record updatedRecord;

        try {
            Record record = om.readValue(input, Record.class);

            try {
                updatedRecord = recordService.updateRecord(record);
            } catch (UpdateFailed e) {
                return errorDetails.create(
                        this.getClass().getName(), "update", "PUT:update/{uid}",
                        HttpStatus.NOT_ACCEPTABLE, null, e).response();
            }
        } catch (IOException e) {
            return errorDetails.create(
                    this.getClass().getName(), "update", "PUT:update/{uid}",
                    HttpStatus.BAD_REQUEST, null, e).response();
        }

        return new ResponseEntity(updatedRecord, HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}/{delete}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String uid, @PathVariable boolean delete) {
        int deleted;
        try {
            Record record = (Record) recordService.findRecord("uid", uid).iterator().next();
            record.setDeleted(delete);

            try {
                deleted = recordService.deleteRecord(record);
            } catch (DeleteFailed deleteFailed) {
                return errorDetails.create(
                        this.getClass().getName(), "delete", "DELETE:delete/{uid}/{delete}",
                        HttpStatus.NOT_ACCEPTABLE, null, deleteFailed).response();
            }
        } catch (NotFound e) {
            return errorDetails.create(
                    this.getClass().getName(), "delete", "DELETE:delete/{uid}/{delete}",
                    HttpStatus.NOT_FOUND, null, e).response();
        }

        return new ResponseEntity(deleted, HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Record> find(@PathVariable String uid) {
        Record record;

        try {
            record = (Record) recordService.findRecord("uid", uid).iterator().next();
        } catch (NotFound e) {
            return errorDetails.create(
                    this.getClass().getName(), "find", "GET:find/{uid}",
                    HttpStatus.NOT_FOUND, null, e).response();
        }

        return new ResponseEntity<Record>(record, HttpStatus.OK);
    }

}
