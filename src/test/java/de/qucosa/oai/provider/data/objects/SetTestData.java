package de.qucosa.oai.provider.data.objects;

import de.qucosa.oai.provider.application.mapper.SetsConfig;

public class SetTestData {
    public static String setspec = "ddc:850";

    public static String setname = "Italian, Romanian, Rhaeto-Romanic literatures";

    public static String setdescription = "";

    public static SetsConfig.Set set() {
        SetsConfig.Set set = new SetsConfig.Set();
        set.setSetSpec(setspec);
        set.setSetName(setname);
        set.setSetDescription(setdescription);
        return set;
    }
}
