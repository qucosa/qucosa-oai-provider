package de.qucosa.oai.provider.data.objects;

import de.qucosa.oai.provider.persistence.pojos.Format;

public class FormatTestData {

    public static Long id = 1L;

    public static String mdprefix = "oai_dc";

    public static String schemaurl = "http://www.openarchives.org/OAI/2.0/oai_dc/";

    public static String namespace = "oai_dc";

    public static boolean deleted = false;

    public static Format format(){
        Format format = new Format();
        format.setId(id);
        format.setMdprefix(mdprefix);
        format.setSchemaUrl(schemaurl);
        format.setNamespace(namespace);
        format.setDeleted(deleted);
        return format;
    }
}
