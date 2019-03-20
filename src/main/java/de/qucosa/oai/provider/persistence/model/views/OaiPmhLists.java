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

package de.qucosa.oai.provider.persistence.model.views;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.qucosa.oai.provider.persistence.model.HasIdentifier;

import java.io.Serializable;
import java.sql.Timestamp;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class OaiPmhLists implements Serializable, HasIdentifier {
    @JsonProperty("rstId")
    private String rstId;

    @JsonProperty("expirationDate")
    private Timestamp expirationDate;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("recordId")
    private Long recordId;

    @JsonProperty("recordStatus")
    private boolean recordStatus;

    @JsonProperty("")
    private Timestamp lastModDate;

    @JsonProperty("xmldata")
    private String xmldata;

    @JsonProperty("disseminationStatus")
    private boolean disseminationStatus;

    @JsonProperty("format")
    private Long format;

    public String getRstId() {
        return rstId;
    }

    public void setRstId(String rstId) {
        this.rstId = rstId;
    }

    public Timestamp getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Timestamp expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public boolean isRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(boolean recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Timestamp getLastModDate() {
        return lastModDate;
    }

    public void setLastModDate(Timestamp lastModDate) {
        this.lastModDate = lastModDate;
    }

    public String getXmldata() {
        return xmldata;
    }

    public void setXmldata(String xmldata) {
        this.xmldata = xmldata;
    }

    public boolean isDisseminationStatus() {
        return disseminationStatus;
    }

    public void setDisseminationStatus(boolean disseminationStatus) {
        this.disseminationStatus = disseminationStatus;
    }

    public Long getFormat() {
        return format;
    }

    public void setFormat(Long format) {
        this.format = format;
    }

    @Override
    @JsonIgnore
    public void setIdentifier(Object identifier) {

    }

    @Override
    @JsonIgnore
    public Object getIdentifier() {
        return null;
    }
}
