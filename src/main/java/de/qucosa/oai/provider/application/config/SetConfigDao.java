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

package de.qucosa.oai.provider.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

public class SetConfigDao {

    private Logger logger = LoggerFactory.getLogger(DissTermsMapper.class);

    private List<SetConfigMapper.Set> mapping;

    private InputStream config;

    public SetConfigDao(String path) throws FileNotFoundException {
        this(new File(path));
    }

    public SetConfigDao(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public SetConfigDao(InputStream stream) {
        this.config = stream;

        ObjectMapper om = new ObjectMapper();

        try {
            mapping = om.readValue(stream, om.getTypeFactory().constructCollectionType(List.class, SetConfigMapper.Set.class));
        } catch (IOException e) {
            logger.error("Cannot parse list-set-conf JSON file.");
        }
    }

    public List<SetConfigMapper.Set> getSetObjects() { return mapping; }

    public SetConfigMapper.Set getSetObject(String setSpec) {
        SetConfigMapper.Set setObj = null;

        for (SetConfigMapper.Set obj : getSetObjects()) {

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
            SetConfigMapper.Set set = getSetObjects().get(i);
            setSpecs.add(set.getSetSpec());
        }

        return setSpecs;
    }
}
