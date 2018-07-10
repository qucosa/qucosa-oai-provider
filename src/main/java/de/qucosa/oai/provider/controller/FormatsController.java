package de.qucosa.oai.provider.controller;

import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Format;
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

@RequestMapping("/formats")
@RestController
public class FormatsController {
    @Autowired
    private Dao formatDao;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Format> save(@RequestBody Format input) {
        return new ResponseEntity<Format>(new Format(), HttpStatus.OK);
    }

    @RequestMapping(value = "{mdprefix}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Format> update(@RequestBody Format input, @PathVariable String mdprefix) {
        return new ResponseEntity<Format>(new Format(), HttpStatus.OK);
    }

    @RequestMapping(value = "{mdprefix}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String mdprefix) {
        return new ResponseEntity(HttpStatus.OK);
    }
}
