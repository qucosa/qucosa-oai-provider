/*
 * *
 *     ~ Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *     ~
 *     ~ Licensed under the Apache License, Version 2.0 (the "License");
 *     ~ you may not use this file except in compliance with the License.
 *     ~ You may obtain a copy of the License at
 *     ~
 *     ~     http://www.apache.org/licenses/LICENSE-2.0
 *     ~
 *     ~ Unless required by applicable law or agreed to in writing, software
 *     ~ distributed under the License is distributed on an "AS IS" BASIS,
 *     ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     ~ See the License for the specific language governing permissions and
 *     ~ limitations under the License.
 *
 */

package de.qucosa.oai.provider.schedulers;

import de.qucosa.oai.provider.persistence.exceptions.DeleteFailed;
import de.qucosa.oai.provider.services.ResumptionTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResumptionTokenScheduler {
    private Logger logger = LoggerFactory.getLogger(ResumptionTokenScheduler.class);

    private ResumptionTokenService tokenService;

    @Autowired
    public ResumptionTokenScheduler(ResumptionTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Scheduled(fixedRate = 5000)
    public void reportCronAct() {

        if (tokenService != null) {
            logger.info("Token Service is da.");

            try {
                tokenService.delete();
            } catch (DeleteFailed deleteFailed) {
                logger.error(deleteFailed.getMessage(), deleteFailed);
            }
        }
    }
}
