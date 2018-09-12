package de.qucosa.oai.provider.persistence.exceptions;

import java.sql.SQLException;

public class NotFound extends Exception {
    public NotFound(String message) {
        super(message);
    }

    public NotFound(String s, SQLException e) {
        super(s);
    }
}
