package de.qucosa.oai.provider.controller;

import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Set;
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

import java.sql.SQLException;

@RequestMapping("/sets")
@RestController
public class SetController {
    @Autowired
    private Dao setDao;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Set> save(@RequestBody Set input) {
        SetApi setApi = new SetApi(setDao, input);
        Set set = null;

        try {
            set = setApi.saveSet();
        } catch (SQLException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Set>(set, HttpStatus.OK);
    }

    @RequestMapping(value = "{setspec}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Set> update(@RequestBody Set input, @PathVariable String setspec) {
        return new ResponseEntity<Set>(new Set(), HttpStatus.OK);
    }

    @RequestMapping(value = "{setspec}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String setspec) {
        return new ResponseEntity<Set>(HttpStatus.OK);
    }
}
