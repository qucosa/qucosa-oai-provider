package de.qucosa.oai.provider.persistence.exceptions;

public class NotFound extends Exception {
    public NotFound(String message) {
        super(message);
    }

    public NotFound(String s, Exception e) {
        super(s, e);
    }
}
