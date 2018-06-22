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

import de.qucosa.oai.provider.application.config.DissTermsDao;
import de.qucosa.oai.provider.application.config.SetConfigDao;
import de.qucosa.oai.provider.application.mapper.SetsConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileNotFoundException;

public class ApplicationConfigListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        
    }

    @Override
    public void contextInitialized(ServletContextEvent sc) {
        ServletContext context = sc.getServletContext();

        try {
            DissTermsDao dissTerms = new DissTermsDao(context.getInitParameter("config.path"));
            context.setAttribute("dissConf", dissTerms);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            SetConfigDao setConfig = new SetConfigDao(context.getInitParameter("config.path"));
            context.setAttribute("sets", setConfig);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
