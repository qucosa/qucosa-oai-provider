package de.qucosa.oai.provider.application;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.qucosa.oai.provider.application.mapper.DissTerms;

public class ApplicationConfigListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        
    }

    @Override
    public void contextInitialized(ServletContextEvent sc) {
        ServletContext context = sc.getServletContext();
        DissTerms dissTerms = new DissTerms(context.getInitParameter("config.path"));
        context.setAttribute("dissConf", dissTerms);
    }
}
