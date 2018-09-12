package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
import de.qucosa.oai.provider.services.RecordService;
import de.qucosa.oai.provider.services.SetService;
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
import java.util.List;

@RequestMapping("/records")
@RestController
public class RecordController {

    private Logger logger = LoggerFactory.getLogger(RecordController.class);

    @Autowired
    private RecordApi recordApi;

    @Autowired
    private FormatApi formatApi;

    @Autowired
    private SetApi setApi;

    @Autowired
    private DisseminationApi disseminationApi;

    @Autowired
    private Dao setsToRecordDao;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) {
        ObjectMapper om = new ObjectMapper();

        try {
            List<RecordTransport> inputData = om.readValue(input, om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));

            if (inputData == null || inputData.size() == 0) {
                return new ResponseEntity("Record transport mapping failed.", HttpStatus.BAD_REQUEST);
            }

            if (!recordApi.checkIfOaiDcDisseminationExists(inputData)) {
                return new ResponseEntity("OAI_DC dissemination failed.", HttpStatus.BAD_REQUEST);
            }

            for (RecordTransport rt : inputData) {
                Format format = null;

                try {
                    format = (Format) formatApi.find("mdprefix", rt.getFormat().getMdprefix()).iterator().next();

                    if (format.getFormatId() == null) {

                        try {
                            format = formatApi.saveFormat(rt.getFormat());
                        } catch (SaveFailed e1) {
                            // @todo build / init an error object?
                            logger.error("Cannot save format.", e1);
                        }
                    }
                } catch (NotFound e) {
                    logger.warn("Cannot find format.", e);
                }

                // @todo return an error object?
                if (format == null) {
                    return new ResponseEntity("Cannot find or save format.", HttpStatus.BAD_REQUEST);
                }

                Record record = null;

                try {
                    record = (Record) recordApi.findRecord("uid", rt.getRecord().getUid()).iterator().next();

                    if (record.getRecordId() == null) {

                        try {
                            record = recordApi.saveRecord(rt.getRecord());
                        } catch (SaveFailed e1) {
                            // @todo build / init an error object?
                            logger.error("Cannot save record..", e1);
                        }
                    }
                } catch (NotFound e) {
                    logger.info("Cannot find record by uid (" + rt.getRecord().getUid() + ").", e);
                }

                // @todo return an error object?
                if (record == null) {
                    return new ResponseEntity("Cannot find or save record.", HttpStatus.BAD_REQUEST);
                }

                for (Set set : rt.getSets()) {
                    Set readSet = null;

                    try {
                        readSet = (Set) setApi.find("setspec", set.getSetSpec()).iterator().next();
                    } catch (NotFound e) {
                        logger.info("Cannot find set (" + set.getSetSpec() + ").");
                    }

                    if (readSet == null) {

                        try {
                            set = setApi.saveSet(set);
                        } catch (SaveFailed e) {
                            logger.error("Cannot save set (" + set.getSetSpec() + ")", e);
                            return new ResponseEntity("Cannot save set (" + set.getSetSpec() + ".", HttpStatus.BAD_REQUEST);
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
                            logger.error("Cannot save set to record entry for (set:" + set.getSetId() + " / record:" + record.getRecordId() + ").", e);
                            return new ResponseEntity("Cannot save set to record entry for (set:" + set.getSetId() + " / record:" + record.getRecordId() + ").", HttpStatus.BAD_REQUEST);
                        }
                    }
                }

                rt.getDissemination().setFormatId(format.getFormatId());
                rt.getDissemination().setRecordId(record.getUid());

                try {
                    disseminationApi.saveDissemination(rt.getDissemination());
                } catch (SaveFailed e) {
                    return new ResponseEntity("Dissemination cannot save.", HttpStatus.BAD_REQUEST);
                }
            }
        } catch (IOException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
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
                updatedRecord = recordApi.updateRecord(record);
            } catch (UpdateFailed e) {
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(updatedRecord, HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}/{delete}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String uid, @PathVariable boolean delete) {
        int deleted;
        try {
            Record record = (Record) recordApi.findRecord("uid", uid).iterator().next();
            record.setDeleted(delete);

            try {
                deleted = recordApi.deleteRecord(record);
            } catch (DeleteFailed deleteFailed) {
                return new ResponseEntity("Record cannot delete.", HttpStatus.BAD_REQUEST);
            }
        } catch (NotFound e) {
            return new ResponseEntity("Record with uid (" + uid + ") not found.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(deleted, HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Record> find(@PathVariable String uid) {
        Record record;

        try {
            record = (Record) recordApi.findRecord("uid", uid).iterator().next();
        } catch (NotFound e) {
            return new ResponseEntity("Record with uid (" + uid + ") not found.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Record>(record, HttpStatus.OK);
    }
}
