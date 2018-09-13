package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.services.DisseminationService;
import de.qucosa.oai.provider.services.FormatService;
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
import java.time.LocalDateTime;
import java.util.Collection;

@RequestMapping("/dissemination")
@RestController
public class DisseminationController {

    private Logger logger = LoggerFactory.getLogger(FormatsController.class);

    @Autowired
    private ErrorDetails errorDetails;

    @Autowired
    private DisseminationService disseminationService;

    @Autowired
    private FormatService formatService;

    @RequestMapping(value = "{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity find(@PathVariable String uid) {
        Collection<Dissemination> disseminations;

        try {
            disseminations = disseminationService.findAllByUid("recordid", uid);
        } catch (NotFound e) {
            return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                    .setDate(LocalDateTime.now())
                    .setErrorMsg(e.getMessage())
                    .setException(e.getClass().getName())
                    .setRequestPath("/dissemination/{uid}")
                    .setMethod("find")
                    .setRequestMethod("GET")
                    .setStatuscode(HttpStatus.NOT_FOUND.toString()), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(disseminations, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) {
        ObjectMapper om = new ObjectMapper();
        Dissemination dissemination;

        try {
            dissemination = om.readValue(input, Dissemination.class);
            dissemination = disseminationService.saveDissemination(dissemination);
        } catch (IOException e) {
            return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                    .setDate(LocalDateTime.now())
                    .setErrorMsg(e.getMessage())
                    .setException(e.getClass().getName())
                    .setRequestPath("/dissemination")
                    .setMethod("save")
                    .setRequestMethod("POST")
                    .setStatuscode(HttpStatus.BAD_REQUEST.toString()), HttpStatus.BAD_REQUEST);
        } catch (SaveFailed e) {
            return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                    .setDate(LocalDateTime.now())
                    .setErrorMsg(e.getMessage())
                    .setException(e.getClass().getName())
                    .setRequestPath("/dissemination")
                    .setMethod("save")
                    .setRequestMethod("POST")
                    .setStatuscode(HttpStatus.NOT_ACCEPTABLE.toString()), HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity(dissemination, HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity update(@RequestBody Dissemination input, @PathVariable String uid) {
        // @todo clarify if is update the dissemination object meaningful.
        return new ResponseEntity(new Dissemination(), HttpStatus.OK);
    }

    @RequestMapping(value = "{uid}/{mdprefix}/{delete}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable String uid, @PathVariable String mdprefix, @PathVariable boolean delete) {
        Dissemination dissemination;

        try {
            Format format;

            try {
                format = (Format) formatService.find("mdprefix", mdprefix).iterator().next();
            } catch (NotFound fnf) {
                return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                        .setDate(LocalDateTime.now())
                        .setErrorMsg(fnf.getMessage())
                        .setException(fnf.getClass().getName())
                        .setRequestPath("/dissemination/{uid}/{mdprefix}/{delete}")
                        .setMethod("delete")
                        .setRequestMethod("DELETE")
                        .setStatuscode(HttpStatus.NOT_FOUND.toString()), HttpStatus.NOT_FOUND);
            }

            try {
                dissemination = disseminationService.findByMultipleValues("formatid = %s AND recordid = %s", String.valueOf(format.getFormatId()), uid);

                dissemination.setDeleted(delete);
                dissemination = disseminationService.deleteDissemination(dissemination);
            } catch (NotFound dnf) {
                return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                        .setDate(LocalDateTime.now())
                        .setErrorMsg(dnf.getMessage())
                        .setException(dnf.getClass().getName())
                        .setRequestPath("/dissemination/{uid}/{mdprefix}/{delete}")
                        .setMethod("delete")
                        .setRequestMethod("DELETE")
                        .setStatuscode(HttpStatus.NOT_FOUND.toString()), HttpStatus.NOT_FOUND);
            }
        } catch (DeleteFailed e) {
            return new ResponseEntity(errorDetails.setClassname(this.getClass().getName())
                    .setDate(LocalDateTime.now())
                    .setErrorMsg(e.getMessage())
                    .setException(e.getClass().getName())
                    .setRequestPath("/dissemination/{uid}/{mdprefix}/{delete}")
                    .setMethod("delete")
                    .setRequestMethod("DELETE")
                    .setStatuscode(HttpStatus.NOT_ACCEPTABLE.toString()), HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity(dissemination, HttpStatus.OK);
    }
}
