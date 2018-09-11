package de.qucosa.oai.provider.persitence.exceptions;

import java.sql.SQLException;

public class NotFound extends Exception {
    public NotFound(String message) {
    }

    public NotFound(String s, SQLException e) {
    }
}
