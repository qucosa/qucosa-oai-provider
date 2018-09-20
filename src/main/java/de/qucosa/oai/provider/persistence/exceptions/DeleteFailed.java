package de.qucosa.oai.provider.persistence.exceptions;

public class DeleteFailed extends Exception {
    public DeleteFailed(String s) {
        super(s);
    }

    public DeleteFailed(String s, Exception e) {
        super(s, e);
    }
}
