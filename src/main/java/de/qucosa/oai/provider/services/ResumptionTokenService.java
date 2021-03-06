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
import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.persistence.exceptions.NotFound;
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.ResumptionToken;

public class ResumptionTokenService {
    private Dao<ResumptionToken> dao;

    public ResumptionTokenService() { }

    public void setDao(Dao<ResumptionToken> dao) {
        this.dao = dao;
    }

    public ResumptionToken saveAndSetIdentifier(ResumptionToken object) throws SaveFailed {
        return dao.saveAndSetIdentifier(object);
    }

    public ResumptionToken findById(String id) throws NotFound {
        return dao.findById(id);
    }

    public void delete() throws DeleteFailed {
        dao.delete();
    }
}
