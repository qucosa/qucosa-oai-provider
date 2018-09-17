package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.ErrorDetails;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Format;
import de.qucosa.oai.provider.services.FormatService;
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

@RequestMapping("/formats")
@RestController
public class FormatsController {

    private Logger logger = LoggerFactory.getLogger(FormatsController.class);

    @Autowired
    private FormatService formatService;

    @Autowired
    private ErrorDetails errorDetails;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity findAll() {
        List<Format> formats;

        try {
            formats = formatService.findAll();
        } catch (NotFound e) {
            return errorDetails.create(this.getClass().getName(), "findAll", "GET:formats",
                    HttpStatus.NOT_FOUND, "", e).response();
        }

        return new ResponseEntity(formats, HttpStatus.OK);
    }

    @RequestMapping(value = "{mdprefix}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity find(@PathVariable String mdprefix) {
        Collection<Format> formats;

        try {
            formats = formatService.find("mdprefix", mdprefix);

            if (formats == null) {return errorDetails.create(this.getClass().getName(), "find", "GET:formats/" + mdprefix,
                    HttpStatus.NOT_FOUND, "Cannot find format.", null).response();
            }
        } catch (NotFound e) {
            return errorDetails.create(this.getClass().getName(), "find", "GET:formats/" + mdprefix,
                    HttpStatus.NOT_FOUND, "", e).response();
        }

        return new ResponseEntity(formats.iterator().next(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public <T> ResponseEntity save(@RequestBody String input) {
        T output = null;
        ObjectMapper om = new ObjectMapper();

        try {
            Format format = formatService.saveFormat(om.readValue(input, Format.class));
            output = (T) format;
        } catch (IOException e) {

            try {
                Collection<Format> formats = formatService.saveFormats(om.readValue(input, om.getTypeFactory().constructCollectionType(List.class, Format.class)));
                output = (T) formats;
            } catch (IOException e1) {
                return errorDetails.create(this.getClass().getName(), "save", "POST:formats",
                        HttpStatus.BAD_REQUEST, "", e).response();
            } catch (SaveFailed saveFailed) {
                return errorDetails.create(this.getClass().getName(), "save", "POST:formats",
                        HttpStatus.NOT_ACCEPTABLE, "", saveFailed).response();
            }
        } catch (SaveFailed e) {
            return errorDetails.create(this.getClass().getName(), "save", "POST:formats",
                    HttpStatus.NOT_ACCEPTABLE, "", e).response();
        }

        return new ResponseEntity(output, HttpStatus.OK);
    }

    @RequestMapping(value = "{mdprefix}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(@RequestBody Format input, @PathVariable String mdprefix) {
        Format format;

        try {
            format = formatService.updateFormat(input, mdprefix);
        } catch (UpdateFailed e) {
            return errorDetails.create(this.getClass().getName(), "update", "PUT:formats/" + mdprefix,
                    HttpStatus.NOT_ACCEPTABLE, "", e).response();
        }

        return new ResponseEntity(format, HttpStatus.OK);
    }

    @RequestMapping(value = "{mdprefix}/{value}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String mdprefix, @PathVariable boolean value) {
        int deleted;

        try {
            deleted = formatService.deleteFormat("mdprefix", mdprefix, value);
        } catch (DeleteFailed e) {
            return errorDetails.create(this.getClass().getName(), "delete", "DELETE:formats/" + mdprefix + "/" + value,
                    HttpStatus.NOT_ACCEPTABLE, "", e).response();
        }

        return new ResponseEntity(deleted, HttpStatus.OK);
    }
}
