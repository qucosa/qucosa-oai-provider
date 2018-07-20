package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.api.record.RecordApi;
import de.qucosa.oai.provider.persitence.Dao;
import de.qucosa.oai.provider.persitence.model.Format;
import de.qucosa.oai.provider.persitence.model.RecordTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RequestMapping("/records")
@RestController
public class RecordController {
    @Autowired
    private Dao recordDao;

    @Autowired
    private RecordApi recordApi;

    @Autowired
    private Environment environment;

    @Autowired
    private FormatsController formatsController;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity save(@RequestBody String input) {
        ObjectMapper om = new ObjectMapper();

        try {
            List<RecordTransport> inputData = om.readValue(input, om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));

            if (inputData == null || inputData.size() == 0) {
                return new ResponseEntity("Record transport mapping failed.", HttpStatus.BAD_REQUEST);
            }

            for (RecordTransport rt : inputData) {
                Format format = null;

                try {
                    ResponseEntity<Format> formatRE = formatsController.find(rt.getFormat().getMdprefix());
                    format = formatRE.getBody();
                } catch (SQLException e) {
                    ResponseEntity<Format> formatRE = formatsController.save(om.writeValueAsString(rt.getFormat()));
                    format = formatRE.getBody();
                }


            }
        } catch (IOException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }


}
