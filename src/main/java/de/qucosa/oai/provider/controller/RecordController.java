package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.dissemination.DisseminationApi;
import de.qucosa.oai.provider.api.format.FormatApi;
import de.qucosa.oai.provider.api.record.RecordApi;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.persitence.model.Format;
import de.qucosa.oai.provider.persitence.model.Record;
import de.qucosa.oai.provider.persitence.model.RecordTransport;
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

    @Autowired
    private RecordApi recordApi;

    @Autowired
    private FormatApi formatApi;

    @Autowired
    private SetApi setApi;

    @Autowired
    private DisseminationApi disseminationApi;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) {
        ObjectMapper om = new ObjectMapper();

        try {
            List<RecordTransport> inputData = om.readValue(input, om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));

            if (inputData == null || inputData.size() == 0) {
                return new ResponseEntity("Record transport mapping failed.", HttpStatus.BAD_REQUEST);
            }

            for (RecordTransport rt : inputData) {
                Format format = null;

                try {
                    format = formatApi.find("mdprefix", rt.getFormat().getMdprefix());
                } catch (SQLException e) {

                    try {
                        format = formatApi.saveFormat(rt.getFormat());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }

                Record record = null;

                try {
                    record = recordApi.findRecord("uid", rt.getRecord().getUid());
                } catch (SQLException e) {

                    try {
                        record = recordApi.saveRecord(rt.getRecord());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }

                try {
                    setApi.saveSets(rt.getSets());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                rt.getDissemination().setFormatId(format.getFormatId());
                rt.getDissemination().setRecordId(record.getRecordId());

                try {
                    disseminationApi.saveDissemination(rt.getDissemination());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Record> find(@PathVariable String uid) {
        Record record = null;

        try {
            record = recordApi.findRecord("uid", uid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<Record>(record, HttpStatus.OK);
    }
}
