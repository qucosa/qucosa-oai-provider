package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.ErrorDetails;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Set;
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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RequestMapping("/sets")
@RestController
public class SetController {

    private Logger logger = LoggerFactory.getLogger(SetController.class);

    @Autowired
    private SetService setApi;

    @Autowired
    private ErrorDetails errorDetails;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Collection<Set>> findAll() {
        Collection<Set> sets;

        try {
            sets = setApi.findAll();
        } catch (NotFound e) {
            return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                    .setDate(LocalDateTime.now())
                    .setErrorMsg(e.getMessage())
                    .setException(e.getClass().getName())
                    .setRequestPath("/sets")
                    .setMethod("findAll")
                    .setRequestMethod("GET")
                    .setStatuscode(HttpStatus.NOT_FOUND.toString()), HttpStatus.NOT_FOUND);
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
            return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                    .setDate(LocalDateTime.now())
                    .setErrorMsg(e.getMessage())
                    .setException(e.getClass().getName())
                    .setRequestPath("/sets/{setspec}")
                    .setMethod("find")
                    .setRequestMethod("GET")
                    .setStatuscode(HttpStatus.NOT_FOUND.toString()), HttpStatus.NOT_FOUND);
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
                return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                        .setDate(LocalDateTime.now())
                        .setErrorMsg(e.getMessage())
                        .setException(e.getClass().getName())
                        .setRequestPath("/sets")
                        .setMethod("save")
                        .setRequestMethod("POST")
                        .setStatuscode(HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
            }
        } catch (SaveFailed e) {
            logger.error("Cannot save set object.", e);
        }

        if (output == null) {
            return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                    .setDate(LocalDateTime.now())
                    .setErrorMsg("Cannot save set objects.")
                    .setRequestPath("/sets")
                    .setMethod("save")
                    .setRequestMethod("POST")
                    .setStatuscode(HttpStatus.NOT_ACCEPTABLE.toString()), HttpStatus.NOT_ACCEPTABLE);
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
            return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                    .setDate(LocalDateTime.now())
                    .setException(UpdateFailed.class.getName())
                    .setErrorMsg(e.getMessage())
                    .setRequestPath("/sets/{setspec}")
                    .setMethod("update")
                    .setRequestMethod("PUT")
                    .setStatuscode(HttpStatus.NOT_ACCEPTABLE.toString()), HttpStatus.NOT_ACCEPTABLE);
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
            return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                    .setDate(LocalDateTime.now())
                    .setException(UpdateFailed.class.getName())
                    .setErrorMsg(e.getMessage())
                    .setRequestPath("/sets/{setspec}/{delete}")
                    .setMethod("delete")
                    .setRequestMethod("DELETE")
                    .setStatuscode(HttpStatus.NOT_ACCEPTABLE.toString()), HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity(deleted, HttpStatus.OK);
    }
}
