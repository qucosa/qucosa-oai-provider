package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.dissemination.DisseminationApi;
import de.qucosa.oai.provider.api.format.FormatApi;
import de.qucosa.oai.provider.api.record.RecordApi;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Format;
import de.qucosa.oai.provider.persitence.model.Record;
import de.qucosa.oai.provider.persitence.model.RecordTransport;
import de.qucosa.oai.provider.persitence.model.Set;
import de.qucosa.oai.provider.persitence.model.SetsToRecord;
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
import java.sql.SQLException;
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
                    format = formatApi.find("mdprefix", rt.getFormat().getMdprefix());

                    if (format.getFormatId() == null) {

                        try {
                            format = formatApi.saveFormat(rt.getFormat());
                        } catch (SQLException e1) {
                            // @todo build / init an error object?
                            logger.error("Cannot save format.", e1);
                        }
                    }
                } catch (SQLException e) {
                    logger.warn("Cannot find format.", e);
                }

                // @todo return an error object?
                if (format == null) {
                    return new ResponseEntity("Cannot find or save format.", HttpStatus.BAD_REQUEST);
                }

                Record record = null;

                try {
                    record = recordApi.findRecord("uid", rt.getRecord().getUid());

                    if (record.getRecordId() == null) {

                        try {
                            record = recordApi.saveRecord(rt.getRecord());
                        } catch (SQLException e1) {
                            // @todo build / init an error object?
                            logger.error("Cannot save record..", e1);
                        }
                    }
                } catch (SQLException e) {
                    logger.info("Cannot find record by uid (" + rt.getRecord().getUid() + ").", e);
                }

                // @todo return an error object?
                if (record == null) {
                    return new ResponseEntity("Cannot find or save record.", HttpStatus.BAD_REQUEST);
                }

                for (Set set : rt.getSets()) {
                    Set readSet = null;

                    try {
                        readSet = setApi.find("setspec", set.getSetSpec());
                    } catch (SQLException e) {
                        logger.info("Cannot find set (" + set.getSetSpec() + ").");
                    }

                    if (readSet == null) {

                        try {
                            set = setApi.saveSet(set);
                        } catch (SQLException e) {
                            logger.error("Cannot save set (" + set.getSetSpec() + ")", e);
                            return new ResponseEntity("Cannot save set (" + set.getSetSpec() + ".", HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        set = readSet;
                    }

                    int strResult = 0;

                    try {
                        strResult = (int) setsToRecordDao.findByMultipleValues(
                                "id_set=%s AND id_record=%s",
                                String.valueOf(set.getSetId()), String.valueOf(record.getRecordId()));
                    } catch (SQLException e) {
                        logger.info("Cannot find set to record entry (set:" + set.getSetId() + " / record:" + record.getRecordId() + ").", e);
                    }

                    if (strResult == 0) {
                        SetsToRecord setsToRecord = new SetsToRecord();
                        setsToRecord.setIdRecord(record.getRecordId());
                        setsToRecord.setIdSet(set.getSetId());

                        try {
                            setsToRecordDao.save(setsToRecord);
                        } catch (SQLException e) {
                            logger.error("Cannot save set to record entry for (set:" + set.getSetId() + " / record:" + record.getRecordId() + ").", e);
                            return new ResponseEntity("Cannot save set to record entry for (set:" + set.getSetId() + " / record:" + record.getRecordId() + ").", HttpStatus.BAD_REQUEST);
                        }
                    }
                }

                rt.getDissemination().setFormatId(format.getFormatId());
                rt.getDissemination().setRecordId(record.getUid());

                try {
                    disseminationApi.saveDissemination(rt.getDissemination());
                } catch (SQLException e) {
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
            } catch (SQLException e) {
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(updatedRecord, HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}/{delete}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Record> delete(@PathVariable String uid, @PathVariable boolean delete) {
        Record record;

        try {
            record = recordApi.findRecord("uid", uid);
            record.setDeleted(delete);
            record = recordApi.deleteRecord(record);
        } catch (SQLException e) {
            return new ResponseEntity("Record with uid (" + uid + ") not found.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Record>(record, HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Record> find(@PathVariable String uid) {
        Record record;

        try {
            record = recordApi.findRecord("uid", uid);
        } catch (SQLException e) {
            return new ResponseEntity("Record with uid (" + uid + ") not found.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Record>(record, HttpStatus.OK);
    }
}
