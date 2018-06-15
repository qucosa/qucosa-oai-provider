package de.qucosa.oai.provider.data.objects;

import de.qucosa.oai.provider.persistence.pojos.Record;

public class RecordTestData {

    public static Long id = 1L;

    public static String pid = "qucosa:55887";

    public static String uid = "oai:example:org:qucosa:55887";

    public static boolean deleted = false;

    public static Record record() {
        Record record = new Record();
        record.setId(id);
        record.setPid(pid);
        record.setUid(uid);
        record.setDeleted(deleted);
        return record;
    }
}
