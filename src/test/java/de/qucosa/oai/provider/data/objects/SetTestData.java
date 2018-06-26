package de.qucosa.oai.provider.data.objects;

import de.qucosa.oai.provider.application.config.SetConfigMapper;

public class SetTestData {
    public static String setspec = "ddc:850";

    public static String setname = "Italian, Romanian, Rhaeto-Romanic literatures";

    public static String setdescription = "";

    public static SetConfigMapper.Set set() {
        SetConfigMapper.Set set = new SetConfigMapper.Set();
        set.setSetSpec(setspec);
        set.setSetName(setname);
        set.setSetDescription(setdescription);
        return set;
    }
}
