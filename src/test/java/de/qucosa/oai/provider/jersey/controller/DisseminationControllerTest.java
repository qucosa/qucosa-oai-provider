package de.qucosa.oai.provider.jersey.controller;

import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.controller.RecordController;
import de.qucosa.oai.provider.data.objects.DisseminationTestData;
import de.qucosa.oai.provider.mock.repositories.PsqlRepository;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import de.qucosa.oai.provider.persistence.pojos.Dissemination;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.core.Application;
import java.sql.SQLException;
import java.util.HashMap;

import static org.mockito.Mockito.mock;

public class DisseminationControllerTest extends JerseyTest {

    @Override
    protected Application configure() {
        PersistenceDaoInterface psqRepoDao = mock(DisseminationControllerTest.DisseminationTestDao.class);
        RecordController recordController = new RecordController(psqRepoDao);

        ResourceConfig config = new ResourceConfig(RecordController.class);
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(DisseminationControllerTest.DisseminationTestDao.class).to(PersistenceDaoInterface.class).in(RequestScoped.class);
            }
        });
        HashMap<String, Object> props = new HashMap<>();
        props.put("dissConf", new DissTerms("/home/opt/oaiprovider/config/"));
        config.setProperties(props);
        return config;
    }

    public static class DisseminationTestDao extends PsqlRepository {

        @Override
        public <T> T update(T object) throws SQLException {
            Dissemination dissemination = DisseminationTestData.dissemination();

            if (dissemination.getFormatId() == null) {
                throw new SQLException("Format ID in dissemination object failed.");
            }

            if (dissemination.getRecordId() == null) {
                throw new SQLException("Format ID in dissemination object failed.");
            }

            return (T) dissemination;
        }
    }
}
