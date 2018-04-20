package de.qucosa.oai.provider.configuration;

import org.junit.Before;
import org.junit.Test;

import de.qucosa.oai.provider.application.mapper.DissTerms;

public class DissTermsTest {
    private String path = "/home/opt/oaiprovider/config/";
    
    private DissTerms dissTerms = null;
    
    @Before
    public void setUp() {
        dissTerms = new DissTerms(path);
    }
    
    @Test
    public void Get_config_xml_namespaces_set() {
        dissTerms.getSetXmlNamespaces();
    }
}
