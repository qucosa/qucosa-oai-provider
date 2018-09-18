package de.qucosa.oai.provider.persistence.exceptions;

public class UndoDeleteFailed extends Exception {

    public UndoDeleteFailed(String s) {
        super(s);
    }

    public UndoDeleteFailed(String s, Exception e) {
        super(s, e);
    }
}
