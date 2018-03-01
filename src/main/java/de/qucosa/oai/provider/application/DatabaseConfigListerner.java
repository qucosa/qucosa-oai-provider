package de.qucosa.oai.provider.application;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.qucosa.oai.provider.application.ApplicationConfigListener.DissTermsDao;
import de.qucosa.oai.provider.application.mapper.DissTerms.DissFormat;
import de.qucosa.oai.provider.application.mapper.DissTerms.DissTerm;
import de.qucosa.oai.provider.application.mapper.DissTerms.Term;
import de.qucosa.oai.provider.application.mapper.DissTerms.XmlNamspace;
import de.qucosa.oai.provider.persistence.Connect;
import de.qucosa.oai.provider.persistence.PersistenceServiceInterface;
import de.qucosa.oai.provider.persistence.pojos.DisseminationPredicate;
import de.qucosa.oai.provider.persistence.pojos.Format;
import de.qucosa.oai.provider.persistence.pojos.XmlNamespace;
import de.qucosa.oai.provider.persistence.postgres.DisseminationPredicateService;
import de.qucosa.oai.provider.persistence.postgres.DisseminationTermsService;
import de.qucosa.oai.provider.persistence.postgres.FormatService;
import de.qucosa.oai.provider.persistence.postgres.SetService;
import de.qucosa.oai.provider.persistence.postgres.XmlNamespaceService;
import de.qucosa.oai.provider.xml.utils.DocumentXmlUtils;

public class DatabaseConfigListerner implements ServletContextListener {
    private Connection connection = null;
    
    private PersistenceServiceInterface service = null;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        connection = new Connect("postgresql", "oaiprovider").connection();
        DissTermsDao dissTermsDao = (DissTermsDao) sce.getServletContext().getAttribute("dissConf");
        installSets();
        installNamespaces(dissTermsDao.getSetXmlNamespaces());
        installDissPredicates(dissTermsDao.getDissTerms());
        installFormats(dissTermsDao.formats());
        installDissTerms(dissTermsDao.getDissTerms());
        
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }
    
    private void installFormats(Set<DissFormat> dissFormats) {
        service = new FormatService();
        service.setConnection(connection);
        Set<Format> formats = new HashSet<>();
        
        for(DissFormat df : dissFormats) {
            Format format = new Format();
            format.setMdprefix(df.getFormat());
            format.setDissType(df.getDissType());
            format.setLastpolldate(new Timestamp(new Date().getTime()));
            formats.add(format);
        }
        
        if (!formats.isEmpty()) {
            service.update(formats);
        }
    }
    
    private void installSets() {
        service = new SetService();
        service.setConnection(connection);
        ObjectMapper om = new ObjectMapper();
        File setSpecs = new File("/home/dseelig/opt/oaiprovider/config/list-set-conf.json");
        Set<de.qucosa.oai.provider.persistence.pojos.Set> json = null;
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = new HashSet<>();
        
        try {
            json = om.readValue(setSpecs, om.getTypeFactory().constructCollectionType(Set.class, de.qucosa.oai.provider.persistence.pojos.Set.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (de.qucosa.oai.provider.persistence.pojos.Set set : json) {
            de.qucosa.oai.provider.persistence.pojos.Set saveSet = new de.qucosa.oai.provider.persistence.pojos.Set();
            saveSet.setSetSpec(set.getSetSpec());
            saveSet.setSetName(set.getSetName());
            saveSet.setPredicate(set.getPredicate());
            
            Document document = DocumentXmlUtils.document(null, true);
            Element root = document.createElement("set");
            
            Element setSpec = document.createElement("setSpec");
            setSpec.appendChild(document.createTextNode(set.getSetSpec()));
            root.appendChild(setSpec);
            
            Element setName = document.createElement("setName");
            setName.appendChild(document.createTextNode(set.getSetName()));
            root.appendChild(setName);
            
            document.appendChild(root);
            
            saveSet.setDocument(document);
            sets.add(saveSet);
        }
        
        service.update(sets);
    }
    
    private void installNamespaces(Set<XmlNamspace> sets) {
        service = new XmlNamespaceService();
        service.setConnection(connection);
        Set<XmlNamespace> namespaces = new HashSet<>();
        
        for (XmlNamspace ns : sets) {
            XmlNamespace save = new XmlNamespace();
            save.setPrefix(ns.getPrefix());
            save.setUrl(ns.getUrl());
            namespaces.add(save);
        }
        
        service.update(namespaces);
    }
    
    private void installDissPredicates(Set<DissTerm> terms) {
        service = new DisseminationPredicateService();
        service.setConnection(connection);
        Set<DisseminationPredicate> predicates = new HashSet<>();
        
        for (DissTerm dt : terms) {
            DisseminationPredicate predicate = new DisseminationPredicate();
            predicate.setPredicate(dt.getDiss());
            predicates.add(predicate);
        }
        
        service.update(predicates);
    }
    
    private void installDissTerms(Set<DissTerm> terms) {
        
        for (DissTerm dt : terms) {
            
            if (dt != null) {
                
                if (dt.getTerms() != null && !dt.getTerms().isEmpty()) {
                    
                    for (Term term : dt.getTerms()) {
                        service = new DisseminationTermsService();
                        service.setConnection(connection);
                        service.update(dt.getDiss(), term.getName(), term.getTerm().replace("'", "\""));
                    }
                }
            }
        }
    }
}
