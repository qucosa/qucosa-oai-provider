package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.QucosaOaiProviderApplication;
import de.qucosa.oai.provider.persitence.model.RecordTransport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import testdata.TestData;

import java.io.IOException;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = QucosaOaiProviderApplication.class)
@AutoConfigureMockMvc
public class RecordControllerTransportIT {

    private List<RecordTransport> rt = null;

    @Autowired
    private MockMvc mvc;

    private ObjectMapper om = new ObjectMapper();

    @Before
    public void setUp() throws IOException {
        rt = om.readValue(TestData.RECORDS_INPUT, om.getTypeFactory().constructCollectionType(List.class, RecordTransport.class));
    }

    @Test
    public void Save_record_input() throws Exception {
        mvc.perform(post("/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.RECORDS_INPUT))
                .andExpect(status().isOk());
    }
}
