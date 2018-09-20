package de.qucosa.oai.provider.persistence.exceptions;

public class UpdateFailed extends Exception {
    public UpdateFailed(String s) {
        super(s);
    }

    public UpdateFailed(String s, Exception e) {
        super(s, e);
    }
}
