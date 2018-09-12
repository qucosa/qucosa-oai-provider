package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.services.DisseminationApi;
import de.qucosa.oai.provider.services.FormatApi;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import de.qucosa.oai.provider.persistence.model.Format;
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

@RequestMapping("/dissemination")
@RestController
public class DisseminationController {

    @Autowired
    private DisseminationApi disseminationApi;

    @Autowired
    private FormatApi formatApi;

    @RequestMapping(value = "{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Collection<Dissemination>> find(@PathVariable String uid) {
        Collection<Dissemination> disseminations;

        try {
            disseminations = disseminationApi.findAllByUid("recordid", uid);
        } catch (NotFound e) {
            return new ResponseEntity("Not disseminations found.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Collection<Dissemination>>(disseminations, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Dissemination> save(@RequestBody String input) {
        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination;

        try {
            dissemination = om.readValue(input, Dissemination.class);
            dissemination = disseminationApi.saveDissemination(dissemination);
        } catch (IOException e) {
            return new ResponseEntity("Dissemination mapping failed.", HttpStatus.BAD_REQUEST);
        } catch (SaveFailed e) {
            return new ResponseEntity("Dissemination cannot save.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Dissemination>(dissemination, HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Dissemination> update(@RequestBody Dissemination input, @PathVariable String uid) {
        return new ResponseEntity<Dissemination>(new Dissemination(), HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}/{mdprefix}/{delete}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String uid, @PathVariable String mdprefix, @PathVariable boolean delete) {
        Dissemination dissemination;

        try {
            Format format;

            try {
                format = (Format) formatApi.find("mdprefix", mdprefix).iterator().next();
            } catch (NotFound notFound) {
                return new ResponseEntity("Format with prefix " + mdprefix + " not found.", HttpStatus.BAD_REQUEST);
            }

            try {
                dissemination = disseminationApi.findByMultipleValues("formatid = %s AND recordid = %s", String.valueOf(format.getFormatId()), uid);

                dissemination.setDeleted(delete);
                dissemination = disseminationApi.deleteDissemination(dissemination);
            } catch (NotFound notFound) {
                return new ResponseEntity("Dissemination not found.", HttpStatus.NOT_FOUND);
            }
        } catch (DeleteFailed e) {
            return new ResponseEntity("Dissemination cannot delete.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(dissemination, HttpStatus.OK);
    }
}
