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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    private String configPath;

    @JsonIgnore
    private SetSpecDao dao = null;

    @JsonProperty("sets")
    private List<Set> sets = new ArrayList<>();

    public List<Set> getSets() {
        return sets;
    }

    public SetsConfig(@JsonProperty("configPath") String configPath) {
        this.configPath = configPath;
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
            dao = new SetSpecDao(configPath);
        }

        return dao;
    }

    public static class Set {
        @JsonProperty("setSpec")
        private String setSpec;

        @JsonProperty("setName")
        private String setName;

        @JsonProperty("predicate")
        private String predicate;

        @JsonProperty("setDescription")
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

        public SetSpecDao(String configPath) {
            ObjectMapper om = new ObjectMapper();
            File setSpecs = new File(configPath + "/list-set-conf.json");

            try {
                sets = om.readValue(setSpecs, om.getTypeFactory().constructCollectionType(List.class, Set.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public SetSpecDao(InputStream stream) {
            ObjectMapper om = new ObjectMapper();

            try {
                sets = om.readValue(stream, om.getTypeFactory().constructCollectionType(List.class, Set.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public SetSpecDao(File file) {
            ObjectMapper om = new ObjectMapper();

            try {
                sets = om.readValue(file, om.getTypeFactory().constructCollectionType(List.class, Set.class));
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
