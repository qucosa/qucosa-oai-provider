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
import de.qucosa.oai.provider.persistence.exceptions.UpdateFailed;
import de.qucosa.oai.provider.persistence.model.Dissemination;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DisseminationService<T> {
    private Dao dao;

    public DisseminationService() {}

    public void setDao(Dao dao) {
        this.dao = dao;
    }

    public Dissemination saveDissemination(Dissemination dissemination) throws SaveFailed {
        return (Dissemination) dao.saveAndSetIdentifier(dissemination);
    }

    public Dissemination update(Dissemination dissemination) throws UpdateFailed {
        return (Dissemination) dao.update(dissemination);
    }

    public Dissemination findByMultipleValues(String clause, String... values) throws NotFound {
        return (Dissemination) dao.findByMultipleValues(clause, values);
    }

    public Collection<T> findByPropertyAndValue(String property, String value) throws NotFound {
        return dao.findByPropertyAndValue(property, value);
    }

    public void delete(Dissemination dissemination) throws DeleteFailed {
        dao.delete(dissemination);
    }
}
