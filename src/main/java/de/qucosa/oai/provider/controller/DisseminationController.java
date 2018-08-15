package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.dissemination.DisseminationApi;
import de.qucosa.oai.provider.api.format.FormatApi;
import de.qucosa.oai.provider.persitence.model.Dissemination;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RequestMapping("/dissemination")
@RestController
public class DisseminationController {

    @Autowired
    private DisseminationApi disseminationApi;

    @Autowired
    private FormatApi formatApi;

    @RequestMapping(value = "{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<Dissemination>> find(@PathVariable String uid) {
        List<Dissemination> disseminations = null;

        try {
            disseminations = disseminationApi.findAllByUid("recordid", uid);
        } catch (SQLException e) {
            return new ResponseEntity("Not disseminations found.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<List<Dissemination>>(disseminations, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Dissemination> save(@RequestBody String input) {
        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination = null;

        try {
            dissemination = om.readValue(input, Dissemination.class);
            dissemination = disseminationApi.saveDissemination(dissemination);
        } catch (IOException e) {
            return new ResponseEntity("Dissemination mapping failed.", HttpStatus.BAD_REQUEST);
        } catch (SQLException e) {
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
        Dissemination dissemination = null;

        try {
            Format format = formatApi.find("mdprefix", mdprefix);
            dissemination = disseminationApi.findByMultipleValues("formatid = %s AND recordid = %s", String.valueOf(format.getFormatId()), uid);
            dissemination.setDeleted(delete);
            dissemination = disseminationApi.deleteDissemination(dissemination);
        } catch (SQLException e) {
            return new ResponseEntity("Format with prefix " + mdprefix + " not found.", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(dissemination, HttpStatus.OK);
    }
}
