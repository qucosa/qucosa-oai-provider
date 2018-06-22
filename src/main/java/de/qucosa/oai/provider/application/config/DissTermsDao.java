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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DissTermsDao {

    private Logger logger = LoggerFactory.getLogger(DissTermsMapper.class);

    private InputStream config;

    private DissTermsMapper mapping;

    public DissTermsDao(String path) throws FileNotFoundException {
        this(new File(path));
    }

    public DissTermsDao(File file) throws FileNotFoundException {
        this(new FileInputStream(file));
    }

    public DissTermsDao(InputStream stream) {
        this.config = stream;

        ObjectMapper om = new ObjectMapper();

        try {
            mapping = om.readValue(config, DissTermsMapper.class);
        } catch (IOException e) {
            logger.error("Cannot parse dissemination-conf JSON file.");
        }
    }

    public Map<String, String> getMapXmlNamespaces() {
        HashSet<DissTermsMapper.XmlNamespace> xmlNamespaces = (HashSet<DissTermsMapper.XmlNamespace>) mapping.getXmlnamespaces();
        Map<String, String> map = new HashMap<>();

        for (DissTermsMapper.XmlNamespace namspace : xmlNamespaces) {
            map.put(namspace.getPrefix(), namspace.getUrl());
        }

        return map;
    }

    public Set<DissTermsMapper.XmlNamespace> getSetXmlNamespaces() { return mapping.getXmlnamespaces(); }

    public DissTermsMapper.XmlNamespace getXmlNamespace(String prefix) {
        DissTermsMapper.XmlNamespace xmlNamspace = null;

        for (DissTermsMapper.XmlNamespace namespace : mapping.getXmlnamespaces()) {

            if (namespace.getPrefix().equals(prefix)) {
                xmlNamspace = namespace;
            }
        }

        return xmlNamspace;
    }

    public DissTermsMapper.Term getTerm(String diss, String name) {
        HashSet<DissTermsMapper.DissTerm> dissTerms = (HashSet<DissTermsMapper.DissTerm>) mapping.getDissTerms();
        DissTermsMapper.Term term = null;

        for (DissTermsMapper.DissTerm dt : dissTerms) {

            if (!dt.getDiss().equals(diss)) {
                logger.error(diss + " is does not exists in dissemination-config.");
                continue;
            }

            if (dt.getTerms().isEmpty()) {
                logger.error(diss + " has no terms config.");
                continue;
            }

            for (DissTermsMapper.Term t : dt.getTerms()) {

                if (!t.getName().equals(name)) {
                    logger.error("The term name " + name + " is not available in dissemination " + diss);
                    continue;
                }

                term = t;
            }

            if (term != null) {
                break;
            }
        }

        return term;
    }

    public Set<DissTermsMapper.DissFormat> getFormats() { return mapping.getFormats(); }

    public DissTermsMapper.DissFormat getFormat(String format) {
        DissTermsMapper.DissFormat dissFormat = null;

        for (DissTermsMapper.DissFormat df : mapping.getFormats()) {

            if (df.getMdprefix().equals(format)) {
                dissFormat = df;
                break;
            }
        }

        return dissFormat;
    }

}
