package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.sets.SetApi;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Set;
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

@RequestMapping("/sets")
@RestController
public class SetController {

    private Logger logger = LoggerFactory.getLogger(SetController.class);

    @Autowired
    private SetApi setApi;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Collection<Set>> findAll() {
        Collection<Set> sets;

        try {
            sets = setApi.findAll();
        } catch (NotFound e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Collection<Set>>(sets, HttpStatus.OK);
    }

    @RequestMapping(value = "{setspec}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Set> find(@PathVariable String setspec) {
        Set set;

        try {
            set = (Set) setApi.find("setspec", setspec).iterator().next();
        } catch (NotFound e) {
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
            Set set = setApi.saveSet(om.readValue(input, Set.class));
            output = (T) set;
        } catch (IOException e) {

            try {
                List<Set> sets = setApi.saveSets(om.readValue(input, om.getTypeFactory().constructCollectionType(List.class, Set.class)));
                output = (T) sets;
            } catch (SaveFailed e1) {
                logger.error("Cannot save set collections.", e1);
            } catch (IOException e1) {
                return new ResponseEntity("Cannot parse set input data.", HttpStatus.BAD_REQUEST);
            }
        } catch (SaveFailed e) {
            logger.error("Cannot save set object.", e);
        }

        if (output == null) {
            return new ResponseEntity("Cannot save set objects.", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity<T>(output, HttpStatus.OK);
    }

    @RequestMapping(value = "{setspec}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Set> update(@RequestBody Set input, @PathVariable String setspec) {
        Set set;

        try {
            set = setApi.updateSet(input, setspec);
        } catch (UpdateFailed e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }


        return new ResponseEntity<Set>(set, HttpStatus.OK);
    }

    @RequestMapping(value = "{setspec}/{delete}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String setspec, @PathVariable boolean delete) {
        int deleted;

        try {
            deleted = setApi.deleteSet("setspec", setspec, delete);
        } catch (DeleteFailed e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(deleted, HttpStatus.OK);
    }
}
