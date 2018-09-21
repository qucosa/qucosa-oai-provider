/**
 ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 */
package de.qucosa.oai.provider.persistence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;
import java.sql.Timestamp;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Dissemination extends ResourceSupport implements Serializable, HasIdentifier {
    @JsonProperty("dissid")
    private Long dissId;

    @JsonProperty("recordid")
    private String recordId;

    @JsonProperty("formatid")
    private Long formatId;

    @JsonProperty("lastmoddate")
    private Timestamp lastmoddate;

    @JsonProperty("xmldata")
    private String xmldata;

    @JsonProperty("deleted")
    private boolean deleted;

    public Long getDissId() { return dissId; }

    public void setDissId(Long dissId) { this.dissId = dissId; }

    public String getRecordId() { return recordId; }

    public void setRecordId(String recordId) { this.recordId = recordId; }

    public Long getFormatId() { return formatId; }

    public void setFormatId(Long formatId) { this.formatId = formatId; }

    public Timestamp getLastmoddate() { return lastmoddate; }

    public void setLastmoddate(Timestamp lastmoddate) { this.lastmoddate = lastmoddate; }

    public String getXmldata() { return xmldata; }

    public void setXmldata(String xmldata) { this.xmldata = xmldata; }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    @Override
    public void setIdentifier(Object identifier) {
        setDissId(Long.parseLong(String.valueOf(identifier)));
    }

    @Override
    @JsonIgnore
    public Object getIdentifier() {
        return getDissId();
    }
}
