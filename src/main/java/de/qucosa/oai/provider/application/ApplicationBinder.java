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

import de.qucosa.oai.provider.persistence.postgres.DisseminationService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import de.qucosa.oai.provider.persistence.postgres.FormatService;
import de.qucosa.oai.provider.persistence.postgres.RecordService;
import de.qucosa.oai.provider.persistence.postgres.SetService;

public class ApplicationBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(SetService.class).to(SetService.class);
        bind(RecordService.class).to(RecordService.class);
        bind(FormatService.class).to(FormatService.class);
        bind(DisseminationService.class).to(DisseminationService.class);
    }
}
