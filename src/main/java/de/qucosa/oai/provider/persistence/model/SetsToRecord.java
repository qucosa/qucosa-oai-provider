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

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SetsToRecord implements HasIdentifier {
    private Long idSet;

    private Long idRecord;

    public Long getIdSet() {
        return idSet;
    }

    public void setIdSet(Long idSet) {
        this.idSet = idSet;
    }

    public Long getIdRecord() {
        return idRecord;
    }

    public void setIdRecord(Long idRecord) {
        this.idRecord = idRecord;
    }

    @Override
    public void setIdentifier(Object identifier) {

    }

    @Override
    @JsonIgnore
    public Object getIdentifier() {
        return null;
    }
}
