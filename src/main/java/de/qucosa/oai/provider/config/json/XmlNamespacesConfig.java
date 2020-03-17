/*
 * Copyright 2019 Saxon State and University Library Dresden (SLUB)
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
package de.qucosa.oai.provider.config.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class XmlNamespacesConfig {
    private List<NamespaceMapper> mapper;

    public XmlNamespacesConfig(String path) throws IOException {
        this(new File(path));
    }

    public XmlNamespacesConfig(File file) throws IOException {
        this(new FileInputStream(file));
    }

    public XmlNamespacesConfig(InputStream stream) throws IOException {
        ObjectMapper om = new ObjectMapper();
        mapper = om.readValue(stream, om.getTypeFactory().constructCollectionType(List.class, NamespaceMapper.class));
    }

    public Map<String, String> getNamespaces() {
        Map<String, String> output = new HashMap<>();

        for (NamespaceMapper entry : mapper) {
            output.put(entry.getPrefix(), entry.getUrl());
        }

        return output;
    }

    @JsonAutoDetect
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NamespaceMapper implements Serializable {
        @JsonProperty("prefix")
        private String prefix;

        @JsonProperty("url")
        private String url;

        public String getPrefix() { return prefix; }

        public void setPrefix(String prefix) { this.prefix = prefix; }

        public String getUrl() { return url; }

        public void setUrl(String url) { this.url = url; }
    }
}
