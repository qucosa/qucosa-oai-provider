/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qucosa.oai.provider.jersey.tests;

import java.net.URI;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;

public abstract class JerseyTestAbstract extends JerseyTest {
    @Context
    protected WebappContext appContext;
    
    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new TestContainerFactory() {
            @Override
            public TestContainer create(URI baseUri, DeploymentContext context) {
                final URI bu = baseUri;
                
                final DeploymentContext co = context;
                
                return new TestContainer() {
                    private HttpServer server = null;
                    
                    private URI baseU;
                    
                    @Override
                    public void stop() {
                        this.server.shutdownNow();
                    }
                    
                    @Override
                    public void start() {
                        this.baseU = UriBuilder.fromUri(bu).path(co.getContextPath()).build();
                        appContext = new WebappContext("testCt", co.getContextPath());
                        co.getResourceConfig().register(new AbstractBinder() {
                            
                            @Override
                            protected void configure() {
                                bind(appContext).to(ServletContext.class);
                            }
                        });
                        
                        this.server = GrizzlyHttpServerFactory.createHttpServer(this.baseU, co.getResourceConfig(), false);
                        appContext.deploy(this.server);
                        Map<String, Object> attrs = co.getResourceConfig().getProperties();
                        
                        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
                            appContext.setAttribute(entry.getKey(), entry.getValue());
                        }
                    }
                    
                    @Override
                    public ClientConfig getClientConfig() {
                        return null;
                    }
                    
                    @Override
                    public URI getBaseUri() {
                        return bu;
                    }
                };
            }
        };
    }
}
