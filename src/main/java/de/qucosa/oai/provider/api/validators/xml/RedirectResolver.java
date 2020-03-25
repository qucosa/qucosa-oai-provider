/*
 * *
 *     ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *     ~
 *     ~ Licensed under the Apache License, Version 2.0 (the "License");
 *     ~ you may not use this file except in compliance with the License.
 *     ~ You may obtain a copy of the License at
 *     ~
 *     ~     http://www.apache.org/licenses/LICENSE-2.0
 *     ~
 *     ~ Unless required by applicable law or agreed to in writing, software
 *     ~ distributed under the License is distributed on an "AS IS" BASIS,
 *     ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     ~ See the License for the specific language governing permissions and
 *     ~ limitations under the License.
 *
 */

package de.qucosa.oai.provider.api.validators.xml;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.xerces.dom.DOMInputImpl;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.net.URI;
import java.util.List;

public class RedirectResolver implements LSResourceResolver {
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        if (systemId == null) return null;

        try {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                final HttpGet httpGet = new HttpGet(systemId);
                final HttpClientContext context = HttpClientContext.create();
                httpClient.execute(httpGet, context);
                final List<URI> locations = context.getRedirectLocations();
                if (locations != null) {
                    URI finalUrl = locations.get(locations.size() - 1);
                    /*
                     WARNING DOMInputImpl is internal proprietary API and may be removed in a future release
                     But we have to use it. There is no choice.
                     */
                    final DOMInputImpl domInput = new DOMInputImpl();
                    // pass on the final URI for the next loading steps
                    domInput.setSystemId(finalUrl.toString());
                    return domInput;
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }
}
