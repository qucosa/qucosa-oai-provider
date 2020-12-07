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
package de.qucosa.oai.provider.persistence.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class Record implements Serializable, HasIdentifier {
    @JsonProperty("recordId")
    private Long recordId;

    @JsonProperty("oaiID")
    private String oaiID;

    @JsonProperty("pid")
    private String pid;

    @JsonProperty("deleted")
    private boolean deleted;

    @JsonProperty("visible")
    private boolean visible;

    @Override
    public void setIdentifier(Object identifier) {
        setRecordId(Long.valueOf(identifier.toString()));
    }

    @Override
    @JsonIgnore
    public Object getIdentifier() {
        return getRecordId();
    }

    public Long getRecordId() { return recordId; }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getOaiID() {
        return oaiID;
    }

    public void setOaiID(String oaiID) {
        this.oaiID = oaiID;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
