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
package de.qucosa.oai.provider.services;

import de.qucosa.oai.provider.persistence.Dao;
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.exceptions.UndoDeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Format;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class FormatService<T> {

    private Dao dao;

    public FormatService() {}

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public Format saveFormat(Format format) throws SaveFailed {
        return (Format) dao.saveAndSetIdentifier(format);
    }

    public Collection<Format> saveFormats(List<Format> formats) throws SaveFailed {
        return dao.saveAndSetIdentifier(formats);
    }

    public Format updateFormat(Format input, String mdprefix) throws UpdateFailed {

        if (!input.getMdprefix().equals(mdprefix)) {
            throw new UpdateFailed("Cannot update format.");
        }

        return (Format) dao.update(input);
    }

    public Collection<Format> find(String column, String value) throws NotFound {
        return dao.findByPropertyAndValue(column, value);
    }

    public List<Format> findAll() throws NotFound {
        return (List<Format>) dao.findAll();
    }

    public void deleteFormat(String ident) throws DeleteFailed {
        dao.delete(ident);
    }

    public void undoDeleteFormat(String ident) throws UndoDeleteFailed {
        dao.undoDelete(ident);
    }
}
