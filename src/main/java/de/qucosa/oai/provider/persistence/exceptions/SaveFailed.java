package de.qucosa.oai.provider.persistence.exceptions;

public class SaveFailed extends Exception {

    public SaveFailed(String s) {
        super(s);
    }

    public SaveFailed(String s, Exception e) {
        super(s, e);
    }
}
