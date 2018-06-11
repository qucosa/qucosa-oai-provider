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

package de.qucosa.oai.provider.persistence.pojos;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Format {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("mdprefix")
    private String mdprefix;

    @JsonProperty("schemaUrl")
    private String schemaUrl;

    @JsonProperty("namespace")
    private String namespace;

    @JsonProperty("deleted")
    private boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMdprefix() {
        return mdprefix;
    }

    public void setMdprefix(String mdprefix) {
        this.mdprefix = mdprefix;
    }

    public String getSchemaUrl() { return schemaUrl; }

    public void setSchemaUrl(String schemaUrl) { this.schemaUrl = schemaUrl; }

    public String getNamespace() { return namespace; }

    public void setNamespace(String namespace) { this.namespace = namespace; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
