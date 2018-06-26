package de.qucosa.oai.provider.helper;

import de.qucosa.oai.provider.application.config.DissTermsDao;
import de.qucosa.oai.provider.application.config.SetConfigDao;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class RestControllerContainerFactory implements TestContainerFactory {

    public static class RestTestContainer implements TestContainer {

        private URI baseUri;

        private HttpServer server;

        public RestTestContainer(URI baseUri, DeploymentContext deploymentContext) {
            this.baseUri = UriBuilder.fromUri(baseUri).path(deploymentContext.getContextPath()).build();

            WebappContext webappContext = new WebappContext("TestContext", deploymentContext.getContextPath());
            deploymentContext.getResourceConfig().register(new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(webappContext).to(ServletContext.class);
                }
            });

            this.server = GrizzlyHttpServerFactory.createHttpServer(this.baseUri, deploymentContext.getResourceConfig(), false);
            webappContext.setAttribute("dissConf", new DissTermsDao(getClass().getResourceAsStream("/config/dissemination-config.json")));
            webappContext.setAttribute("sets", new SetConfigDao(getClass().getResourceAsStream("/config/list-set-conf.json")));
            webappContext.deploy(this.server);
        }

        @Override
        public ClientConfig getClientConfig() {
            return null;
        }

        @Override
        public URI getBaseUri() {
            return baseUri;
        }

        @Override
        public void start() {

            if (!server.isStarted()) {
                try {
                    server.start();

                    if(baseUri.getPort() == 0) {
                        baseUri = UriBuilder.fromUri(baseUri).port(server.getListener("grizzly").getPort()).build();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void stop() {

            if (server.isStarted()) {
                server.shutdownNow();
            }
        }
    }

    @Override
    public TestContainer create(URI baseUri, DeploymentContext deploymentContext) {
        return new RestTestContainer(baseUri, deploymentContext);
    }
}
