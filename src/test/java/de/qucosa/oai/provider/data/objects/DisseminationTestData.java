package de.qucosa.oai.provider.data.objects;

import de.qucosa.oai.provider.persistence.pojos.Dissemination;

import java.sql.Timestamp;

public class DisseminationTestData {

    public static Long id = 1L;

    public static Long formatid = 1L;

    public static Long recordid = 1L;

    public static Timestamp lastmoddate;

    public static String xmldata;

    public static boolean deleted = false;

    public static Dissemination dissemination() {
        Dissemination dissemination = new Dissemination();
        dissemination.setId(id);
        dissemination.setRecordId(recordid);
        dissemination.setFormatId(formatid);
        dissemination.setLastmoddate(lastmoddate);
        dissemination.setXmldata(xmldata);
        dissemination.setDeleted(deleted);
        return dissemination;
    }
}
