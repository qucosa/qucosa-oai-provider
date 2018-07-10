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

package de.qucosa.oai.provider.application;

import de.qucosa.oai.provider.persistence.PersistenceDao;
import de.qucosa.oai.provider.persistence.postgres.DisseminationDao;
import de.qucosa.oai.provider.persistence.postgres.FormatDao;
import de.qucosa.oai.provider.persistence.postgres.RecordDao;
import de.qucosa.oai.provider.persistence.postgres.SetDao;
import de.qucosa.oai.provider.persistence.postgres.SetsToRecordDao;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

public class ApplicationBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(SetDao.class).to(PersistenceDao.class).in(RequestScoped.class);
        bind(RecordDao.class).to(PersistenceDao.class).in(RequestScoped.class);
        bind(FormatDao.class).to(PersistenceDao.class).in(RequestScoped.class);
        bind(DisseminationDao.class).to(PersistenceDao.class).in(RequestScoped.class);
        bind(SetsToRecordDao.class).to(PersistenceDao.class).in(RequestScoped.class);
    }
}
