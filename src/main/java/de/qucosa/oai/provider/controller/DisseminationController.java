package de.qucosa.oai.provider.controller;

import de.qucosa.oai.provider.persitence.model.Dissemination;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/dissemination")
@RestController
public class DisseminationController {
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Dissemination> save(@RequestBody Dissemination input) {
        return new ResponseEntity<Dissemination>(new Dissemination(), HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Dissemination> update(@RequestBody Dissemination input, @PathVariable String uid) {
        return new ResponseEntity<Dissemination>(new Dissemination(), HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Dissemination> find(@PathVariable String uid) {
        return new ResponseEntity<Dissemination>(new Dissemination(), HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String uid) {
        return new ResponseEntity(HttpStatus.OK);
    }
}
