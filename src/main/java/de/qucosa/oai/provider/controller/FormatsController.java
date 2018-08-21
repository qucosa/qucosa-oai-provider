package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.format.FormatApi;
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

@RequestMapping("/formats")
@RestController
public class FormatsController {
    @Autowired
    private FormatApi formatApi;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<Format>> findAll() {
        List<Format> formats;

        try {
            formats = formatApi.findAll();
        } catch (SQLException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<List<Format>>(formats, HttpStatus.OK);
    }

    @RequestMapping(value = "{mdprefix}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Format> find(@PathVariable String mdprefix) throws SQLException {
        Format format;

        try {
            format = formatApi.find("mdprefix", mdprefix);
        } catch (SQLException e) {
            throw new SQLException(e.getMessage(), e);
        }

        return new ResponseEntity<Format>(format, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public <T> ResponseEntity<T> save(@RequestBody String input) {
        T output;
        ObjectMapper om = new ObjectMapper();

        try {
            Format format = formatApi.saveFormat(om.readValue(input, Format.class));
            output = (T) format;
        } catch (IOException e) {

            try {
                List<Format> formats = formatApi.saveFormats(om.readValue(input, om.getTypeFactory().constructCollectionType(List.class, Format.class)));
                output = (T) formats;
            } catch (SQLException e1) {
                return new ResponseEntity(e1.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (IOException e1) {
                return new ResponseEntity(e1.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } catch (SQLException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<T>(output, HttpStatus.OK);
    }

    @RequestMapping(value = "{mdprefix}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Format> update(@RequestBody Format input, @PathVariable String mdprefix) {
        Format format;

        try {
            format = formatApi.updateFormat(input, mdprefix);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Format>(format, HttpStatus.OK);
    }

    @RequestMapping(value = "{mdprefix}/{value}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Format> delete(@PathVariable String mdprefix, @PathVariable boolean value) {
        Format format;

        try {
            format = formatApi.deleteFormat("mdprefix", mdprefix, value);
        } catch (SQLException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Format>(format, HttpStatus.OK);
    }
}
