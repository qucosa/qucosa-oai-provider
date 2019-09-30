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
import de.qucosa.oai.provider.persistence.exceptions.SaveFailed;
import de.qucosa.oai.provider.persistence.model.RstToIdentifiers;

import java.util.Collection;

public class RstToIdentifiersService {
    private Dao<RstToIdentifiers> dao;

    public RstToIdentifiersService(){}

    public void setDao(Dao<RstToIdentifiers> dao) {
        this.dao = dao;
    }

    public Collection<RstToIdentifiers> saveAndSetIdentifier(Collection<RstToIdentifiers> objects) throws SaveFailed {
        return dao.saveAndSetIdentifier(objects);
    }
}
