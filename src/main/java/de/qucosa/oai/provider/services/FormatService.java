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
package de.qucosa.oai.provider.services;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.model.Format;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class FormatService {

    private Dao<Format> dao;

    public FormatService() {}

    public void setDao(Dao<Format> dao) {
        this.dao = dao;
    }

    public Format saveFormat(Format format) {
        return dao.saveAndSetIdentifier(format);
    }

    public Collection<Format> saveFormats(List<Format> formats) {
        return dao.saveAndSetIdentifier(formats);
    }

    public Format updateFormat(Format input, String mdprefix) {

        if (!input.getMdprefix().equals(mdprefix)) {
            //throw new UpdateFailed("Cannot update format.");
        }

        return dao.update(input);
    }

    public Format findById(String id) {
        return dao.findById(id);
    }

    public Collection<Format> find(String column, String value) {
        return dao.findByPropertyAndValue(column, value);
    }

    public Collection<Format> findAll() {
        return dao.findAll();
    }

    public void delete(Format format) {
        dao.delete(format);
    }
}
