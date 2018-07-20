package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.sets.SetApi;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RequestMapping("/sets")
@RestController
public class SetController {
    @Autowired
    private SetApi setApi;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<Set>> findAll() {
        List<Set> sets = null;

        try {
            sets = setApi.findAll();
        } catch (SQLException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<List<Set>>(sets, HttpStatus.OK);
    }

    @RequestMapping(value = "{setspec}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Set> find(@PathVariable String setspec) {
        Set set = null;

        try {
            set = setApi.find("setspec", setspec);
        } catch (SQLException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<Set>(set, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public <T> ResponseEntity<T> save(@RequestBody String input) {
        T output = null;
        ObjectMapper om = new ObjectMapper();

        try {
            Set set = setApi.saveSet((Set) om.readValue(input, Set.class));
            output = (T) set;
        } catch (IOException e) {

            try {
                List<Set> sets = setApi.saveSets(om.readValue(input, om.getTypeFactory().constructCollectionType(List.class, Set.class)));
                output = (T) sets;
            } catch (SQLException e1) {
                return new ResponseEntity(e1.getMessage(), HttpStatus.NOT_ACCEPTABLE);
            } catch (IOException e1) {

            }
        } catch (SQLException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<T>(output, HttpStatus.OK);
    }

    @RequestMapping(value = "{setspec}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Set> update(@RequestBody Set input, @PathVariable String setspec) {
        Set set = null;

        try {
            set = setApi.updateSet(input, setspec);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }


        return new ResponseEntity<Set>(set, HttpStatus.OK);
    }

    @RequestMapping(value = "{setspec}/{delete}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Set> delete(@PathVariable String setspec, @PathVariable boolean delete) {
        Set deleted;

        try {
            deleted = setApi.deleteSet("setspec", setspec, delete);
        } catch (SQLException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Set>(deleted, HttpStatus.OK);
    }
}
