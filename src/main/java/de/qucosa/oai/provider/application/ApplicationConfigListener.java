package de.qucosa.oai.provider.application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.application.mapper.DissTerms;
import de.qucosa.oai.provider.application.mapper.DissTerms.DissFormat;
import de.qucosa.oai.provider.application.mapper.DissTerms.DissTerm;
import de.qucosa.oai.provider.application.mapper.DissTerms.Term;
import de.qucosa.oai.provider.application.mapper.DissTerms.XmlNamspace;

public class ApplicationConfigListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        
    }

    @Override
    public void contextInitialized(ServletContextEvent sc) {
        ServletContext context = sc.getServletContext();
        context.setAttribute("dissConf", new DissTermsDao());
    }
    
    public static class DissTermsDao {
        private final Logger logger = LoggerFactory.getLogger(DissTermsDao.class);

        DissTerms dissTerms = null;

        public DissTermsDao() {
            ObjectMapper om = new ObjectMapper();
            File file = new File("/home/dseelig/opt/oaiprovider/config/dissemination-config.json");

            try {
                dissTerms = om.readValue(Files.readAllBytes(Paths.get(file.getAbsolutePath())), DissTerms.class);
            } catch (IOException e) {
                e.printStackTrace();
                logger.debug("dissemination-conf parse failed.");
            }
        }

        public Map<String, String> getMapXmlNamespaces() {
            HashSet<XmlNamspace> xmlNamespaces = (HashSet<XmlNamspace>) dissTerms.getXmlnamespacees();
            Map<String, String> map = new HashMap<>();

            for (XmlNamspace namspace : xmlNamespaces) {
                map.put(namspace.getPrefix(), namspace.getUrl());
            }

            return map;
        }

        public Set<XmlNamspace> getSetXmlNamespaces() {
            return xmlNamespaces();
        }

        public XmlNamspace getXmlNamespace(String prefix) {
            XmlNamspace xmlNamspace = null;

            for (XmlNamspace namespace : xmlNamespaces()) {

                if (namespace.getPrefix().equals(prefix)) {
                    xmlNamspace = namespace;
                }
            }

            return xmlNamspace;
        }

        public Term getTerm(String diss, String name) {
            HashSet<DissTerm> dissTerms = (HashSet<DissTerm>) this.dissTerms.getDissTerms();
            Term term = null;

            for (DissTerm dt : dissTerms) {

                if (!dt.getDiss().equals(diss)) {
                    logger.debug(diss + " is does not exists in dissemination-config.");
                    continue;
                }

                if (dt.getTerms().isEmpty()) {
                    logger.debug(diss + " has no terms config.");
                    continue;
                }

                for (Term t : dt.getTerms()) {

                    if (!t.getName().equals(name)) {
                        logger.debug("The term name " + name + " is not available in dissemination " + diss);
                        continue;
                    }

                    term = t;
                }

                if (term != null) {
                    break;
                }
            }

            return term;
        }
        
        public Set<DissFormat> dissFormats() {
            return dissTerms.getFormats();
        }
        
        public DissFormat dissFormat(String format) {
            DissFormat dissFormat = null;
            
            for (DissFormat df : dissTerms.getFormats()) {
                
                if (df.getFormat().equals(format)) {
                    dissFormat = df;
                    break;
                }
            }
            
            return dissFormat;
        }

        private Set<XmlNamspace> xmlNamespaces() {
            HashSet<XmlNamspace> xmlNamespaces = (HashSet<XmlNamspace>) dissTerms.getXmlnamespacees();
            return xmlNamespaces;
        }
    }
}
