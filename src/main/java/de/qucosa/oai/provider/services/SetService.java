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
import de.qucosa.oai.provider.persistence.model.Set;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class SetService<T> {
    private Dao dao;

    public SetService() {}

    public void setDao(Dao<Set> dao) {
        this.dao = dao;
    }

    public Set saveSet(Set input) throws SaveFailed {
        return (Set) dao.saveAndSetIdentifier(input);
    }

    public List<Set> saveSets(List<Set> input) throws SaveFailed {
        return (List<Set>) dao.saveAndSetIdentifier(input);
    }

    public List<Set> findAll() throws NotFound {
        return (List<Set>) dao.findAll();
    }

    public Collection<Set> find(String column, String setspec) throws NotFound {
        return dao.findByPropertyAndValue(column, setspec);
    }

    public Set updateSet(Set input, String setspec) throws UpdateFailed {
        Set output;

        if (!input.getSetSpec().equals(setspec)) {
            throw new UpdateFailed("Cannot update set.");
        }

        output = (Set) dao.update(input);

        return output;
    }

    public void deleteSet(String setspec) throws DeleteFailed {
        dao.delete(setspec);
    }

    public void undoDeleteSet(String setspec) throws UndoDeleteFailed {
        dao.undoDelete(setspec);
    }
}
