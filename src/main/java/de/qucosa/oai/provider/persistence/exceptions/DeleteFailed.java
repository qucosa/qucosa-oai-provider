package de.qucosa.oai.provider.persistence.exceptions;

import java.sql.SQLException;

public class DeleteFailed extends Exception {
    public DeleteFailed(String s) {
    }

    public DeleteFailed(String s, SQLException e) {
    }
}
