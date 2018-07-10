package de.qucosa.oai.provider.controller;

import de.qucosa.oai.provider.persitence.Dao;
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

@RequestMapping("/record")
@RestController
public class RecordController {
    @Autowired
    private Dao recordDao;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody RecordTransport input) {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Record> update(@RequestBody RecordTransport input, @PathVariable String uid) {
        return new ResponseEntity(new Record(), HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Record> delete(@PathVariable String uid) {
        return new ResponseEntity(new Record(), HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Record> find(@PathVariable String uid) {
        return new ResponseEntity(new Record(), HttpStatus.OK);
    }
}
