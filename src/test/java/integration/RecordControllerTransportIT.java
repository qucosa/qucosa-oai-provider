package integration;

import de.qucosa.oai.provider.QucosaOaiProviderApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import testdata.TestData;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = QucosaOaiProviderApplication.class)
@AutoConfigureMockMvc
public class RecordControllerTransportIT {

    @Autowired
    private MockMvc mvc;

    @Test
    public void Save_record_input() throws Exception {
        mvc.perform(post("/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestData.RECORDS_INPUT))
                .andExpect(status().isOk());
    }
}
