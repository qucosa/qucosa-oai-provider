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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.qucosa.oai.provider.persistence.model.HasIdentifier;
import de.qucosa.oai.provider.persistence.model.Set;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

// this is an json data mapper
@SuppressWarnings("unused")
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class OaiPmhList implements Serializable, HasIdentifier {

    // column record_id
    @JsonProperty("recordId")
    private Long recordId;

    // column uid
    @JsonProperty("uid")
    private String uid;

    // column pid
    @JsonProperty("pid")
    private String pid;

    // column record_status
    @JsonProperty("recordStatus")
    private boolean recordStatus;

    // column format_id
    @JsonProperty("format")
    private Long format;

    // column mdprefix
    @JsonProperty("mdprefix")
    private String mdprefix;

    // column lastmoddate
    @JsonProperty("lastModDate")
    private Timestamp lastModDate;

    // column xmldata
    @JsonProperty("xmldata")
    private String xmldata;

    // column diss_status
    @JsonProperty("dissStatus")
    private boolean dissStatus;

    @JsonProperty("sets")
    private Collection<Set> sets;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(boolean recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Long getFormat() {
        return format;
    }

    public void setFormat(Long format) {
        this.format = format;
    }

    public String getMdprefix() {
        return mdprefix;
    }

    public void setMdprefix(String mdprefix) {
        this.mdprefix = mdprefix;
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

    public boolean isDissStatus() {
        return dissStatus;
    }

    public void setDissStatus(boolean dissStatus) {
        this.dissStatus = dissStatus;
    }

    public Collection<Set> getSets() {
        return sets;
    }

    public void setSets(Collection<Set> sets) {
        this.sets = sets;
    }

    @Override
    public void setIdentifier(Object identifier) {

    }

    @Override
    public Object getIdentifier() {
        return null;
    }
}
