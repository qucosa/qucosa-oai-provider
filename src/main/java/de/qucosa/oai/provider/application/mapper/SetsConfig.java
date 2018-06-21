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

package de.qucosa.oai.provider.application.mapper;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This is a json mapper class for mapping the list sets config file
 *
 * @author dseelig
 */
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetsConfig {
    @JsonIgnore
    private Logger logger = LoggerFactory.getLogger(DissTerms.class);

    @JsonIgnore
    private InputStream config;

    @JsonIgnore
    private SetSpecDao dao = null;

    @JsonProperty("sets")
    private List<Set> sets = new ArrayList<>();

    public List<Set> getSets() {
        return sets;
    }

    public <T> SetsConfig(@JsonProperty("config") T config) {
        if (config instanceof String) {
            this.config = getClass().getResourceAsStream((String) config);
        }

        if (config instanceof InputStream) {
            this.config = (InputStream) config;
        }

        if (config instanceof File) {

            try {
                this.config = new FileInputStream((File) config);
            } catch (FileNotFoundException e) {
                logger.error("list-set-conf.json is not found.", e);
            }
        }
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    @JsonIgnore
    public Set getSetObject(String setSpec) {
        return dao().getSetObject(setSpec);
    }

    @JsonIgnore
    public List<Set> getSetObjects() {
        return dao().getSetObjects();
    }

    @JsonIgnore
    public java.util.Set<String> getSetSpecs() {
        return dao().getSetSpecs();
    }

    private SetSpecDao dao() {

        if (dao == null) {
            dao = new SetSpecDao(config);
        }

        return dao;
    }

    public static class Set {
        @JsonProperty("setspec")
        private String setSpec;

        @JsonProperty("setname")
        private String setName;

        @JsonProperty("predicate")
        private String predicate;

        @JsonProperty("setdescription")
        private String setDescription;

        public String getSetSpec() {
            return setSpec;
        }

        public void setSetSpec(String setSpec) {
            this.setSpec = setSpec;
        }

        public String getSetName() {
            return setName;
        }

        public void setSetName(String setName) {
            this.setName = setName;
        }

        public String getPredicate() {
            return predicate;
        }

        public void setPredicate(String predicate) {
            this.predicate = predicate;
        }

        public String getSetDescription() { return setDescription; }

        public void setSetDescription(String setDescription) { this.setDescription = setDescription; }
    }

    private static class SetSpecDao {

        private final Logger logger = LoggerFactory.getLogger(SetSpecDao.class);

        private List<Set> sets = null;

        public SetSpecDao(InputStream stream) {
            ObjectMapper om = new ObjectMapper();

            try {
                sets = om.readValue(stream, om.getTypeFactory().constructCollectionType(List.class, Set.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public List<Set> getSetObjects() {
            return sets;
        }

        public Set getSetObject(String setSpec) {
            Set setObj = null;

            for (Set obj : getSetObjects()) {

                if (obj.getSetSpec().equals(setSpec)) {
                    setObj = obj;
                    break;
                }
            }

            return setObj;
        }

        public java.util.Set<String> getSetSpecs() {
            java.util.Set<String> setSpecs = new HashSet<String>();

            for (int i = 0; i < getSetObjects().size(); i++) {
                Set set = getSetObjects().get(i);
                setSpecs.add(set.getSetSpec());
            }

            return setSpecs;
        }
    }
}
